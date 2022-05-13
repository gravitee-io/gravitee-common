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

import io.gravitee.node.api.configuration.Configuration;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.net.ProxyOptions;
import io.vertx.core.net.ProxyType;
import org.junit.jupiter.api.Test;

/**
 * @author GraviteeSource Team
 */
public class VertxProxyOptionsUtilsTest {

    @Test
    void shouldCreateFromProperties() {
        final Configuration configuration = mock(Configuration.class);
        when(configuration.getProperty(PROXY_HOST_PROPERTY)).thenReturn("localhost");
        when(configuration.getProperty(PROXY_PORT_PROPERTY)).thenReturn("3128");
        when(configuration.getProperty(PROXY_TYPE_PROPERTY)).thenReturn("HTTP");

        final ProxyOptions expectedProxyOptions = new ProxyOptions();

        final HttpClientOptions httpClientOptions = new HttpClientOptions();

        setSystemProxy(httpClientOptions, configuration);

        assertThat(httpClientOptions.getProxyOptions()).usingRecursiveComparison().isEqualTo(expectedProxyOptions);
    }

    @Test
    void shouldCreateProxyOptionsWithUserNameAndPassword() {
        final Configuration configuration = mock(Configuration.class);
        when(configuration.getProperty(PROXY_HOST_PROPERTY)).thenReturn("localhost");
        when(configuration.getProperty(PROXY_PORT_PROPERTY)).thenReturn("3128");
        when(configuration.getProperty(PROXY_TYPE_PROPERTY)).thenReturn("HTTP");
        when(configuration.getProperty(PROXY_USERNAME_PROPERTY)).thenReturn("gravitee");
        when(configuration.getProperty(PROXY_PASSWORD_PROPERTY)).thenReturn("gravitee");

        final ProxyOptions expectedProxyOptions = new ProxyOptions();
        expectedProxyOptions.setUsername("gravitee");
        expectedProxyOptions.setPassword("gravitee");

        final HttpClientOptions httpClientOptions = new HttpClientOptions();

        setSystemProxy(httpClientOptions, configuration);

        assertThat(httpClientOptions.getProxyOptions()).usingRecursiveComparison().isEqualTo(expectedProxyOptions);
    }

    @Test
    void shouldSupportSocks4() {
        final Configuration configuration = mock(Configuration.class);
        when(configuration.getProperty(PROXY_HOST_PROPERTY)).thenReturn("localhost");
        when(configuration.getProperty(PROXY_PORT_PROPERTY)).thenReturn("4145");
        when(configuration.getProperty(PROXY_TYPE_PROPERTY)).thenReturn("SOCKS4");

        final ProxyOptions expectedProxyOptions = new ProxyOptions();
        expectedProxyOptions.setPort(4145);
        expectedProxyOptions.setType(ProxyType.SOCKS4);

        final HttpClientOptions httpClientOptions = new HttpClientOptions();

        setSystemProxy(httpClientOptions, configuration);

        assertThat(httpClientOptions.getProxyOptions()).usingRecursiveComparison().isEqualTo(expectedProxyOptions);
    }

    @Test
    void shouldSupportSocks5() {
        final Configuration configuration = mock(Configuration.class);
        when(configuration.getProperty(PROXY_HOST_PROPERTY)).thenReturn("localhost");
        when(configuration.getProperty(PROXY_PORT_PROPERTY)).thenReturn("1080");
        when(configuration.getProperty(PROXY_TYPE_PROPERTY)).thenReturn("SOCKS5");

        final ProxyOptions expectedProxyOptions = new ProxyOptions();
        expectedProxyOptions.setPort(1080);
        expectedProxyOptions.setType(ProxyType.SOCKS5);

        final HttpClientOptions httpClientOptions = new HttpClientOptions();

        setSystemProxy(httpClientOptions, configuration);

        assertThat(httpClientOptions.getProxyOptions()).usingRecursiveComparison().isEqualTo(expectedProxyOptions);
    }

    @Test
    void shouldNotCreateProxyOptionsBecauseNoHost() {
        final Configuration configuration = mock(Configuration.class);
        when(configuration.getProperty(PROXY_PORT_PROPERTY)).thenReturn("4145");
        when(configuration.getProperty(PROXY_TYPE_PROPERTY)).thenReturn("SOCKS4");

        assertThatThrownBy(() -> setSystemProxy(new HttpClientOptions(), configuration))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("system.proxy.host: Proxy host may not be null");
    }

    @Test
    void shouldNotCreateProxyOptionsBecauseNoPort() {
        final Configuration configuration = mock(Configuration.class);
        when(configuration.getProperty(PROXY_HOST_PROPERTY)).thenReturn("localhost");
        when(configuration.getProperty(PROXY_TYPE_PROPERTY)).thenReturn("HTTP");

        assertThatThrownBy(() -> setSystemProxy(new HttpClientOptions(), configuration))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("system.proxy.port: Proxy port may not be null");
    }

    @Test
    void shouldNotCreateProxyOptionsBecausePortIsNotANumber() {
        final Configuration configuration = mock(Configuration.class);
        when(configuration.getProperty(PROXY_HOST_PROPERTY)).thenReturn("localhost");
        when(configuration.getProperty(PROXY_PORT_PROPERTY)).thenReturn("1O24");
        when(configuration.getProperty(PROXY_TYPE_PROPERTY)).thenReturn("HTTP");

        assertThatThrownBy(() -> setSystemProxy(new HttpClientOptions(), configuration))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("system.proxy.port: For input string: \"1O24\"");
    }

    @Test
    void shouldNotCreateProxyOptionsBecausePortIsOutOfRange() {
        final Configuration configuration = mock(Configuration.class);
        when(configuration.getProperty(PROXY_HOST_PROPERTY)).thenReturn("localhost");
        when(configuration.getProperty(PROXY_PORT_PROPERTY)).thenReturn("65536");
        when(configuration.getProperty(PROXY_TYPE_PROPERTY)).thenReturn("HTTP");

        assertThatThrownBy(() -> setSystemProxy(new HttpClientOptions(), configuration))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("system.proxy.port: Invalid proxy port 65536");
    }

    @Test
    void shouldNotCreateProxyOptionsBecauseOfUnknownType() {
        final Configuration configuration = mock(Configuration.class);
        when(configuration.getProperty(PROXY_HOST_PROPERTY)).thenReturn("localhost");
        when(configuration.getProperty(PROXY_PORT_PROPERTY)).thenReturn("70");
        when(configuration.getProperty(PROXY_TYPE_PROPERTY)).thenReturn("GOPHER");

        assertThatThrownBy(() -> setSystemProxy(new HttpClientOptions(), configuration))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("system.proxy.type: No enum constant io.vertx.core.net.ProxyType.GOPHER");
    }

    @Test
    void shouldAggregateErrorMessages() {
        final Configuration configuration = mock(Configuration.class);

        assertThatThrownBy(() -> setSystemProxy(new HttpClientOptions(), configuration))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining(
                "system.proxy.host: Proxy host may not be null, system.proxy.port: Proxy port may not be null, system.proxy.type: Name is null"
            );
    }
}
