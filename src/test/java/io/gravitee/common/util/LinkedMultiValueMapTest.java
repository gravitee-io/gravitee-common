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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * @author Yann TAVERNIER (yann.tavernier at graviteesource.com)
 * @author GraviteeSource Team
 */
class LinkedMultiValueMapTest {

    Map<String, KeyObject> testObjectHolder;

    @BeforeEach
    void setUp() {
        testObjectHolder = new HashMap<>();
        testObjectHolder.put("first", new KeyObject());
        testObjectHolder.put("second", new KeyObject());
        testObjectHolder.put("third", new KeyObject());
        testObjectHolder.put("fourth", new KeyObject());
    }

    @ParameterizedTest
    @ValueSource(strings = { "first", "second", "third" })
    public void shouldContainsKeyCaseInsensitive(String keyName) {
        final LinkedMultiValueMap<KeyObject, String> map = new LinkedMultiValueMap<>();
        map.set(testObjectHolder.get("first"), "value");
        map.set(testObjectHolder.get("second"), "value");
        map.set(testObjectHolder.get("third"), "value");

        assertThat(map.containsKey(testObjectHolder.get(keyName))).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = { "first,fifth", "fifth", "fourth,first,third,second,fifth" })
    public void shouldNotContainsAllKeysCaseInsensitive(String keysName) {
        final LinkedMultiValueMap<KeyObject, String> map = new LinkedMultiValueMap<>();
        map.set(testObjectHolder.get("first"), "value");
        map.set(testObjectHolder.get("second"), "value");
        map.set(testObjectHolder.get("third"), "value");
        map.set(testObjectHolder.get("fourth"), "value");

        final List<KeyObject> toBeContained = Arrays
            .stream(keysName.split(","))
            .map(key -> testObjectHolder.get(key))
            .collect(Collectors.toList());

        assertThat(map.containsAllKeys(toBeContained)).isFalse();
    }

    @ParameterizedTest
    @ValueSource(strings = { "first,second", "first", "first,third,second", "fourth,first,third,second" })
    public void shouldContainsAllKeysCaseInsensitive(String keysName) {
        final LinkedMultiValueMap<KeyObject, String> map = new LinkedMultiValueMap<>();
        map.set(testObjectHolder.get("first"), "value");
        map.set(testObjectHolder.get("second"), "value");
        map.set(testObjectHolder.get("third"), "value");
        map.set(testObjectHolder.get("fourth"), "value");

        final List<KeyObject> toBeContained = Arrays
            .stream(keysName.split(","))
            .map(key -> testObjectHolder.get(key))
            .collect(Collectors.toList());

        assertThat(map.containsAllKeys(toBeContained)).isTrue();
    }

    @ParameterizedTest
    @EmptySource
    public void shouldContainsAllKeysCaseInsensitiveEmptyParameter(List<KeyObject> keys) {
        final LinkedMultiValueMap<KeyObject, String> map = new LinkedMultiValueMap<>();
        map.set(testObjectHolder.get("first"), "value");
        map.set(testObjectHolder.get("second"), "value");
        map.set(testObjectHolder.get("third"), "value");
        map.set(testObjectHolder.get("fourth"), "value");

        assertThat(map.containsAllKeys(keys)).isTrue();
    }

    private static class KeyObject {

        public KeyObject() {}
    }
}
