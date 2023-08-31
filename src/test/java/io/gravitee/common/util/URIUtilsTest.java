/**
 * Copyright (C) 2015 The Gravitee team (http://gravitee.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.gravitee.common.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class URIUtilsTest {

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class QueryParameters {

        @ParameterizedTest
        @MethodSource("provideParametersWithSemicolonAsSeparator")
        void should_test_query_parameters_semicolon_as_separator(String query, MultiValueMap<String, String> expectedQueryParameters) {
            final MultiValueMap<String, String> result = URIUtils.parameters(query);
            assertThat(result).isEqualTo(URIUtils.parameters(query, false)).isEqualTo(expectedQueryParameters);
        }

        @ParameterizedTest
        @MethodSource("provideParametersWithSemicolonAsNormalChar")
        void should_test_query_parameters_semicolon_as_normal_char(String query, MultiValueMap<String, String> expectedQueryParameters) {
            final MultiValueMap<String, String> result = URIUtils.parameters(query, true);
            assertThat(result).isEqualTo(expectedQueryParameters);
        }

        Stream<Arguments> provideParametersWithSemicolonAsSeparator() {
            return provideParameters(false);
        }

        Stream<Arguments> provideParametersWithSemicolonAsNormalChar() {
            return provideParameters(true);
        }

        Stream<Arguments> provideParameters(boolean semicolonAsNormalChar) {
            return Stream.of(
                Arguments.of("", new LinkedMultiValueMap<>()),
                Arguments.of("?", new LinkedMultiValueMap<>()),
                Arguments.of("?k", new LinkedMultiValueMap<>(Map.of("k", listOfNull()))),
                Arguments.of("?k&j", new LinkedMultiValueMap<>(Map.of("k", listOfNull(), "j", listOfNull()))),
                Arguments.of("?foo=bar&k", new LinkedMultiValueMap<>(Map.of("foo", List.of("bar"), "k", listOfNull()))),
                Arguments.of("?k&foo=bar", new LinkedMultiValueMap<>(Map.of("foo", List.of("bar"), "k", listOfNull()))),
                Arguments.of(
                    "?foo1=bar&k&foo=bar",
                    new LinkedMultiValueMap<>(Map.of("foo", List.of("bar"), "foo1", List.of("bar"), "k", listOfNull()))
                ),
                Arguments.of(
                    "?foo1&k=v&foo",
                    new LinkedMultiValueMap<>(Map.of("foo", listOfNull(), "k", List.of("v"), "foo1", listOfNull()))
                ),
                Arguments.of("?filter=", new LinkedMultiValueMap<>(Map.of("filter", List.of("")))),
                Arguments.of("?filter=field1", new LinkedMultiValueMap<>(Map.of("filter", List.of("field1")))),
                Arguments.of("/test?filter=field1", new LinkedMultiValueMap<>(Map.of("filter", List.of("field1")))),
                Arguments.of("/test?filter==field1", new LinkedMultiValueMap<>(Map.of("filter", List.of("=field1")))),
                Arguments.of("/test?filter=%3field1", new LinkedMultiValueMap<>(Map.of("filter", List.of("%3field1")))),
                Arguments.of("/test?filter=%20field1", new LinkedMultiValueMap<>(Map.of("filter", List.of("%20field1")))),
                Arguments.of("/test?filter=field1#123", new LinkedMultiValueMap<>(Map.of("filter", List.of("field1")))),
                Arguments.of(
                    "/test?filter=field1&date=yesterday",
                    new LinkedMultiValueMap<>(Map.of("filter", List.of("field1"), "date", List.of("yesterday")))
                ),
                Arguments.of(
                    "/test?filter=field1;date=yesterday",
                    semicolonAsNormalChar
                        ? new LinkedMultiValueMap<>(Map.of("filter", List.of("field1;date=yesterday")))
                        : new LinkedMultiValueMap<>(Map.of("filter", List.of("field1"), "date", List.of("yesterday")))
                ),
                Arguments.of(
                    "/test?filter=;date=yesterday",
                    semicolonAsNormalChar
                        ? new LinkedMultiValueMap<>(Map.of("filter", List.of(";date=yesterday")))
                        : new LinkedMultiValueMap<>(Map.of("filter", List.of(""), "date", List.of("yesterday")))
                ),
                Arguments.of(
                    "/test?filter=field1;date=yesterday&foo=bar",
                    semicolonAsNormalChar
                        ? new LinkedMultiValueMap<>(Map.of("filter", List.of("field1;date=yesterday"), "foo", List.of("bar")))
                        : new LinkedMultiValueMap<>(
                            Map.of("filter", List.of("field1"), "date", List.of("yesterday"), "foo", List.of("bar"))
                        )
                ),
                Arguments.of(
                    "/test?filter=field1;date=yesterday&foo=bar&",
                    semicolonAsNormalChar
                        ? new LinkedMultiValueMap<>(Map.of("filter", List.of("field1;date=yesterday"), "foo", List.of("bar")))
                        : new LinkedMultiValueMap<>(
                            Map.of("filter", List.of("field1"), "date", List.of("yesterday"), "foo", List.of("bar"))
                        )
                ),
                Arguments.of(
                    "/test?filter=field1;date=yesterday&foo=bar;",
                    semicolonAsNormalChar
                        ? new LinkedMultiValueMap<>(Map.of("filter", List.of("field1;date=yesterday"), "foo", List.of("bar;")))
                        : new LinkedMultiValueMap<>(
                            Map.of("filter", List.of("field1"), "date", List.of("yesterday"), "foo", List.of("bar"))
                        )
                ),
                Arguments.of("?filter=field1&filter=field2", new LinkedMultiValueMap<>(Map.of("filter", List.of("field1", "field2"))))
            );
        }

        private List<String> listOfNull() {
            final ArrayList<String> list = new ArrayList<>();
            list.add(null);
            return list;
        }
    }

    @Nested
    class IsAbsolute {

        @ParameterizedTest
        @ValueSource(strings = { "http://api.gravitee.io/echo", "https://api.gravitee.io/echo", "any://api.gravitee.io/echo" })
        public void shouldBeAbsolute(String url) {
            assertThat(URIUtils.isAbsolute(url)).isTrue();
        }

        @ParameterizedTest
        @ValueSource(strings = { "a://api.gravitee.io/echo", "api.gravitee.io/echo", "/echo" })
        public void shouldNotBeAbsolute() {
            assertThat(URIUtils.isAbsolute("a://api.gravitee.io/echo")).isFalse();
        }

        @Test
        public void shouldNotBeAbsoluteWithNull() {
            assertThat(URIUtils.isAbsolute(null)).isFalse();
        }
    }
}
