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

import io.gravitee.gateway.api.buffer.Buffer;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.FlowableTransformer;
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
                result.assertComplete().assertValueCount(1).assertValue(buffer -> buffer.equals(new ServerEvent("test message")));
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
                assertThat(values)
                    .containsExactly(new ServerEvent("event1"), new ServerEvent("2", "event2", null, null), new ServerEvent("event3"));
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
                assertThat(values).containsExactly(new ServerEvent("event1"), new ServerEvent("event2"), new ServerEvent("event3"));
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
                result.assertComplete().assertValueCount(1).assertValue(buffer -> buffer.equals(new ServerEvent("part1 part2")));
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
                result.assertComplete().assertValueCount(1).assertValue(buffer -> buffer.equals(new ServerEvent("test message")));
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
                assertThat(values)
                    .containsExactly(new ServerEvent("complete1"), new ServerEvent("complete2"), new ServerEvent("incomplete"));
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
                result.assertComplete().assertValueCount(1).assertValue(buffer -> new ServerEvent("test").equals(buffer));
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
                assertThat(result.values())
                    .containsExactly(
                        new ServerEvent("start", "{\"type\": \"start\"}", "1", null),
                        new ServerEvent("message", "{\"chunk\": \"hello world\"}", "2", null),
                        new ServerEvent("simple")
                    );
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
            result.assertComplete().assertValueCount(1).assertValue(buffer -> buffer.toString().contains("data: test\nid: 123"));
        }
    }
}
