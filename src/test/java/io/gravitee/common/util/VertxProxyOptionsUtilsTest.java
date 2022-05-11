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

import static io.gravitee.common.util.VertxProxyOptionsUtils.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.vertx.core.net.ProxyOptions;
import io.vertx.core.net.ProxyType;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

/**
 * @author GraviteeSource Team
 */
public class VertxProxyOptionsUtilsTest {

    @Test
    void shouldCreateProxyOptions() {
        final Environment environment = mock(Environment.class);
        when(environment.getProperty(PROXY_HOST_PROPERTY)).thenReturn("localhost");
        when(environment.getProperty(PROXY_PORT_PROPERTY)).thenReturn("3128");
        when(environment.getProperty(PROXY_TYPE_PROPERTY)).thenReturn("HTTP");

        final ApplicationContext springContext = mock(ApplicationContext.class);
        when(springContext.getEnvironment()).thenReturn(environment);

        final ProxyOptions expectedProxyOptions = new ProxyOptions();

        ProxyOptions proxyOptions = initFromSpringContext(springContext);

        assertThat(proxyOptions).usingRecursiveComparison().isEqualTo(expectedProxyOptions);
    }

    @Test
    void shouldCreateProxyOptionsWithUserNameAndPassword() {
        final Environment environment = mock(Environment.class);
        when(environment.getProperty(PROXY_HOST_PROPERTY)).thenReturn("localhost");
        when(environment.getProperty(PROXY_PORT_PROPERTY)).thenReturn("3128");
        when(environment.getProperty(PROXY_TYPE_PROPERTY)).thenReturn("HTTP");
        when(environment.getProperty(PROXY_USERNAME_PROPERTY)).thenReturn("gravitee");
        when(environment.getProperty(PROXY_PASSWORD_PROPERTY)).thenReturn("gravitee");

        final ApplicationContext springContext = mock(ApplicationContext.class);
        when(springContext.getEnvironment()).thenReturn(environment);

        final ProxyOptions expectedProxyOptions = new ProxyOptions();
        expectedProxyOptions.setUsername("gravitee");
        expectedProxyOptions.setPassword("gravitee");

        ProxyOptions proxyOptions = initFromSpringContext(springContext);

        assertThat(proxyOptions).usingRecursiveComparison().isEqualTo(expectedProxyOptions);
    }

    @Test
    void shouldSupportSocks4() {
        final Environment environment = mock(Environment.class);
        when(environment.getProperty(PROXY_HOST_PROPERTY)).thenReturn("localhost");
        when(environment.getProperty(PROXY_PORT_PROPERTY)).thenReturn("4145");
        when(environment.getProperty(PROXY_TYPE_PROPERTY)).thenReturn("SOCKS4");

        final ApplicationContext springContext = mock(ApplicationContext.class);
        when(springContext.getEnvironment()).thenReturn(environment);

        final ProxyOptions expectedProxyOptions = new ProxyOptions();
        expectedProxyOptions.setPort(4145);
        expectedProxyOptions.setType(ProxyType.SOCKS4);

        ProxyOptions proxyOptions = initFromSpringContext(springContext);

        assertThat(proxyOptions).usingRecursiveComparison().isEqualTo(expectedProxyOptions);
    }

    @Test
    void shouldSupportSocks5() {
        final Environment environment = mock(Environment.class);
        when(environment.getProperty(PROXY_HOST_PROPERTY)).thenReturn("localhost");
        when(environment.getProperty(PROXY_PORT_PROPERTY)).thenReturn("1080");
        when(environment.getProperty(PROXY_TYPE_PROPERTY)).thenReturn("SOCKS5");

        final ApplicationContext springContext = mock(ApplicationContext.class);
        when(springContext.getEnvironment()).thenReturn(environment);

        final ProxyOptions expectedProxyOptions = new ProxyOptions();
        expectedProxyOptions.setPort(1080);
        expectedProxyOptions.setType(ProxyType.SOCKS5);

        ProxyOptions proxyOptions = initFromSpringContext(springContext);

        assertThat(proxyOptions).usingRecursiveComparison().isEqualTo(expectedProxyOptions);
    }

    @Test
    void shouldNotCreateProxyOptionsBecauseNoHost() {
        final Environment environment = mock(Environment.class);
        when(environment.getProperty(PROXY_PORT_PROPERTY)).thenReturn("4145");
        when(environment.getProperty(PROXY_TYPE_PROPERTY)).thenReturn("SOCKS4");

        final ApplicationContext springContext = mock(ApplicationContext.class);
        when(springContext.getEnvironment()).thenReturn(environment);

        assertThatThrownBy(() -> initFromSpringContext(springContext))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("system.proxy.host: Proxy host may not be null");
    }

    @Test
    void shouldNotCreateProxyOptionsBecauseNoPort() {
        final Environment environment = mock(Environment.class);
        when(environment.getProperty(PROXY_HOST_PROPERTY)).thenReturn("localhost");
        when(environment.getProperty(PROXY_TYPE_PROPERTY)).thenReturn("HTTP");

        final ApplicationContext springContext = mock(ApplicationContext.class);
        when(springContext.getEnvironment()).thenReturn(environment);

        assertThatThrownBy(() -> initFromSpringContext(springContext))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("system.proxy.port: Proxy port may not be null");
    }

    @Test
    void shouldNotCreateProxyOptionsBecausePortIsNotANumber() {
        final Environment environment = mock(Environment.class);
        when(environment.getProperty(PROXY_HOST_PROPERTY)).thenReturn("localhost");
        when(environment.getProperty(PROXY_PORT_PROPERTY)).thenReturn("1O24");
        when(environment.getProperty(PROXY_TYPE_PROPERTY)).thenReturn("HTTP");

        final ApplicationContext springContext = mock(ApplicationContext.class);
        when(springContext.getEnvironment()).thenReturn(environment);

        assertThatThrownBy(() -> initFromSpringContext(springContext))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("system.proxy.port: For input string: \"1O24\"");
    }

    @Test
    void shouldNotCreateProxyOptionsBecausePortIsOutOfRange() {
        final Environment environment = mock(Environment.class);
        when(environment.getProperty(PROXY_HOST_PROPERTY)).thenReturn("localhost");
        when(environment.getProperty(PROXY_PORT_PROPERTY)).thenReturn("65536");
        when(environment.getProperty(PROXY_TYPE_PROPERTY)).thenReturn("HTTP");

        final ApplicationContext springContext = mock(ApplicationContext.class);
        when(springContext.getEnvironment()).thenReturn(environment);

        assertThatThrownBy(() -> initFromSpringContext(springContext))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("system.proxy.port: Invalid proxy port 65536");
    }

    @Test
    void shouldNotCreateProxyOptionsBecauseOfUnknownType() {
        final Environment environment = mock(Environment.class);
        when(environment.getProperty(PROXY_HOST_PROPERTY)).thenReturn("localhost");
        when(environment.getProperty(PROXY_PORT_PROPERTY)).thenReturn("70");
        when(environment.getProperty(PROXY_TYPE_PROPERTY)).thenReturn("GOPHER");

        final ApplicationContext springContext = mock(ApplicationContext.class);
        when(springContext.getEnvironment()).thenReturn(environment);

        assertThatThrownBy(() -> initFromSpringContext(springContext))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("system.proxy.type: No enum constant io.vertx.core.net.ProxyType.GOPHER");
    }

    @Test
    void shouldAggregateErrorMessages() {
        final Environment environment = mock(Environment.class);

        final ApplicationContext springContext = mock(ApplicationContext.class);
        when(springContext.getEnvironment()).thenReturn(environment);

        assertThatThrownBy(() -> initFromSpringContext(springContext))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining(
                "system.proxy.host: Proxy host may not be null, system.proxy.port: Proxy port may not be null, system.proxy.type: Name is null"
            );
    }
}
