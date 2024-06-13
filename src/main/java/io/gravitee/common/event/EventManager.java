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
package io.gravitee.common.event;

/**
 * @author David BRASSELY (brasseld at gmail.com)
 */
public interface EventManager {
    <T extends Enum<T>, S> void publishEvent(final T type, final S content);

    <T extends Enum<T>, S> void publishEvent(final Event<T, S> event);

    <T extends Enum<T>> void subscribeForEvents(final EventListener<T, ?> eventListener, final Class<T> eventTypeClass);

    <T extends Enum<T>> void subscribeForEvents(final EventListener<T, ?> eventListener, final T... eventTypes);

    <T extends Enum<T>> void unsubscribeForEvents(final EventListener<T, ?> eventListener, final Class<T> eventTypesClass);

    <T extends Enum<T>> void unsubscribeForEvents(final EventListener<T, ?> eventListener, final T... eventTypes);
}
