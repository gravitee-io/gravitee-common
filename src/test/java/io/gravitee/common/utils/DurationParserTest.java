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
package io.gravitee.common.utils;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

/**
 * @author Guillaume LAMIRAND (guillaume.lamirand at graviteesource.com)
 * @author GraviteeSource Team
 */
class DurationParserTest {

    private static Stream<Arguments> provideIso8601Duration() {
        return Stream.of(
            Arguments.of("PT15M", "PT15M"),
            Arguments.of("+PT15M", "PT15M"),
            Arguments.of("PT10H", "PT10H"),
            Arguments.of("P2D", "P2D"),
            Arguments.of("P2DT3H4M", "P2DT3H4M"),
            Arguments.of("-PT6H3M", "-PT6H3M"),
            Arguments.of("-PT-6H+3M", "-PT-6H+3M")
        );
    }

    @ParameterizedTest
    @MethodSource("provideIso8601Duration")
    void shouldReturnDurationWhenValueIsIso8601(final String given, final String expected) {
        assertThat(DurationParser.parse(given)).isEqualTo(Duration.parse(expected));
    }

    private static Stream<Arguments> provideSimpleDuration() {
        return Stream.of(
            // Simple duration / days / hours / minutes / sec / millis

            Arguments.of("10d", 10, 240, 14400, 864000, 864000000),
            Arguments.of("10D", 10, 240, 14400, 864000, 864000000),
            Arguments.of("+10d", 10, 240, 14400, 864000, 864000000),
            Arguments.of("-10d", -10, -240, -14400, -864000, -864000000),
            Arguments.of("10h", 0, 10, 600, 36000, 36000000),
            Arguments.of("10H", 0, 10, 600, 36000, 36000000),
            Arguments.of("+10h", 0, 10, 600, 36000, 36000000),
            Arguments.of("-10h", 0, -10, -600, -36000, -36000000),
            Arguments.of("10m", 0, 0, 10, 600, 600000),
            Arguments.of("10M", 0, 0, 10, 600, 600000),
            Arguments.of("+10m", 0, 0, 10, 600, 600000),
            Arguments.of("-10m", 0, 0, -10, -600, -600000),
            Arguments.of("10s", 0, 0, 0, 10, 10000),
            Arguments.of("10S", 0, 0, 0, 10, 10000),
            Arguments.of("+10s", 0, 0, 0, 10, 10000),
            Arguments.of("-10s", 0, 0, 0, -10, -10000),
            Arguments.of("10ms", 0, 0, 0, 0, 10),
            Arguments.of("10MS", 0, 0, 0, 0, 10),
            Arguments.of("+10ms", 0, 0, 0, 0, 10),
            Arguments.of("-10ms", 0, 0, 0, -1, -10)
        );
    }

    @ParameterizedTest
    @MethodSource("provideSimpleDuration")
    void shouldReturnDurationWhenValueIsMillis(
        final String given,
        final long expectedDays,
        final long expectedHours,
        final long expectedMinutes,
        final long expectedSeconds,
        final long expectedMillis
    ) {
        assertThat(DurationParser.parse(given))
            .hasDays(expectedDays)
            .hasHours(expectedHours)
            .hasMinutes(expectedMinutes)
            .hasSeconds(expectedSeconds)
            .hasMillis(expectedMillis);
    }

    private static Stream<Arguments> provideBadFormatSimpleDuration() {
        return Stream.of(
            Arguments.of(" "),
            Arguments.of("1 "),
            Arguments.of("1"),
            Arguments.of("1d "),
            Arguments.of("1h "),
            Arguments.of("1m "),
            Arguments.of("1s "),
            Arguments.of("1ms "),
            Arguments.of("1md"),
            Arguments.of("1pou")
        );
    }

    @ParameterizedTest
    @MethodSource("provideBadFormatSimpleDuration")
    @NullAndEmptySource
    void shouldReturnNullWhenValueHasBadFormat(final String given) {
        Duration duration = DurationParser.parse(given);
        assertThat(duration).isNull();
    }
}
