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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * @author David BRASSELY (david.brassely at graviteesource.com)
 * @author GraviteeSource Team
 */
public class EnvironmentUtilsTest {

    @Test
    public void shouldEncodeArrayKey() {
        String key = EnvironmentUtils.encodeArrayKey("properties_0_values_1_key");
        assertEquals("properties[0].values[1].key", key);
    }

    @Test
    public void shouldEncodeArrayKey2() {
        String key = EnvironmentUtils.encodeArrayKey("properties_0_values[1].key");
        assertEquals("properties[0].values[1].key", key);
    }

    @Test
    public void shouldEncodeIndexedArrayKey() {
        String key = EnvironmentUtils.encodeIndexedArrayKey("properties[0].values[1].key");
        assertEquals("properties_0_values_1_key", key);
    }

    @Test
    public void shouldEncodeIndexedArrayKey2() {
        String key = EnvironmentUtils.encodeIndexedArrayKey("properties[0].values_1_key");
        assertEquals("properties_0_values_1_key", key);
    }

    @Test
    public void shouldMergeMap() {
        Map<String, Object> entries = new HashMap<>();

        Map<String, Object> map1 = Maps.<String, Object>builder().put("properties_0_values_1_key", "value1").build();

        EnvironmentUtils.addAll(entries, map1);

        assertFalse(entries.isEmpty());

        Map<String, Object> map2 = Maps.<String, Object>builder().put("properties[0].values_1_key", "value2").build();

        EnvironmentUtils.addAll(entries, map2);

        assertEquals(1, entries.size());

        Map<String, Object> map3 = Maps.<String, Object>builder().put("properties[0].values[1].key", "value3").build();

        EnvironmentUtils.addAll(entries, map2);

        assertEquals(1, entries.size());
        assertEquals("value1", entries.get("properties[0].values[1].key"));
    }

    @Test
    public void should_return_true_if_no_configured_tags() {
        assertTrue(EnvironmentUtils.hasMatchingTags(Optional.empty(), new HashSet<>()));
    }

    @Test
    public void should_return_true_if_configured_tags_is_null() {
        assertTrue(EnvironmentUtils.hasMatchingTags(Optional.ofNullable(null), new HashSet<>()));
    }

    @Test
    public void should_return_false_if_tag_is_included_and_excluded() {
        assertFalse(EnvironmentUtils.hasMatchingTags(Optional.of(Arrays.asList("env", "!env")), new HashSet<>(Arrays.asList("env"))));
    }

    @Test
    public void should_return_true_if_one_matching_tag_in_included_list() {
        assertTrue(EnvironmentUtils.hasMatchingTags(Optional.of(Arrays.asList("env")), new HashSet<>(Arrays.asList("env"))));
    }

    @ParameterizedTest
    @NullSource
    @EmptySource
    public void should_return_true_if_empty_matching_tag_in_excluded_list(final Set<String> tags) {
        assertTrue(EnvironmentUtils.hasMatchingTags(Optional.of(Arrays.asList("!env")), tags));
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

    @ParameterizedTest
    @NullSource
    @EmptySource
    public void should_return_false_if_empty_tags(final Set<String> tags) {
        assertFalse(EnvironmentUtils.hasMatchingTags(Optional.of(Arrays.asList("env1", "!env2")), tags));
    }
}
