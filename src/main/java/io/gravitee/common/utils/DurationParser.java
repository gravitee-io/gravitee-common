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

package io.gravitee.common.utils;

import java.time.Duration;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.AllArgsConstructor;
import org.springframework.util.StringUtils;

/**
 * @author Guillaume LAMIRAND (guillaume.lamirand at graviteesource.com)
 * @author GraviteeSource Team
 */
public class DurationParser {

    /**
     * This describes a simple duration format, are supported :
     * <li>
     *     <ul>1ms: 1 milliseconds</ul>
     *     <ul>1s: 1 seconds/ul>
     *     <ul>1m: 1 minute</ul>
     *     <ul>1h: 1 hour</ul>
     *     <ul>1d: 1 day</ul>
     * </li>
     */
    private static final String SIMPLE_FORMAT_REGEX = "^([+-]?\\d+)([a-zA-Z]{0,2})$";
    private static Pattern simpleFormatPattern;

    /**
     * Parse the given value to a duration. It will try to detect the format and create the Duration accordingly.
     *
     * @param value the value to parse
     * @return a duration
     */
    public static Duration parse(final String value) {
        Duration duration = null;
        if (value != null && value.length() > 0) {
            try {
                duration = Duration.parse(value);
            } catch (DateTimeParseException e) {
                // means value doesn't follow ISO-8601 format, try with simple format
                try {
                    Matcher matcher = getSimpleFormatPattern().matcher(value);
                    if (matcher.matches()) {
                        String suffix = matcher.group(2);
                        if (StringUtils.hasLength(suffix)) {
                            Unit unit = Unit.fromSuffix(suffix);
                            duration = unit.parse(matcher.group(1));
                        }
                    }
                } catch (Exception ex) {
                    // Ignore this, and return null value
                }
            }
        }
        return duration;
    }

    private static Pattern getSimpleFormatPattern() {
        if (simpleFormatPattern == null) {
            simpleFormatPattern = Pattern.compile(SIMPLE_FORMAT_REGEX);
        }
        return simpleFormatPattern;
    }

    /**
     * Units supported.
     */
    @AllArgsConstructor
    enum Unit {
        /**
         * Milliseconds.
         */
        MILLIS(ChronoUnit.MILLIS, "ms"),

        /**
         * Seconds.
         */
        SECONDS(ChronoUnit.SECONDS, "s"),

        /**
         * Minutes.
         */
        MINUTES(ChronoUnit.MINUTES, "m"),

        /**
         * Hours.
         */
        HOURS(ChronoUnit.HOURS, "h"),

        /**
         * Days.
         */
        DAYS(ChronoUnit.DAYS, "d");

        private final ChronoUnit chronoUnit;
        private final String suffix;

        public Duration parse(final String value) {
            return Duration.of(Long.parseLong(value), this.chronoUnit);
        }

        public static Unit fromSuffix(final String suffix) {
            for (Unit candidate : values()) {
                if (candidate.suffix.equalsIgnoreCase(suffix)) {
                    return candidate;
                }
            }
            throw new IllegalArgumentException("Unknown unit '" + suffix + "'");
        }
    }
}
