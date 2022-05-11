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

import io.vertx.core.net.ProxyOptions;
import io.vertx.core.net.ProxyType;
import java.util.Objects;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

/**
 * @author GraviteeSource Team
 */
public class VertxProxyOptionsUtils {

    static final String PROXY_HOST_PROPERTY = "system.proxy.host";
    static final String PROXY_PORT_PROPERTY = "system.proxy.port";
    static final String PROXY_TYPE_PROPERTY = "system.proxy.type";
    static final String PROXY_USERNAME_PROPERTY = "system.proxy.username";
    static final String PROXY_PASSWORD_PROPERTY = "system.proxy.password";

    public static ProxyOptions initFromSpringContext(ApplicationContext applicationContext) {
        final Environment environment = applicationContext.getEnvironment();
        final ProxyOptions proxyOptions = new ProxyOptions();
        final StringBuilder errorMessageBuilder = new StringBuilder();

        try {
            proxyOptions.setHost(environment.getProperty(PROXY_HOST_PROPERTY));
        } catch (Exception e) {
            appendErrorMessage(errorMessageBuilder, PROXY_HOST_PROPERTY, e);
        }

        try {
            proxyOptions.setPort(parseProxyPort(environment.getProperty(PROXY_PORT_PROPERTY)));
        } catch (Exception e) {
            appendErrorMessage(errorMessageBuilder, PROXY_PORT_PROPERTY, e);
        }

        try {
            proxyOptions.setType(ProxyType.valueOf(environment.getProperty(PROXY_TYPE_PROPERTY)));
        } catch (Exception e) {
            appendErrorMessage(errorMessageBuilder, PROXY_TYPE_PROPERTY, e);
        }

        proxyOptions.setUsername(environment.getProperty(PROXY_USERNAME_PROPERTY));
        proxyOptions.setPassword(environment.getProperty(PROXY_PASSWORD_PROPERTY));

        if (errorMessageBuilder.length() > 0) {
            throw new IllegalStateException(errorMessageBuilder.toString());
        }

        return proxyOptions;
    }

    private static int parseProxyPort(String proxyPortPropertyValue) {
        return Integer.parseInt(Objects.requireNonNull(proxyPortPropertyValue, "Proxy port may not be null"));
    }

    private static void appendErrorMessage(StringBuilder messageBuilder, String property, Exception e) {
        if (messageBuilder.length() > 0) {
            messageBuilder.append(", ");
        }
        messageBuilder.append(property).append(": ").append(e.getMessage());
    }
}
