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
import static org.junit.jupiter.params.provider.Arguments.arguments;

import io.gravitee.gateway.api.buffer.Buffer;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ServerEventTest {

    @ParameterizedTest
    @MethodSource("testsCases")
    void should_parse_event(String serialized, ServerEvent event) {
        Buffer input = Buffer.buffer(serialized);

        ServerEvent parsed = ServerEvent.parse(input);

        assertThat(parsed).isEqualTo(event);
        // Ensure if not modified, the buffer is the same as the input
        assertThat(parsed.toBuffer()).isEqualTo(input);
    }

    @ParameterizedTest
    @MethodSource("testsCases")
    void should_serialize_event(String serialized, ServerEvent event) {
        Buffer buffer = event.toBuffer();

        assertThat(buffer.toString()).isEqualTo(serialized);
    }

    static Stream<Arguments> testsCases() {
        return Stream.of(
            arguments("data: Hello World\n\n", new ServerEvent(null, "Hello World", null, null)),
            arguments("event: e1\ndata: Hello World\nretry: 12\n\n", new ServerEvent("e1", "Hello World", null, 12L))
        );
    }

    @Test
    void should_update_data_and_preserve_order() {
        var originalBuffer = Buffer.buffer("event: message\nid: 1\ndata: old data\n\n");
        var event = ServerEvent.parse(originalBuffer);

        var updatedEvent = event.withData("new data");

        assertThat(updatedEvent.data()).isEqualTo("new data");
        assertThat(updatedEvent.toBuffer().toString()).isEqualTo("event: message\nid: 1\ndata: new data\n\n");
    }

    @Test
    void should_update_data_on_manually_created_event() {
        var event = new ServerEvent("event", "old data", "id", 123L);
        var updatedEvent = event.withData("new data");

        assertThat(updatedEvent.data()).isEqualTo("new data");
        assertThat(updatedEvent.toBuffer().toString()).isEqualTo("event: event\ndata: new data\nid: id\nretry: 123\n\n");
    }

    @Test
    void should_return_same_instance_when_data_is_not_changed() {
        var event = ServerEvent.parse(Buffer.buffer("data: old data\n\n"));
        var updatedEvent = event.withData("old data");

        assertThat(updatedEvent).isSameAs(event);
    }

    @Test
    void should_handle_multiline_data_update() {
        var originalBuffer = Buffer.buffer("id: 1\ndata: line1\ndata: line2\n\n");
        var event = ServerEvent.parse(originalBuffer);

        var updatedEvent = event.withData("newline1\nnewline2");

        assertThat(updatedEvent.data()).isEqualTo("newline1\nnewline2");
        assertThat(updatedEvent.toBuffer().toString()).isEqualTo("id: 1\ndata: newline1\ndata: newline2\n\n");
    }

    @Test
    void should_handle_removing_data() {
        var originalBuffer = Buffer.buffer("id: 1\ndata: old data\n\n");
        var event = ServerEvent.parse(originalBuffer);

        var updatedEvent = event.withData(null);

        assertThat(updatedEvent.data()).isNull();
        assertThat(updatedEvent.toBuffer().toString()).isEqualTo("id: 1\n\n");
    }

    @Test
    void should_preserve_comments_when_updating_data() {
        var originalBuffer = Buffer.buffer(": this is a comment\nevent: message\nid: 1\n: another comment\ndata: old data\n\n");
        var event = ServerEvent.parse(originalBuffer);

        var updatedEvent = event.withData("new data");

        var expectedBuffer = ": this is a comment\nevent: message\nid: 1\n: another comment\ndata: new data\n\n";

        assertThat(updatedEvent.data()).isEqualTo("new data");
        assertThat(updatedEvent.toBuffer().toString()).isEqualTo(expectedBuffer);
    }

    @Test
    void should_add_data_to_event_without_it() {
        var originalBuffer = Buffer.buffer("id: 1\nevent: message\n\n");
        var event = ServerEvent.parse(originalBuffer);

        var updatedEvent = event.withData("new data");

        assertThat(updatedEvent.data()).isEqualTo("new data");
        assertThat(updatedEvent.toBuffer().toString()).isEqualTo("id: 1\nevent: message\ndata: new data\n\n");
    }

    @Test
    void should_replace_single_line_data_with_multi_line_data() {
        var originalBuffer = Buffer.buffer("id: 1\ndata: old data\nevent: message\n\n");
        var event = ServerEvent.parse(originalBuffer);

        var updatedEvent = event.withData("line1\nline2");

        assertThat(updatedEvent.data()).isEqualTo("line1\nline2");
        assertThat(updatedEvent.toBuffer().toString()).isEqualTo("id: 1\ndata: line1\ndata: line2\nevent: message\n\n");
    }

    @Test
    void should_replace_multi_line_data_with_single_line_data() {
        var originalBuffer = Buffer.buffer("id: 1\ndata: line1\ndata: line2\nevent: message\n\n");
        var event = ServerEvent.parse(originalBuffer);

        var updatedEvent = event.withData("new data");

        assertThat(updatedEvent.data()).isEqualTo("new data");
        assertThat(updatedEvent.toBuffer().toString()).isEqualTo("id: 1\ndata: new data\nevent: message\n\n");
    }

    @Test
    void should_update_data_with_empty_string() {
        var originalBuffer = Buffer.buffer("id: 1\ndata: old data\n\n");
        var event = ServerEvent.parse(originalBuffer);

        var updatedEvent = event.withData("");

        assertThat(updatedEvent.data()).isEqualTo("");
        assertThat(updatedEvent.toBuffer().toString()).isEqualTo("id: 1\ndata: \n\n");
    }

    @Test
    void should_update_data_with_only_newlines() {
        var originalBuffer = Buffer.buffer("id: 1\ndata: old data\n\n");
        var event = ServerEvent.parse(originalBuffer);

        var updatedEvent = event.withData("\n\n");

        assertThat(updatedEvent.data()).isEqualTo("\n\n");
        assertThat(updatedEvent.toBuffer().toString()).isEqualTo("id: 1\ndata: \ndata: \n\n");
    }

    @Test
    void should_update_data_with_newlines_and_text() {
        var originalBuffer = Buffer.buffer("id: 1\ndata: old data\n\n");
        var event = ServerEvent.parse(originalBuffer);

        var updatedEvent = event.withData("foo\n\nbar");

        assertThat(updatedEvent.data()).isEqualTo("foo\n\nbar");
        assertThat(updatedEvent.toBuffer().toString()).isEqualTo("id: 1\ndata: foo\ndata: \ndata: bar\n\n");
    }
}
