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
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

import java.util.*;
import java.util.stream.Collectors;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * @author Florent CHAMFROY (florent.chamfroy at graviteesource.com)
 * @author GraviteeSource Team
 */
public class LinkedCaseInsensitiveMultiValueMapTest {

    @Test
    public void containsAllKeyShouldBeCaseInsensitive() {
        LinkedCaseInsensitiveMultiValueMap<String> map = new LinkedCaseInsensitiveMultiValueMap<>();
        map.add("KEYUP", "k");
        map.add("keydown", "k");

        assertTrue(map.containsAllKeys(Arrays.asList("KEYUP", "keyUP", "kEyUp", "keyDown", "KEYDOWN")));
    }

    @Test
    public void containsKeyShouldBeCaseInsensitive() {
        LinkedCaseInsensitiveMultiValueMap<String> map = new LinkedCaseInsensitiveMultiValueMap<>();
        map.add("KEYUP", "k");
        map.add("keydown", "k");

        assertTrue(map.containsKey("KEYUP"));
        assertTrue(map.containsKey("keyup"));
        assertTrue(map.containsKey("KEYDOWN"));
        assertTrue(map.containsKey("keydown"));
    }

    @Test
    public void addShouldBeCaseInsensitive() {
        LinkedCaseInsensitiveMultiValueMap<String> map = new LinkedCaseInsensitiveMultiValueMap<>();
        map.add("UP", "VALUP");
        map.add("up", "valdown");

        assertEquals(Arrays.asList("VALUP", "valdown"), map.get("UP"));
        assertEquals(Arrays.asList("VALUP", "valdown"), map.get("up"));

        map.add("down", "valdown");
        map.add("DOWN", "VALUP");

        assertEquals(Arrays.asList("valdown", "VALUP"), map.get("DOWN"));
        assertEquals(Arrays.asList("valdown", "VALUP"), map.get("down"));
    }

    @Test
    public void getOrDefaultShouldBeCaseInsensitive() {
        LinkedCaseInsensitiveMultiValueMap<String> map = new LinkedCaseInsensitiveMultiValueMap<>();
        map.add("KEYUP", "k");
        map.add("keydown", "k");

        assertEquals(Arrays.asList("k"), map.getOrDefault("KEYUP", Collections.singletonList("a")));
        assertEquals(Arrays.asList("k"), map.getOrDefault("keyup", Collections.singletonList("a")));
        assertEquals(Arrays.asList("k"), map.getOrDefault("KEYDOWN", Collections.singletonList("a")));
        assertEquals(Arrays.asList("k"), map.getOrDefault("keydown", Collections.singletonList("a")));
    }

    @Test
    public void computeIfAbsentShouldBeCaseInsensitive() {
        LinkedCaseInsensitiveMultiValueMap<String> map = new LinkedCaseInsensitiveMultiValueMap<>();
        map.add("KEYUP", "k");
        map.add("keydown", "k");

        assertEquals(Arrays.asList("k"), map.computeIfAbsent("KEYUP", key -> Collections.singletonList(key)));
        assertEquals(Arrays.asList("k"), map.computeIfAbsent("keyup", key -> Collections.singletonList(key)));
        assertEquals(Arrays.asList("k"), map.computeIfAbsent("KEYDOWN", key -> Collections.singletonList(key)));
        assertEquals(Arrays.asList("k"), map.computeIfAbsent("keydown", key -> Collections.singletonList(key)));
        assertEquals(Arrays.asList("xxxxxxx"), map.computeIfAbsent("xxxxxxx", key -> Collections.singletonList(key)));
        assertEquals(Arrays.asList("xxxxxxx"), map.computeIfAbsent("XXXXXXX", key -> Collections.singletonList(key)));
        assertEquals(Arrays.asList("YYYYYYY"), map.computeIfAbsent("YYYYYYY", key -> Collections.singletonList(key)));
        assertEquals(Arrays.asList("YYYYYYY"), map.computeIfAbsent("yyyyyyy", key -> Collections.singletonList(key)));
    }

    @Test
    public void computeIfPresentShouldBeCaseInsensitive() {
        LinkedCaseInsensitiveMultiValueMap<String> map = new LinkedCaseInsensitiveMultiValueMap<>();
        map.add("KEYUP", "k");
        map.add("keydown", "k");

        assertEquals(Arrays.asList("KEYUP"), map.computeIfPresent("KEYUP", (key, v) -> Collections.singletonList(key)));
        assertEquals(Arrays.asList("keyup"), map.computeIfPresent("keyup", (key, v) -> Collections.singletonList(key)));
        assertEquals(Arrays.asList("KEYDOWN"), map.computeIfPresent("KEYDOWN", (key, v) -> Collections.singletonList(key)));
        assertEquals(Arrays.asList("keydown"), map.computeIfPresent("keydown", (key, v) -> Collections.singletonList(key)));
        assertNull(map.computeIfPresent("xxxxxxx", (key, v) -> Collections.singletonList(key)));
    }

    @Test
    public void replaceShouldBeCaseInsensitive() {
        LinkedCaseInsensitiveMultiValueMap<String> map = new LinkedCaseInsensitiveMultiValueMap<>();
        map.add("KEYUP", "k");
        map.add("keydown", "k");

        final List<String> kbis = Collections.singletonList("kbis");
        map.replace("keyup", kbis);
        map.replace("KEYDOWN", kbis);

        assertEquals(kbis, map.get("KEYUP"));
        assertEquals(kbis, map.get("keydown"));
    }
}
