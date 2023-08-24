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
package io.gravitee.common.component;

/**
 * @author David BRASSELY (brasseld at gmail.com)
 */
public interface LifecycleComponent<T> {
    Lifecycle.State lifecycleState();

    default T preStart() throws Exception {
        return (T) this;
    }

    T start() throws Exception;

    default T postStart() throws Exception {
        return (T) this;
    }

    default T preStop() throws Exception {
        return (T) this;
    }

    T stop() throws Exception;

    default T postStop() throws Exception {
        return (T) this;
    }
}
