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
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * @author Jeoffrey HAEYAERT (jeoffrey.haeyaert at graviteesource.com)
 * @author GraviteeSource Team
 */
class SizeUtilsTest {

    @ParameterizedTest
    @CsvSource(
        value = {
            "128B, 128",
            "128K, 131072",
            "128KB, 131072",
            "128, 134217728",
            "128M, 134217728",
            "128MB, 134217728",
            "128G, 137438953472",
            "128GB, 137438953472",
        }
    )
    void should_convert_string_value_to_integer_byte_value(String stringValue, Long expectedValue) {
        final long bytes = SizeUtils.toBytes(stringValue);
        assertThat(bytes).withFailMessage("Expected %s for %s, got %s", expectedValue, stringValue, bytes).isEqualTo(expectedValue);
    }

    @ParameterizedTest
    @ValueSource(strings = { "invalid", "128FooBar" })
    void should_throw_number_format_exception_when_invalid(String stringValue) {
        assertThrows(NumberFormatException.class, () -> SizeUtils.toBytes(stringValue));
    }

    @Test
    void should_return_minus_1_when_null() {
        assertThat(SizeUtils.toBytes(null)).isEqualTo(-1);
    }
}
