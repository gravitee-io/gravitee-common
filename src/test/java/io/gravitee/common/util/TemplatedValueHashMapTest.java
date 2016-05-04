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

import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

/**
 * @author David BRASSELY (david at gravitee.io)
 * @author GraviteeSource Team
 */
public class TemplatedValueHashMapTest {

    @Test
    public void should_returnNull() {
        Map<String, String> properties = new TemplatedValueHashMap();

        Assert.assertNull(properties.get("dummy_key"));
    }

    @Test
    public void should_returnValue() {
        Map<String, String> properties = new TemplatedValueHashMap();
        properties.put("my_key", "my_value");
        Assert.assertEquals("my_value", properties.get("my_key"));
    }

    @Test
    public void should_returnResolveSingleValue() {
        Map<String, String> properties = new TemplatedValueHashMap();
        properties.put("my_key", "my_value");
        properties.put("other_key", "{{my_key}}");
        Assert.assertEquals("my_value", properties.get("other_key"));
    }

    @Test
    public void should_returnResolveMultipleValue() {
        Map<String, String> properties = new TemplatedValueHashMap();
        properties.put("my_key", "my_value");
        properties.put("my_key2", "other_value");
        properties.put("other_key", "{{my_key}} - {{my_key2}}");
        Assert.assertEquals("my_value - other_value", properties.get("other_key"));
    }

    @Test(expected = IllegalStateException.class)
    public void should_returnResolveUnknownValue() {
        Map<String, String> properties = new TemplatedValueHashMap();
        properties.put("my_key", "my_value");
        properties.put("other_key", "{{my_key2}}");
        Assert.assertNull(properties.get("other_key"));
    }
}
