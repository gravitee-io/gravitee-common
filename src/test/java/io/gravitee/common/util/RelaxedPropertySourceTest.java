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

import java.util.Collections;

/**
 * @author Nicolas GERAUD (nicolas.geraud at graviteesource.com) 
 * @author GraviteeSource Team
 */
public class RelaxedPropertySourceTest {
    @Test
    public void shouldManageUppercase() {
        testKeyConfig("key1.key2", "key1.key2");
        testKeyConfig("key1.key2", "key1_key2");
        testKeyConfig("key1.key2", "KEY1.KEY2");
        testKeyConfig("key1.key2", "KEY1_KEY2");
    }

    @Test
    public void shouldManageHyphen() {
        testKeyConfig("key-1.key-2", "key-1.key-2");
        testKeyConfig("key-1.key-2", "key-1_key-2");
        testKeyConfig("key-1.key-2", "KEY-1.KEY-2");
        testKeyConfig("key-1.key-2", "KEY-1_KEY-2");

        testKeyConfig("key-1.key-2", "key1.key2");
        testKeyConfig("key-1.key-2", "key1_key2");
        testKeyConfig("key-1.key-2", "KEY1.KEY2");
        testKeyConfig("key-1.key-2", "KEY1_KEY2");
    }

    @Test
    public void shouldManageTable() {
        testKeyConfig("tab[0]", "tab[0]");
        testKeyConfig("tab[0]", "tab_0");
        testKeyConfig("tab[0]", "TAB[0]");
        testKeyConfig("tab[0]", "TAB_0");

        testKeyConfig("tab[0].subtab[1].value", "tab[0].subtab[1].value");
        testKeyConfig("tab[0].subtab[1].value", "tab_0_subtab_1_value");
        testKeyConfig("tab[0].subtab[1].value", "TAB[0].SUBTAB[1].VALUE");
        testKeyConfig("tab[0].subtab[1].value", "TAB_0_SUBTAB_1_VALUE");

        testKeyConfig("tab[0].sub-tab[1].value", "tab[0].sub-tab[1].value");
        testKeyConfig("tab[0].sub-tab[1].value", "tab_0_sub-tab_1_value");
        testKeyConfig("tab[0].sub-tab[1].value", "tab[0].subtab[1].value");
        testKeyConfig("tab[0].sub-tab[1].value", "tab_0_subtab_1_value");
        testKeyConfig("tab[0].sub-tab[1].value", "TAB[0].SUBTAB[1].VALUE");
        testKeyConfig("tab[0].sub-tab[1].value", "TAB_0_SUBTAB_1_VALUE");
    }

    @Test
    public void shouldManageDefaultValues() {
        testKeyConfig("email.subject:[Gravitee.io] %s", "", null);
    }

    @Test
    public void shouldNotSupportSnakeCase() {
        testKeyConfig("key1.key2", "Key1.Key2", null);
        testKeyConfig("key1.key2", "Key1_Key2", null);
    }

    private void testKeyConfig(String key, String config) {
        testKeyConfig(key, config, true);
    }
    private void testKeyConfig(String key, String config, Object expected) {
        RelaxedPropertySource test = new RelaxedPropertySource("test", Collections.singletonMap(config, true));
        Assert.assertEquals(key + "==" + config, expected, test.getProperty(key));
    }
}
