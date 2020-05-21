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

import org.junit.Test;

import static org.junit.Assert.*;

public class LinkedCaseInsensitiveMapTest {
    @Test
    public void containsKeyShouldBeCaseInsensitive() {
        LinkedCaseInsensitiveMap<String> map = new LinkedCaseInsensitiveMap<>();
        map.put("KEYUP", "k");
        map.put("keydown", "k");

        assertTrue(map.containsKey("KEYUP"));
        assertTrue(map.containsKey("keyup"));
        assertTrue(map.containsKey("KEYDOWN"));
        assertTrue(map.containsKey("keydown"));
    }

    @Test
    public void putIfAbsentShouldBeCaseInsensitive() {
        LinkedCaseInsensitiveMap<String> map = new LinkedCaseInsensitiveMap<>();
        map.putIfAbsent("UP", "VALUP");
        map.putIfAbsent("up", "valdown");

        assertEquals("VALUP", map.get("UP"));
        assertEquals("VALUP", map.get("up"));

        map.putIfAbsent("down", "valdown");
        map.putIfAbsent("DOWN", "VALUP");

        assertEquals("valdown", map.get("DOWN"));
        assertEquals("valdown", map.get("down"));
    }

    @Test
    public void getOrDefaultShouldBeCaseInsensitive() {
        LinkedCaseInsensitiveMap<String> map = new LinkedCaseInsensitiveMap<>();
        map.put("KEYUP", "k");
        map.put("keydown", "k");

        assertEquals("k", map.getOrDefault("KEYUP", "a"));
        assertEquals("k", map.getOrDefault("keyup", "a"));
        assertEquals("k", map.getOrDefault("KEYDOWN", "a"));
        assertEquals("k", map.getOrDefault("keydown", "a"));
    }

    @Test
    public void computeIfAbsentShouldBeCaseInsensitive() {
        LinkedCaseInsensitiveMap<String> map = new LinkedCaseInsensitiveMap<>();
        map.put("KEYUP", "k");
        map.put("keydown", "k");

        assertEquals("k", map.computeIfAbsent("KEYUP", (key) -> key));
        assertEquals("k", map.computeIfAbsent("keyup", (key) -> key));
        assertEquals("k", map.computeIfAbsent("KEYDOWN", (key) -> key));
        assertEquals("k", map.computeIfAbsent("keydown", (key) -> key));
        assertEquals("xxxxxxx", map.computeIfAbsent("xxxxxxx", (key) -> key));
        assertEquals("xxxxxxx", map.computeIfAbsent("XXXXXXX", (key) -> key));
        assertEquals("YYYYYYY", map.computeIfAbsent("YYYYYYY", (key) -> key));
        assertEquals("YYYYYYY", map.computeIfAbsent("yyyyyyy", (key) -> key));
    }

    @Test
    public void computeIfPresentShouldBeCaseInsensitive() {
        LinkedCaseInsensitiveMap<String> map = new LinkedCaseInsensitiveMap<>();
        map.put("KEYUP", "k");
        map.put("keydown", "k");

        assertEquals("KEYUP", map.computeIfPresent("KEYUP", (key, v) -> key));
        assertEquals("keyup", map.computeIfPresent("keyup", (key, v) -> key));
        assertEquals("KEYDOWN", map.computeIfPresent("KEYDOWN", (key, v) -> key));
        assertEquals("keydown", map.computeIfPresent("keydown", (key, v) -> key));
        assertNull(map.computeIfPresent("xxxxxxx", (key, v) -> key));
    }

    @Test
    public void replaceShouldBeCaseInsensitive() {
        LinkedCaseInsensitiveMap<String> map = new LinkedCaseInsensitiveMap<>();
        map.put("KEYUP", "k");
        map.put("keydown", "k");

        map.replace("keyup", "kbis");
        map.replace("KEYDOWN", "kbis");

        assertEquals("kbis", map.get("KEYUP"));
        assertEquals("kbis", map.get("keydown"));
    }
}
