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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class URIUtilsTest {

    @Test
    public void test1() {
        MultiValueMap<String, String> parameters = URIUtils.parameters("/test?k=v");
        Assertions.assertEquals("v", parameters.get("k").get(0));
    }

    @Test
    public void test2() {
        MultiValueMap<String, String> parameters = URIUtils.parameters("?k=v");
        Assertions.assertEquals("v", parameters.get("k").get(0));
    }

    @Test
    public void test3() {
        MultiValueMap<String, String> parameters = URIUtils.parameters("");
        Assertions.assertEquals(0, parameters.size());
    }

    @Test
    public void test4() {
        MultiValueMap<String, String> parameters = URIUtils.parameters("?");
        Assertions.assertEquals(0, parameters.size());
    }

    @Test
    public void test5() {
        MultiValueMap<String, String> parameters = URIUtils.parameters("?k=");
        Assertions.assertEquals(1, parameters.size());
        Assertions.assertEquals("", parameters.get("k").get(0));
    }

    @Test
    public void test6() {
        MultiValueMap<String, String> parameters = URIUtils.parameters("/test?k=v&foo=bar");
        Assertions.assertEquals(2, parameters.size());
        Assertions.assertEquals("v", parameters.get("k").get(0));
        Assertions.assertEquals("bar", parameters.get("foo").get(0));
    }

    @Test
    public void test7() {
        MultiValueMap<String, String> parameters = URIUtils.parameters("/test?k=v&k=bar");
        Assertions.assertEquals(1, parameters.size());
        Assertions.assertEquals("v", parameters.get("k").get(0));
        Assertions.assertEquals("bar", parameters.get("k").get(1));
    }

    @Test
    public void test8() {
        MultiValueMap<String, String> parameters = URIUtils.parameters("/test?k==v");
        Assertions.assertEquals(1, parameters.size());
        Assertions.assertEquals("=v", parameters.get("k").get(0));
    }

    @Test
    public void test9() {
        MultiValueMap<String, String> parameters = URIUtils.parameters("/test?k=%3Dv");
        Assertions.assertEquals(1, parameters.size());
        Assertions.assertEquals("%3Dv", parameters.get("k").get(0));
    }

    @Test
    public void test10() {
        MultiValueMap<String, String> parameters = URIUtils.parameters("/test?k=%253Dv");
        Assertions.assertEquals(1, parameters.size());
        Assertions.assertEquals("%253Dv", parameters.get("k").get(0));
    }

    @Test
    public void test11() {
        MultiValueMap<String, String> parameters = URIUtils.parameters("/test?foo=bar#123");
        Assertions.assertEquals(1, parameters.size());
        Assertions.assertEquals("bar", parameters.get("foo").get(0));
    }

    @Test
    public void test12() {
        MultiValueMap<String, String> parameters = URIUtils.parameters("/test?k=v;foo=bar");
        Assertions.assertEquals(2, parameters.size());
        Assertions.assertEquals("v", parameters.get("k").get(0));
        Assertions.assertEquals("bar", parameters.get("foo").get(0));
    }

    @Test
    public void test13() {
        MultiValueMap<String, String> parameters = URIUtils.parameters("/test?k=v&foo=bar;a=b");
        Assertions.assertEquals(3, parameters.size());
        Assertions.assertEquals("v", parameters.get("k").get(0));
        Assertions.assertEquals("bar", parameters.get("foo").get(0));
        Assertions.assertEquals("b", parameters.get("a").get(0));
    }

    @Test
    public void test14() {
        MultiValueMap<String, String> parameters = URIUtils.parameters("/test?k=v&foo=bar;a=b&");
        Assertions.assertEquals(3, parameters.size());
        Assertions.assertEquals("v", parameters.get("k").get(0));
        Assertions.assertEquals("bar", parameters.get("foo").get(0));
        Assertions.assertEquals("b", parameters.get("a").get(0));
    }

    @Test
    public void test15() {
        MultiValueMap<String, String> parameters = URIUtils.parameters("/test?k=v&foo=bar;a=b;");
        Assertions.assertEquals(3, parameters.size());
        Assertions.assertEquals("v", parameters.get("k").get(0));
        Assertions.assertEquals("bar", parameters.get("foo").get(0));
        Assertions.assertEquals("b", parameters.get("a").get(0));
    }

    @Test
    public void test16() {
        MultiValueMap<String, String> parameters = URIUtils.parameters("/test?k=&foo=bar");
        Assertions.assertEquals(2, parameters.size());
        Assertions.assertEquals("", parameters.get("k").get(0));
        Assertions.assertEquals("bar", parameters.get("foo").get(0));
    }

    @Test
    public void test17() {
        MultiValueMap<String, String> parameters = URIUtils.parameters("?k");
        Assertions.assertEquals(1, parameters.size());
        Assertions.assertNull(parameters.get("k").get(0));
    }

    @Test
    public void test18() {
        MultiValueMap<String, String> parameters = URIUtils.parameters("?k&j");
        Assertions.assertEquals(2, parameters.size());
        Assertions.assertTrue(parameters.containsKey("k"));
        Assertions.assertNull(parameters.get("k").get(0));
        Assertions.assertTrue(parameters.containsKey("j"));
        Assertions.assertNull(parameters.get("j").get(0));
    }

    @Test
    public void test19() {
        MultiValueMap<String, String> parameters = URIUtils.parameters("?foo=bar&k");
        Assertions.assertEquals(2, parameters.size());
        Assertions.assertTrue(parameters.containsKey("k"));
        Assertions.assertNull(parameters.get("k").get(0));
        Assertions.assertTrue(parameters.containsKey("foo"));
        Assertions.assertEquals("bar", parameters.get("foo").get(0));
    }

    @Test
    public void test20() {
        MultiValueMap<String, String> parameters = URIUtils.parameters("?k&foo=bar");
        Assertions.assertEquals(2, parameters.size());
        Assertions.assertTrue(parameters.containsKey("k"));
        Assertions.assertNull(parameters.get("k").get(0));
        Assertions.assertTrue(parameters.containsKey("foo"));
        Assertions.assertEquals("bar", parameters.get("foo").get(0));
    }

    @Test
    public void test21() {
        MultiValueMap<String, String> parameters = URIUtils.parameters("?foo1=bar&k&foo=bar");
        Assertions.assertEquals(3, parameters.size());
        Assertions.assertTrue(parameters.containsKey("k"));
        Assertions.assertNull(parameters.get("k").get(0));
        Assertions.assertTrue(parameters.containsKey("foo"));
        Assertions.assertEquals("bar", parameters.get("foo").get(0));
        Assertions.assertTrue(parameters.containsKey("foo1"));
        Assertions.assertEquals("bar", parameters.get("foo1").get(0));
    }

    @Test
    public void test22() {
        MultiValueMap<String, String> parameters = URIUtils.parameters("?foo1&k=v&foo");
        Assertions.assertEquals(3, parameters.size());
        Assertions.assertTrue(parameters.containsKey("foo1"));
        Assertions.assertNull(parameters.get("foo1").get(0));
        Assertions.assertTrue(parameters.containsKey("foo"));
        Assertions.assertNull(parameters.get("foo").get(0));
        Assertions.assertTrue(parameters.containsKey("k"));
        Assertions.assertEquals("v", parameters.get("k").get(0));
    }

    @ParameterizedTest
    @ValueSource(strings = { "http://api.gravitee.io/echo", "https://api.gravitee.io/echo", "any://api.gravitee.io/echo" })
    public void shouldBeAbsolute(String url) {
        assertThat(URIUtils.isAbsolute(url)).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = { "a://api.gravitee.io/echo", "api.gravitee.io/echo", "/echo" })
    public void shouldNotBeAbsolute() {
        assertThat(URIUtils.isAbsolute("a://api.gravitee.io/echo")).isFalse();
    }

    @Test
    public void shouldNotBeAbsoluteWithNull() {
        assertThat(URIUtils.isAbsolute(null)).isFalse();
    }
}
