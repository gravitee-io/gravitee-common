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
package io.gravitee.common.http;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * @author Yann TAVERNIER (yann.tavernier at graviteesource.com)
 * @author GraviteeSource Team
 */
public class HttpHeadersTest {

    @ParameterizedTest
    @ValueSource(strings = { "X-Header", "X-HeaDER", "x-header" })
    public void shouldContainsKeyCaseInsensitive(String header) {
        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("X-Header", "value");
        httpHeaders.set("X-Another-header", "value");

        assertThat(httpHeaders.containsKey(header)).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = { "X-Header,x-another-header,HOST,accept-EnCoDing", "X-HeaDER,X-Another-HEADER", "x-header" })
    public void shouldContainsAllKeysCaseInsensitive(String headers) {
        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("X-Header", "value");
        httpHeaders.set("X-Another-header", "value");
        httpHeaders.set("Host", "value");
        httpHeaders.set("accept-encoding", "value");

        assertThat(httpHeaders.containsAllKeys(asList(headers.split(",")))).isTrue();
    }

    @ParameterizedTest
    @EmptySource
    public void shouldContainsAllKeysCaseInsensitiveEmptyParameter(List<String> headers) {
        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("X-Header", "value");
        httpHeaders.set("X-Another-header", "value");
        httpHeaders.set("Host", "value");
        httpHeaders.set("accept-encoding", "value");

        assertThat(httpHeaders.containsAllKeys(headers)).isTrue();
    }
}
