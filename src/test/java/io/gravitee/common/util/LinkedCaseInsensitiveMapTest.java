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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class LinkedCaseInsensitiveMapTest {

    @Test
    public void containsKeyShouldBeCaseInsensitive() {
        LinkedCaseInsensitiveMap<String> map = new LinkedCaseInsensitiveMap<>();
        map.put("KEYUP", "k");
        map.put("keydown", "k");

        Assertions.assertTrue(map.containsKey("KEYUP"));
        Assertions.assertTrue(map.containsKey("keyup"));
        Assertions.assertTrue(map.containsKey("KEYDOWN"));
        Assertions.assertTrue(map.containsKey("keydown"));
    }

    @Test
    public void putIfAbsentShouldBeCaseInsensitive() {
        LinkedCaseInsensitiveMap<String> map = new LinkedCaseInsensitiveMap<>();
        map.putIfAbsent("UP", "VALUP");
        map.putIfAbsent("up", "valdown");

        Assertions.assertEquals("VALUP", map.get("UP"));
        Assertions.assertEquals("VALUP", map.get("up"));

        map.putIfAbsent("down", "valdown");
        map.putIfAbsent("DOWN", "VALUP");

        Assertions.assertEquals("valdown", map.get("DOWN"));
        Assertions.assertEquals("valdown", map.get("down"));
    }

    @Test
    public void getOrDefaultShouldBeCaseInsensitive() {
        LinkedCaseInsensitiveMap<String> map = new LinkedCaseInsensitiveMap<>();
        map.put("KEYUP", "k");
        map.put("keydown", "k");

        Assertions.assertEquals("k", map.getOrDefault("KEYUP", "a"));
        Assertions.assertEquals("k", map.getOrDefault("keyup", "a"));
        Assertions.assertEquals("k", map.getOrDefault("KEYDOWN", "a"));
        Assertions.assertEquals("k", map.getOrDefault("keydown", "a"));
    }

    @Test
    public void computeIfAbsentShouldBeCaseInsensitive() {
        LinkedCaseInsensitiveMap<String> map = new LinkedCaseInsensitiveMap<>();
        map.put("KEYUP", "k");
        map.put("keydown", "k");

        Assertions.assertEquals("k", map.computeIfAbsent("KEYUP", key -> key));
        Assertions.assertEquals("k", map.computeIfAbsent("keyup", key -> key));
        Assertions.assertEquals("k", map.computeIfAbsent("KEYDOWN", key -> key));
        Assertions.assertEquals("k", map.computeIfAbsent("keydown", key -> key));
        Assertions.assertEquals("xxxxxxx", map.computeIfAbsent("xxxxxxx", key -> key));
        Assertions.assertEquals("xxxxxxx", map.computeIfAbsent("XXXXXXX", key -> key));
        Assertions.assertEquals("YYYYYYY", map.computeIfAbsent("YYYYYYY", key -> key));
        Assertions.assertEquals("YYYYYYY", map.computeIfAbsent("yyyyyyy", key -> key));
    }

    @Test
    public void computeIfPresentShouldBeCaseInsensitive() {
        LinkedCaseInsensitiveMap<String> map = new LinkedCaseInsensitiveMap<>();
        map.put("KEYUP", "k");
        map.put("keydown", "k");

        Assertions.assertEquals("KEYUP", map.computeIfPresent("KEYUP", (key, v) -> key));
        Assertions.assertEquals("keyup", map.computeIfPresent("keyup", (key, v) -> key));
        Assertions.assertEquals("KEYDOWN", map.computeIfPresent("KEYDOWN", (key, v) -> key));
        Assertions.assertEquals("keydown", map.computeIfPresent("keydown", (key, v) -> key));
        Assertions.assertNull(map.computeIfPresent("xxxxxxx", (key, v) -> key));
    }

    @Test
    public void replaceShouldBeCaseInsensitive() {
        LinkedCaseInsensitiveMap<String> map = new LinkedCaseInsensitiveMap<>();
        map.put("KEYUP", "k");
        map.put("keydown", "k");

        map.replace("keyup", "kbis");
        map.replace("KEYDOWN", "kbis");

        Assertions.assertEquals("kbis", map.get("KEYUP"));
        Assertions.assertEquals("kbis", map.get("keydown"));
    }
}
