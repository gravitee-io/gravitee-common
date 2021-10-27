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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.*;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author David BRASSELY (david.brassely at graviteesource.com)
 * @author GraviteeSource Team
 */
public class EnvironmentUtilsTest {

    @Test
    public void shouldEncodeArrayKey() {
        String key = EnvironmentUtils.encodeArrayKey("properties_0_values_1_key");
        Assert.assertEquals("properties[0].values[1].key", key);
    }

    @Test
    public void shouldEncodeArrayKey2() {
        String key = EnvironmentUtils.encodeArrayKey("properties_0_values[1].key");
        Assert.assertEquals("properties[0].values[1].key", key);
    }

    @Test
    public void shouldEncodeIndexedArrayKey() {
        String key = EnvironmentUtils.encodeIndexedArrayKey("properties[0].values[1].key");
        Assert.assertEquals("properties_0_values_1_key", key);
    }

    @Test
    public void shouldEncodeIndexedArrayKey2() {
        String key = EnvironmentUtils.encodeIndexedArrayKey("properties[0].values_1_key");
        Assert.assertEquals("properties_0_values_1_key", key);
    }

    @Test
    public void shouldMergeMap() {
        Map<String, Object> entries = new HashMap<>();

        Map<String, Object> map1 = Maps.<String, Object>builder().put("properties_0_values_1_key", "value1").build();

        EnvironmentUtils.addAll(entries, map1);

        Assert.assertFalse(entries.isEmpty());

        Map<String, Object> map2 = Maps.<String, Object>builder().put("properties[0].values_1_key", "value2").build();

        EnvironmentUtils.addAll(entries, map2);

        Assert.assertEquals(1, entries.size());

        Map<String, Object> map3 = Maps.<String, Object>builder().put("properties[0].values[1].key", "value3").build();

        EnvironmentUtils.addAll(entries, map2);

        Assert.assertEquals(1, entries.size());
        Assert.assertEquals("value1", entries.get("properties[0].values[1].key"));
    }

    @Test
    public void should_return_true_if_no_configured_tags() {
        assertTrue(EnvironmentUtils.hasMatchingTags(Optional.empty(), new HashSet<>()));
    }

    @Test
    public void should_return_true_if_configured_tags_is_null() {
        assertTrue(EnvironmentUtils.hasMatchingTags(Optional.ofNullable(null), new HashSet<>()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void should_fail_if_tag_is_included_and_excluded() {
        EnvironmentUtils.hasMatchingTags(Optional.of(Arrays.asList("env", "!env")), new HashSet<>());
    }

    @Test
    public void should_return_true_if_one_matching_tag_in_included_list() {
        assertTrue(EnvironmentUtils.hasMatchingTags(Optional.of(Arrays.asList("env")), new HashSet<>(Arrays.asList("env"))));
    }

    @Test
    public void should_return_false_if_one_matching_tag_in_excluded_list() {
        assertFalse(EnvironmentUtils.hasMatchingTags(Optional.of(Arrays.asList("!env")), new HashSet<>(Arrays.asList("env"))));
    }

    @Test
    public void should_return_true_if_no_matching_tag_in_excluded_list_and_no_included_tag_list() {
        assertTrue(EnvironmentUtils.hasMatchingTags(Optional.of(Arrays.asList("!env")), new HashSet<>(Arrays.asList("anotherEnv"))));
    }

    @Test
    public void should_return_true_if_one_matching_included_tag() {
        assertTrue(EnvironmentUtils.hasMatchingTags(Optional.of(Arrays.asList("env1", "!env2")), new HashSet<>(Arrays.asList("env1"))));
    }

    @Test
    public void should_return_false_if_one_matching_excluded_tag() {
        assertFalse(EnvironmentUtils.hasMatchingTags(Optional.of(Arrays.asList("env1", "!env2")), new HashSet<>(Arrays.asList("env2"))));
    }

    @Test
    public void should_return_false_if_no_matching_tag() {
        assertFalse(EnvironmentUtils.hasMatchingTags(Optional.of(Arrays.asList("env1", "!env2")), new HashSet<>(Arrays.asList("env3"))));
    }
}
