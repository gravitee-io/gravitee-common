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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class ServerEventTest {

    @ParameterizedTest
    @MethodSource("testsCases")
    void parse(String serialized, ServerEvent event) {
        Buffer input = Buffer.buffer(serialized);

        ServerEvent parsed = ServerEvent.parse(input);

        assertThat(parsed).isEqualTo(event);
    }

    @ParameterizedTest
    @MethodSource("testsCases")
    void serialize(String serialized, ServerEvent event) {
        Buffer buffer = event.toBuffer();

        assertThat(buffer.toString()).isEqualTo(serialized);
    }

    static Stream<Arguments> testsCases() {
        return Stream.of(
            arguments("data: Hello World\n\n", new ServerEvent(null, "Hello World", null, null)),
            arguments("event: e1\ndata: Hello World\nretry: 12\n\n", new ServerEvent("e1", "Hello World", null, 12L))
        );
    }
}
