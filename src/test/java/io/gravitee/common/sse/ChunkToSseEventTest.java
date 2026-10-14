/*
 * Copyright © 2015 The Gravitee team (http://gravitee.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.gravitee.common.sse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

import io.gravitee.gateway.api.buffer.Buffer;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.FlowableTransformer;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ChunkToSseEventTest {

    @Nested
    class ChunkToEvent {

        @Nested
        class SingleEvent {

            @Test
            void should_emit_single_complete_event() {
                // Given
                String sseEvent = "data: test message\n\n";
                Flowable<Buffer> upstream = Flowable.just(Buffer.buffer(sseEvent));

                // When
                var result = ChunkToSseEvent.chunkToEvent(upstream).test();

                // Then
                result
                    .assertComplete()
                    .assertValueCount(1)
                    .assertValue(buffer -> buffer.equals(new ServerEvent("test message")));
            }

            @Test
            void should_emit_single_event_with_multiple_lines() {
                // Given
                String sseEvent = "event: message\ndata: test\nid: 123\n\n";
                Flowable<Buffer> upstream = Flowable.just(Buffer.buffer(sseEvent));

                // When
                var result = ChunkToSseEvent.chunkToEvent(upstream).test();

                // Then
                result
                    .assertComplete()
                    .assertValueCount(1)
                    .assertValue(buffer -> buffer.equals(new ServerEvent("message", "test", "123", null)));
            }
        }

        @Nested
        class MultipleEvents {

            @Test
            void should_emit_multiple_complete_events_in_single_chunk() {
                // Given
                String sseEvents = "data: event1\n\ndata: event2\n\ndata: event3\n\n";
                Flowable<Buffer> upstream = Flowable.just(Buffer.buffer(sseEvents));

                // When
                var result = ChunkToSseEvent.chunkToEvent(upstream).test();

                // Then
                result.assertComplete();
                var values = result.values();
                assertThat(values).containsExactly(new ServerEvent("event1"), new ServerEvent("event2"), new ServerEvent("event3"));
                assertThat(values.get(0).toBuffer().toString()).isEqualTo("data: event1\n\n");
            }

            @Test
            void should_emit_events_across_multiple_chunks() {
                // Given
                Flowable<Buffer> upstream = Flowable.just(
                    Buffer.buffer("data: event1\n\n"),
                    Buffer.buffer("data: event2\n\n"),
                    Buffer.buffer("data: event3\n\n")
                );

                // When
                var result = ChunkToSseEvent.chunkToEvent(upstream).test();

                // Then
                result.assertComplete();
                var values = result.values();
                assertThat(values).containsExactly(new ServerEvent("event1"), new ServerEvent("event2"), new ServerEvent("event3"));
            }

            @Test
            void should_emit_events_across_multiple_chunks_others_separators() {
                // Given
                Flowable<Buffer> upstream = Flowable.just(
                    Buffer.buffer("data: event1\r\n\n"),
                    Buffer.buffer("data: event2\revent: 2\r\r"),
                    Buffer.buffer("data: event3\n\n")
                );

                // When
                var result = ChunkToSseEvent.chunkToEvent(upstream).test();

                // Then
                result.assertComplete();
                var values = result.values();
                assertThat(values).containsExactly(
                    new ServerEvent("event1"),
                    new ServerEvent("2", "event2", null, null),
                    new ServerEvent("event3")
                );
            }

            @Test
            void should_emit_events_across_multiple_chunks_not_aligned() {
                // Given
                Flowable<Buffer> upstream = Flowable.just(
                    Buffer.buffer("data: event1\r"),
                    Buffer.buffer("\ndata: event2\n\n"),
                    Buffer.buffer("data: event3\n\n")
                );

                // When
                var result = ChunkToSseEvent.chunkToEvent(upstream).test();

                // Then
                result.assertComplete();
                var values = result.values();
                assertThat(values).containsExactly(new ServerEvent("event1\nevent2"), new ServerEvent("event3"));
            }
        }

        @Nested
        class IncompleteEvents {

            @Test
            void should_buffer_incomplete_event_and_emit_when_complete() {
                // Given
                Flowable<Buffer> upstream = Flowable.just(Buffer.buffer("data: part1"), Buffer.buffer(" part2\n\n"));

                // When
                var result = ChunkToSseEvent.chunkToEvent(upstream).test();

                // Then
                result
                    .assertComplete()
                    .assertValueCount(1)
                    .assertValue(buffer -> buffer.equals(new ServerEvent("part1 part2")));
            }

            @Test
            void should_buffer_incomplete_event_split_across_multiple_chunks() {
                // Given
                Flowable<Buffer> upstream = Flowable.just(
                    Buffer.buffer("data: "),
                    Buffer.buffer("test "),
                    Buffer.buffer("message"),
                    Buffer.buffer("\n\n")
                );

                // When
                var result = ChunkToSseEvent.chunkToEvent(upstream).test();

                // Then
                result
                    .assertComplete()
                    .assertValueCount(1)
                    .assertValue(buffer -> buffer.equals(new ServerEvent("test message")));
            }

            @Test
            void should_emit_last_incomplete_event_on_completion() {
                // Given
                String incompleteEvent = "data: incomplete event without separator";
                Flowable<Buffer> upstream = Flowable.just(Buffer.buffer(incompleteEvent));

                // When
                var result = ChunkToSseEvent.chunkToEvent(upstream).test();

                // Then
                result
                    .assertComplete()
                    .assertValueCount(1)
                    .assertValue(buffer -> buffer.equals(new ServerEvent("incomplete event without separator")));
            }

            @Test
            void should_emit_last_incomplete_event_after_complete_events() {
                // Given
                String sseData = "data: complete1\n\ndata: complete2\n\ndata: incomplete";
                Flowable<Buffer> upstream = Flowable.just(Buffer.buffer(sseData));

                // When
                var result = ChunkToSseEvent.chunkToEvent(upstream).test();

                // Then
                result.assertComplete().assertValueCount(3);
                var values = result.values();
                assertThat(values).containsExactly(
                    new ServerEvent("complete1"),
                    new ServerEvent("complete2"),
                    new ServerEvent("incomplete")
                );
            }
        }

        @Nested
        class EdgeCases {

            @Test
            void should_handle_empty_upstream() {
                // Given
                Flowable<Buffer> upstream = Flowable.empty();

                // When
                var result = ChunkToSseEvent.chunkToEvent(upstream).test();

                // Then
                result.assertComplete().assertNoValues();
            }

            @Test
            void should_handle_single_newline_separator() {
                // Given
                String sseEvent = "data: test\n";
                Flowable<Buffer> upstream = Flowable.just(Buffer.buffer(sseEvent));

                // When
                var result = ChunkToSseEvent.chunkToEvent(upstream).test();

                // Then
                result
                    .assertComplete()
                    .assertValueCount(1)
                    .assertValue(buffer -> new ServerEvent("test").equals(buffer));
            }

            @Test
            void should_handle_multiple_consecutive_separators() {
                // Given
                String sseEvents = "data: test\n\n\n\ndata: next\n\n";
                Flowable<Buffer> upstream = Flowable.just(Buffer.buffer(sseEvents));

                // When
                var result = ChunkToSseEvent.chunkToEvent(upstream).test();

                // Then
                result.assertComplete().assertValueCount(3);
                assertThat(result.values()).containsExactly(new ServerEvent("test"), new ServerEvent(null), new ServerEvent("next"));
            }

            @Test
            void should_handle_event_separator_split_across_chunks() {
                // Given
                Flowable<Buffer> upstream = Flowable.just(Buffer.buffer("data: test\n"), Buffer.buffer("\ndata: next\n\n"));

                // When
                var result = ChunkToSseEvent.chunkToEvent(upstream).test();

                // Then
                result.assertComplete().assertValueCount(2);
                assertThat(result.values()).containsExactly(new ServerEvent("test"), new ServerEvent("next"));
            }

            @Test
            void should_handle_complex_sse_stream() {
                // Given
                Flowable<Buffer> upstream = Flowable.just(
                    Buffer.buffer("event: start\ndata: {\"type\": \"start\"}\nid: 1\n\n"),
                    Buffer.buffer("event: message\ndata: {\"chunk\": \"hello"),
                    Buffer.buffer(" world\"}\nid: 2\n\ndata: simple\n\n")
                );

                // When
                var result = ChunkToSseEvent.chunkToEvent(upstream).test();

                // Then
                result.assertComplete().assertValueCount(3);
                assertThat(result.values()).containsExactly(
                    new ServerEvent("start", "{\"type\": \"start\"}", "1", null),
                    new ServerEvent("message", "{\"chunk\": \"hello world\"}", "2", null),
                    new ServerEvent("simple")
                );
            }
        }

        @Nested
        class LineEndings {

            @Test
            void should_emit_one_event_for_crlf_terminated_lines() {
                // Given
                String sseEvent = "event: message\r\ndata: {\"id\":1}\r\nid: 7\r\n\r\n";
                Flowable<Buffer> upstream = Flowable.just(Buffer.buffer(sseEvent));

                // When
                var result = ChunkToSseEvent.chunkToEvent(upstream).test();

                // Then
                result.assertComplete();
                assertThat(result.values()).containsExactly(new ServerEvent("message", "{\"id\":1}", "7", null));
                assertThat(result.values().get(0).toBuffer().toString()).isEqualTo(sseEvent);
            }

            @Test
            void should_join_multi_line_data_terminated_by_crlf() {
                // Given
                Flowable<Buffer> upstream = Flowable.just(Buffer.buffer("data: line1\r\ndata: line2\r\n\r\n"));

                // When
                var result = ChunkToSseEvent.chunkToEvent(upstream).test();

                // Then
                result.assertComplete();
                assertThat(result.values()).containsExactly(new ServerEvent("line1\nline2"));
            }

            @Test
            void should_handle_crlf_event_separator_split_across_chunks() {
                // Given
                Flowable<Buffer> upstream = Flowable.just(Buffer.buffer("data: event1\r\n\r"), Buffer.buffer("\ndata: event2\r\n\r\n"));

                // When
                var result = ChunkToSseEvent.chunkToEvent(upstream).test();

                // Then
                result.assertComplete();
                assertThat(result.values()).containsExactly(new ServerEvent("event1"), new ServerEvent("event2"));
                assertThat(result.values().get(0).toBuffer().toString()).isEqualTo("data: event1\r\n\r\n");
                assertThat(result.values().get(1).toBuffer().toString()).isEqualTo("data: event2\r\n\r\n");
            }

            @Test
            void should_handle_cr_only_event_separator_split_across_chunks() {
                // Given
                Flowable<Buffer> upstream = Flowable.just(Buffer.buffer("data: a\r"), Buffer.buffer("\rdata: b\r\r"));

                // When
                var result = ChunkToSseEvent.chunkToEvent(upstream).test();

                // Then
                result.assertComplete();
                assertThat(result.values()).containsExactly(new ServerEvent("a"), new ServerEvent("b"));
                assertThat(result.values().get(0).toBuffer().toString()).isEqualTo("data: a\r\r");
                assertThat(result.values().get(1).toBuffer().toString()).isEqualTo("data: b\r\r");
            }

            @Test
            void should_end_event_after_the_whole_crlf_when_lf_is_followed_by_crlf_split_across_chunks() {
                // Given
                Flowable<Buffer> upstream = Flowable.just(Buffer.buffer("data: a\n\r"), Buffer.buffer("\ndata: b\n\n"));

                // When
                var result = ChunkToSseEvent.chunkToEvent(upstream).test();

                // Then
                result.assertComplete();
                assertThat(result.values()).containsExactly(new ServerEvent("a"), new ServerEvent("b"));
                assertThat(result.values().get(0).toBuffer().toString()).isEqualTo("data: a\n\r\n");
                assertThat(result.values().get(1).toBuffer().toString()).isEqualTo("data: b\n\n");
            }
        }

        @Nested
        class MultiByteCharacters {

            @Test
            void should_keep_a_multi_byte_character_split_across_chunks() {
                // Given
                byte[] stream = "data: first\n\ndata: price 10€\n\n".getBytes(StandardCharsets.UTF_8);
                int insideEuroSign = "data: first\n\ndata: price 10".getBytes(StandardCharsets.UTF_8).length + 1;
                Flowable<Buffer> upstream = Flowable.just(
                    Buffer.buffer(Arrays.copyOfRange(stream, 0, insideEuroSign)),
                    Buffer.buffer(Arrays.copyOfRange(stream, insideEuroSign, stream.length))
                );

                // When
                var result = ChunkToSseEvent.chunkToEvent(upstream).test();

                // Then
                result.assertComplete();
                assertThat(result.values()).containsExactly(new ServerEvent("first"), new ServerEvent("price 10€"));
                var forwarded = new ByteArrayOutputStream();
                result.values().forEach(event -> forwarded.writeBytes(event.toBuffer().getBytes()));
                assertThat(forwarded.toByteArray()).isEqualTo(stream);
            }
        }

        @Nested
        class LargeEvents {

            @Test
            void should_frame_a_large_event_received_in_many_small_chunks_in_linear_time() {
                // Given
                String data = "x".repeat(2 * 1024 * 1024);
                byte[] stream = ("event: message\ndata: " + data + "\n\n").getBytes(StandardCharsets.UTF_8);
                List<Buffer> chunks = new ArrayList<>();
                for (int offset = 0; offset < stream.length; offset += 512) {
                    chunks.add(Buffer.buffer(Arrays.copyOfRange(stream, offset, Math.min(stream.length, offset + 512))));
                }
                Flowable<Buffer> upstream = Flowable.fromIterable(chunks);

                // When
                List<ServerEvent> events = assertTimeoutPreemptively(Duration.ofSeconds(10), () ->
                    ChunkToSseEvent.chunkToEvent(upstream).toList().blockingGet()
                );

                // Then
                assertThat(events).containsExactly(new ServerEvent("message", data, null, null));
            }
        }

        @Nested
        class ErrorHandling {

            @Test
            void should_propagate_upstream_error() {
                // Given
                RuntimeException exception = new RuntimeException("Test error");
                Flowable<Buffer> upstream = Flowable.error(exception);

                // When
                var result = ChunkToSseEvent.chunkToEvent(upstream).test();

                // Then
                result.assertError(RuntimeException.class).assertError(throwable -> throwable.getMessage().equals("Test error"));
            }
        }
    }

    @Nested
    class Split {

        @Test
        void should_split_on_the_first_blank_line() {
            var parts = ChunkToSseEvent.split(Buffer.buffer("data: a\n\ndata: b\n\n"));

            assertThat(parts).extracting(Buffer::toString).containsExactly("data: a", "data: b\n\n");
        }

        @Test
        void should_return_the_buffer_itself_when_there_is_no_blank_line() {
            Buffer buffer = Buffer.buffer("data: a\ndata: b\n");

            assertThat(ChunkToSseEvent.split(buffer)).containsExactly(buffer);
        }

        @Test
        void should_return_an_empty_first_part_when_the_buffer_starts_with_a_blank_line() {
            var parts = ChunkToSseEvent.split(Buffer.buffer("\n\ndata: a\n\n"));

            assertThat(parts).extracting(Buffer::toString).containsExactly("", "data: a\n\n");
        }

        @Test
        void should_return_an_empty_second_part_when_the_buffer_ends_with_the_blank_line() {
            var parts = ChunkToSseEvent.split(Buffer.buffer("data: a\n\n"));

            assertThat(parts).extracting(Buffer::toString).containsExactly("data: a", "");
        }

        @Test
        void should_split_on_cr_only_line_endings() {
            var parts = ChunkToSseEvent.split(Buffer.buffer("data: a\r\rdata: b\r\r"));

            assertThat(parts).extracting(Buffer::toString).containsExactly("data: a", "data: b\r\r");
        }

        @Test
        void should_not_split_on_a_single_crlf() {
            var parts = ChunkToSseEvent.split(Buffer.buffer("data: a\r\ndata: b\r\n\r\ndata: c\r\n\r\n"));

            assertThat(parts).extracting(Buffer::toString).containsExactly("data: a\r\ndata: b", "data: c\r\n\r\n");
        }

        @Test
        void should_return_the_bytes_unchanged() {
            byte[] first = { 'd', 'a', 't', 'a', ':', ' ', (byte) 0xC3, 'x' };
            byte[] second = { 'd', 'a', 't', 'a', ':', ' ', (byte) 0xFF, '\n', '\n' };
            var input = new ByteArrayOutputStream();
            input.writeBytes(first);
            input.writeBytes(new byte[] { '\n', '\n' });
            input.writeBytes(second);

            var parts = ChunkToSseEvent.split(Buffer.buffer(input.toByteArray()));

            assertThat(parts).hasSize(2);
            assertThat(parts.get(0).getBytes()).isEqualTo(first);
            assertThat(parts.get(1).getBytes()).isEqualTo(second);
        }
    }

    @Nested
    class OnServerEvent {

        @Test
        void should_transform_server_event_and_convert_back_to_buffer() {
            // Given
            Flowable<Buffer> upstream = Flowable.just(Buffer.buffer("data: event1\n\n"), Buffer.buffer("data: event2\n\n"));
            FlowableTransformer<ServerEvent, ServerEvent> transformer = upstreamEvents ->
                upstreamEvents.map(event -> event.withData(event.data() + " transformed"));

            // When
            var result = upstream.compose(ChunkToSseEvent.onServerEvent(transformer)).test();

            // Then
            result
                .assertComplete()
                .assertValueCount(2)
                .assertValueAt(0, buffer -> buffer.toString().contains("event1 transformed"))
                .assertValueAt(1, buffer -> buffer.toString().contains("event2 transformed"));
        }

        @Test
        void should_not_modify_events_with_identity_transformer() {
            // Given
            String sseEvent = "data: test\nid: 123\n\n";
            Flowable<Buffer> upstream = Flowable.just(Buffer.buffer(sseEvent));
            FlowableTransformer<ServerEvent, ServerEvent> transformer = upstreamEvents -> upstreamEvents;

            // When
            var result = upstream.compose(ChunkToSseEvent.onServerEvent(transformer)).test();

            // Then
            result
                .assertComplete()
                .assertValueCount(1)
                .assertValue(buffer -> buffer.toString().contains("data: test\nid: 123"));
        }
    }
}
