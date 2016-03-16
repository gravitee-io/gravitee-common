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

import java.util.ServiceLoader;

/**
 * @author David BRASSELY (david at gravitee.io)
 * @author GraviteeSource Team
 */
public final class ServiceLoaderHelper {

    public static <T> T loadFactory(Class<T> clazz) {
        ServiceLoader<T> factories = ServiceLoader.load(clazz);
        if (factories.iterator().hasNext()) {
            return factories.iterator().next();
        } else {
            factories = ServiceLoader.load(clazz, ServiceLoaderHelper.class.getClassLoader());
            if (factories.iterator().hasNext()) {
                return factories.iterator().next();
            } else {
                throw new IllegalStateException("Cannot find META-INF/services/" + clazz.getName() + " on classpath");
            }
        }
    }
}
