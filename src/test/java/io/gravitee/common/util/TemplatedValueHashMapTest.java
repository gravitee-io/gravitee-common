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

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author David BRASSELY (david at gravitee.io)
 * @author GraviteeSource Team
 */
public class TemplatedValueHashMapTest {

    @Test
    public void should_returnNull() {
        Map<String, String> properties = new TemplatedValueHashMap();

        Assertions.assertNull(properties.get("dummy_key"));
    }

    @Test
    public void should_returnValue() {
        Map<String, String> properties = new TemplatedValueHashMap();
        properties.put("my_key", "my_value");
        Assertions.assertEquals("my_value", properties.get("my_key"));
    }

    @Test
    public void should_returnResolveSingleValue() {
        Map<String, String> properties = new TemplatedValueHashMap();
        properties.put("my_key", "my_value");
        properties.put("other_key", "{{my_key}}");
        Assertions.assertEquals("my_value", properties.get("other_key"));
    }

    @Test
    public void should_returnResolveMultipleValue() {
        Map<String, String> properties = new TemplatedValueHashMap();
        properties.put("my_key", "my_value");
        properties.put("my_key2", "other_value");
        properties.put("other_key", "{{my_key}} - {{my_key2}}");
        Assertions.assertEquals("my_value - other_value", properties.get("other_key"));
    }

    @Test
    public void should_returnResolveUnknownValue() {
        Map<String, String> properties = new TemplatedValueHashMap();
        properties.put("my_key", "my_value");
        properties.put("other_key", "{{my_key2}}");
        assertThrows(IllegalStateException.class, () -> properties.get("other_key"));
    }
}
