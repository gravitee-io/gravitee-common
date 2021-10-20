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

import java.util.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

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

        Assertions.assertTrue(map.containsAllKeys(Arrays.asList("KEYUP", "keyUP", "kEyUp", "keyDown", "KEYDOWN")));
    }

    @Test
    public void containsKeyShouldBeCaseInsensitive() {
        LinkedCaseInsensitiveMultiValueMap<String> map = new LinkedCaseInsensitiveMultiValueMap<>();
        map.add("KEYUP", "k");
        map.add("keydown", "k");

        Assertions.assertTrue(map.containsKey("KEYUP"));
        Assertions.assertTrue(map.containsKey("keyup"));
        Assertions.assertTrue(map.containsKey("KEYDOWN"));
        Assertions.assertTrue(map.containsKey("keydown"));
    }

    @Test
    public void addShouldBeCaseInsensitive() {
        LinkedCaseInsensitiveMultiValueMap<String> map = new LinkedCaseInsensitiveMultiValueMap<>();
        map.add("UP", "VALUP");
        map.add("up", "valdown");

        Assertions.assertEquals(Arrays.asList("VALUP", "valdown"), map.get("UP"));
        Assertions.assertEquals(Arrays.asList("VALUP", "valdown"), map.get("up"));

        map.add("down", "valdown");
        map.add("DOWN", "VALUP");

        Assertions.assertEquals(Arrays.asList("valdown", "VALUP"), map.get("DOWN"));
        Assertions.assertEquals(Arrays.asList("valdown", "VALUP"), map.get("down"));
    }

    @Test
    public void getOrDefaultShouldBeCaseInsensitive() {
        LinkedCaseInsensitiveMultiValueMap<String> map = new LinkedCaseInsensitiveMultiValueMap<>();
        map.add("KEYUP", "k");
        map.add("keydown", "k");

        Assertions.assertEquals(Arrays.asList("k"), map.getOrDefault("KEYUP", Collections.singletonList("a")));
        Assertions.assertEquals(Arrays.asList("k"), map.getOrDefault("keyup", Collections.singletonList("a")));
        Assertions.assertEquals(Arrays.asList("k"), map.getOrDefault("KEYDOWN", Collections.singletonList("a")));
        Assertions.assertEquals(Arrays.asList("k"), map.getOrDefault("keydown", Collections.singletonList("a")));
    }

    @Test
    public void computeIfAbsentShouldBeCaseInsensitive() {
        LinkedCaseInsensitiveMultiValueMap<String> map = new LinkedCaseInsensitiveMultiValueMap<>();
        map.add("KEYUP", "k");
        map.add("keydown", "k");

        Assertions.assertEquals(Arrays.asList("k"), map.computeIfAbsent("KEYUP", key -> Collections.singletonList(key)));
        Assertions.assertEquals(Arrays.asList("k"), map.computeIfAbsent("keyup", key -> Collections.singletonList(key)));
        Assertions.assertEquals(Arrays.asList("k"), map.computeIfAbsent("KEYDOWN", key -> Collections.singletonList(key)));
        Assertions.assertEquals(Arrays.asList("k"), map.computeIfAbsent("keydown", key -> Collections.singletonList(key)));
        Assertions.assertEquals(Arrays.asList("xxxxxxx"), map.computeIfAbsent("xxxxxxx", key -> Collections.singletonList(key)));
        Assertions.assertEquals(Arrays.asList("xxxxxxx"), map.computeIfAbsent("XXXXXXX", key -> Collections.singletonList(key)));
        Assertions.assertEquals(Arrays.asList("YYYYYYY"), map.computeIfAbsent("YYYYYYY", key -> Collections.singletonList(key)));
        Assertions.assertEquals(Arrays.asList("YYYYYYY"), map.computeIfAbsent("yyyyyyy", key -> Collections.singletonList(key)));
    }

    @Test
    public void computeIfPresentShouldBeCaseInsensitive() {
        LinkedCaseInsensitiveMultiValueMap<String> map = new LinkedCaseInsensitiveMultiValueMap<>();
        map.add("KEYUP", "k");
        map.add("keydown", "k");

        Assertions.assertEquals(Arrays.asList("KEYUP"), map.computeIfPresent("KEYUP", (key, v) -> Collections.singletonList(key)));
        Assertions.assertEquals(Arrays.asList("keyup"), map.computeIfPresent("keyup", (key, v) -> Collections.singletonList(key)));
        Assertions.assertEquals(Arrays.asList("KEYDOWN"), map.computeIfPresent("KEYDOWN", (key, v) -> Collections.singletonList(key)));
        Assertions.assertEquals(Arrays.asList("keydown"), map.computeIfPresent("keydown", (key, v) -> Collections.singletonList(key)));
        Assertions.assertNull(map.computeIfPresent("xxxxxxx", (key, v) -> Collections.singletonList(key)));
    }

    @Test
    public void replaceShouldBeCaseInsensitive() {
        LinkedCaseInsensitiveMultiValueMap<String> map = new LinkedCaseInsensitiveMultiValueMap<>();
        map.add("KEYUP", "k");
        map.add("keydown", "k");

        final List<String> kbis = Collections.singletonList("kbis");
        map.replace("keyup", kbis);
        map.replace("KEYDOWN", kbis);

        Assertions.assertEquals(kbis, map.get("KEYUP"));
        Assertions.assertEquals(kbis, map.get("keydown"));
    }
}
