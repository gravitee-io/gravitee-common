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
package io.gravitee.common.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * @author Yann TAVERNIER (yann.tavernier at graviteesource.com)
 * @author GraviteeSource Team
 */
class ListUtilsTest {

    @Test
    void should_fetch_null_attribute_as_list() {
        assertThat(ListUtils.toList(null)).isNull();
    }

    static Stream<Arguments> listableAttribute() {
        return Stream.of(
            Arguments.arguments("a,b,c", List.of("a", "b", "c")),
            Arguments.arguments("a , b, c ", List.of("a", "b", "c")),
            Arguments.arguments(" a , b, c ", List.of("a", "b", "c")),
            Arguments.arguments("a   ,   b,    c    ", List.of("a", "b", "c")),
            Arguments.arguments("a\t,\tb,\tc\t", List.of("a", "b", "c")),
            Arguments.arguments("a\t\t,\t\tb,\t\tc\t\t", List.of("a", "b", "c")),
            Arguments.arguments("a b c", List.of("a b c")),
            Arguments.arguments(" a b c ", List.of("a b c")),
            Arguments.arguments("[\"a\", \"b\", \"c\"]", List.of("a", "b", "c")),
            Arguments.arguments("[\"a\", {}, \"c\"]", List.of("a", "{}", "c")),
            Arguments.arguments("[\"a\", 1, \"c\"]", List.of("a", "1", "c")),
            Arguments.arguments("     [\"a\", 1, \"c\"]       ", List.of("a", "1", "c")),
            Arguments.arguments(
                "[\"a\", 123456789123456789123456789123456789, \"c\"]",
                List.of("a", "123456789123456789123456789123456789", "c")
            ),
            Arguments.arguments("[\"a\", 123456789123456789.123456789123456789, \"c\"]", List.of("a", "1.23456789123456784E17", "c")),
            Arguments.arguments("[\"a\", true, \"c\"]", List.of("a", "true", "c")),
            Arguments.arguments(List.of("a", "b", "c"), List.of("a", "b", "c")),
            Arguments.arguments(new ArrayList<>(List.of(1, 2, 3)), List.of(1, 2, 3)),
            Arguments.arguments(Collections.emptyList(), Collections.emptyList()),
            Arguments.arguments(new Object[] { "a", "b", "c", 1 }, List.of("a", "b", "c", 1)),
            Arguments.arguments(1, List.of(1))
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("listableAttribute")
    void should_fetch_attribute_as_list(Object attributeValue, List<Object> expectedList) {
        List<Object> result = ListUtils.toList(attributeValue);
        assertThat(result).containsAll(expectedList).isNotSameAs(expectedList);
        assertThatCode(() -> result.add(new Object())).isOfAnyClassIn(UnsupportedOperationException.class);
    }
}
