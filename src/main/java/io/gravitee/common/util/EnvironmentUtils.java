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

import java.text.Collator;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.CompositePropertySource;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.PropertySource;

/**
 * @author David BRASSELY (david.brassely at graviteesource.com)
 * @author GraviteeSource Team
 */
public final class EnvironmentUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(EnvironmentUtils.class);

    public static Map<String, Object> getPropertiesStartingWith(ConfigurableEnvironment aEnv, String aKeyPrefix) {
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> map = getAllProperties(aEnv);

        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();

            if (encodedKey(key).startsWith(encodedKey(aKeyPrefix))) {
                result.put(key, entry.getValue());
            }
        }

        return result;
    }

    public static Object get(String key, Map<String, Object> map) {
        if (map == null || map.isEmpty()) {
            return null;
        }

        return map.get(encodedKey(key));
    }

    public static String encodedKey(String key) {
        return key.replaceAll("\\.", "_").replaceAll("\\[", "_").replaceAll("\\]", "").toUpperCase();
    }

    public static Map<String, Object> getAllProperties(ConfigurableEnvironment aEnv) {
        Map<String, Object> result = new HashMap<>();
        aEnv.getPropertySources().forEach(ps -> addAll(result, getAllProperties(ps)));
        return result;
    }

    public static Map<String, Object> getAllProperties(PropertySource<?> aPropSource) {
        Map<String, Object> result = new HashMap<>();

        if (aPropSource instanceof CompositePropertySource) {
            CompositePropertySource cps = (CompositePropertySource) aPropSource;
            cps.getPropertySources().forEach(ps -> addAll(result, getAllProperties(ps)));
            return result;
        }

        if (aPropSource instanceof EnumerablePropertySource<?>) {
            EnumerablePropertySource<?> ps = (EnumerablePropertySource<?>) aPropSource;
            Arrays.asList(ps.getPropertyNames()).forEach(key -> result.put(key, ps.getProperty(key)));
            return result;
        }

        // note: Most descendants of PropertySource are EnumerablePropertySource. There are some
        // few others like JndiPropertySource or StubPropertySource
        LOGGER.debug("Given PropertySource is instanceof " + aPropSource.getClass().getName() + " and cannot be iterated");

        return result;
    }

    public static boolean hasMatchingTags(Optional<List<String>> configuredTags, Set<String> tags) {
        if (configuredTags.isPresent()) {
            List<String> tagList = configuredTags.get();

            final List<String> inclusionTags = tagList.stream().map(String::trim).filter(tag -> !tag.startsWith("!")).toList();

            final List<String> exclusionTags = tagList
                .stream()
                .map(String::trim)
                .filter(tag -> tag.startsWith("!"))
                .map(tag -> tag.substring(1))
                .toList();

            return (
                (inclusionTags.isEmpty() || tagsContains(inclusionTags, tags)) &&
                (exclusionTags.isEmpty() || !tagsContains(exclusionTags, tags))
            );
        }

        // no tags configured on this gateway instance
        return true;
    }

    static void addAll(Map<String, Object> aBase, Map<String, Object> aToBeAdded) {
        for (Map.Entry<String, Object> entry : aToBeAdded.entrySet()) {
            // Precedence is managed by the order of property sources, so we should skip if the key is already present
            // without taking care of the key's format
            if (aBase.containsKey(encodeArrayKey(entry.getKey())) || aBase.containsKey(encodeIndexedArrayKey(entry.getKey()))) {
                continue;
            }

            aBase.put(encodeArrayKey(entry.getKey()), entry.getValue());
        }
    }

    static String encodeArrayKey(String key) {
        return key.replaceAll("_([0-9]+)_", "[$1]\\.").replaceAll("_", ".");
    }

    static String encodeIndexedArrayKey(String key) {
        return key.replaceAll("\\[([0-9]+)\\].", "_$1_").replaceAll("\\.", "_");
    }

    private static boolean tagsContains(Collection<String> tags, Collection<String> searchedTags) {
        if (searchedTags == null || searchedTags.isEmpty()) {
            return false;
        }
        return tags
            .stream()
            .anyMatch(tag ->
                searchedTags
                    .stream()
                    .anyMatch(crtTag -> {
                        final Collator collator = Collator.getInstance();
                        collator.setStrength(Collator.NO_DECOMPOSITION);
                        return collator.compare(tag, crtTag) == 0;
                    })
            );
    }
}
