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
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author David BRASSELY (david at gravitee.io)
 * @author GraviteeSource Team
 */
class TemplatedValueHashMapTest {

    @Test
    public void should_return_null() {
        Map<String, String> properties = new TemplatedValueHashMap();

        assertThat(properties.get("dummy_key")).isNull();
    }

    @Test
    public void should_return_value() {
        Map<String, String> properties = new TemplatedValueHashMap();
        properties.put("my_key", "my_value");
        assertThat(properties.get("my_key")).isEqualTo("my_value");
    }

    @Test
    public void should_return_resolve_single_value() {
        Map<String, String> properties = new TemplatedValueHashMap();
        properties.put("my_key", "my_value");
        properties.put("other_key", "{{my_key}}");
        assertThat(properties.get("other_key")).isEqualTo("my_value");
    }

    @Test
    public void should_return_resolve_multiple_value() {
        Map<String, String> properties = new TemplatedValueHashMap();
        properties.put("my_key", "my_value");
        properties.put("my_key2", "other_value");
        properties.put("other_key", "{{my_key}} - {{my_key2}}");
        assertThat(properties.get("other_key")).isEqualTo("my_value - other_value");
    }

    @Test
    public void should_return_resolve_unknown_value() {
        Map<String, String> properties = new TemplatedValueHashMap();
        properties.put("my_key", "my_value");
        properties.put("other_key", "{{my_key2}}");
        assertThrows(IllegalStateException.class, () -> properties.get("other_key"));
    }

    @Test
    public void should_return_resolve_value_with_list_key() {
        Map<String, String> properties = new TemplatedValueHashMap();
        properties.put("my_key", "my_value");
        properties.put("my_key2", "my_value2");
        properties.put("my_key3", "my_value3");

        assertThat(properties.get(List.of("my_key2"))).isEqualTo("my_value2");
    }
}
