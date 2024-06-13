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
package io.gravitee.common.event.impl;

import io.gravitee.common.event.Event;
import io.gravitee.common.event.EventListener;
import io.gravitee.common.event.EventManager;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import lombok.extern.slf4j.Slf4j;

/**
 * @author David BRASSELY (brasseld at gmail.com)
 */
@Slf4j
public class EventManagerImpl implements EventManager {

    private final Map<ComparableEventType<? extends Enum<?>>, List<EventListenerSubscription<?, ?>>> listeners = new ConcurrentHashMap<>();

    public <T extends Enum<T>, S> void publishEvent(final T type, final S content) {
        this.publishEvent(new SimpleEvent<>(type, content));
    }

    public <T extends Enum<T>, S> void publishEvent(final Event<T, S> event) {
        log.debug("Publish event {} - {}", event.type(), event.content());
        List<EventListenerSubscription<?, ?>> eventTypeListeners =
            this.listeners.get(new ComparableEventType<>((Class<T>) event.type().getClass()));

        if (eventTypeListeners != null && !eventTypeListeners.isEmpty()) {
            eventTypeListeners
                .stream()
                .filter(wrapper -> wrapper.events().contains(event.type()))
                .forEach(wrapper -> ((EventListenerSubscription<T, S>) wrapper).eventListener().onEvent(event));
        }
    }

    public <T extends Enum<T>> void subscribeForEvents(EventListener<T, ?> eventListener, T... eventTypes) {
        if (eventTypes.length > 0) {
            Class<T> eventTypeClass = (Class<T>) eventTypes[0].getClass();
            EnumSet<T> eventTypesSet = EnumSet.of(eventTypes[0], eventTypes);
            addEventListener(eventListener, eventTypeClass, eventTypesSet);
        }
    }

    public <T extends Enum<T>> void subscribeForEvents(EventListener<T, ?> eventListener, Class<T> eventTypeClass) {
        addEventListener(eventListener, eventTypeClass, EnumSet.allOf(eventTypeClass));
    }

    private <T extends Enum<T>> void addEventListener(
        final EventListener<T, ?> eventListener,
        final Class<T> eventTypeClass,
        final Set<T> eventTypes
    ) {
        log.debug("Register new listener {} for event type {}", eventListener.getClass().getSimpleName(), eventTypeClass);

        this.listeners.compute(
                new ComparableEventType<>(eventTypeClass),
                (k, v) -> {
                    if (v == null) {
                        v = new CopyOnWriteArrayList<>();
                    }
                    v.add(new EventListenerSubscription<>(eventListener, eventTypes));
                    return v;
                }
            );
    }

    @Override
    public <T extends Enum<T>> void unsubscribeForEvents(final EventListener<T, ?> eventListener, final Class<T> eventTypesClass) {
        removeEventListener(eventListener, eventTypesClass, EnumSet.allOf(eventTypesClass));
    }

    @Override
    public <T extends Enum<T>> void unsubscribeForEvents(final EventListener<T, ?> eventListener, final T... eventTypes) {
        if (eventTypes.length > 0) {
            Class<T> eventTypeClass = (Class<T>) eventTypes[0].getClass();
            EnumSet<T> eventTypesSet = EnumSet.of(eventTypes[0], eventTypes);
            removeEventListener(eventListener, eventTypeClass, eventTypesSet);
        }
    }

    private <T extends Enum<T>> void removeEventListener(
        final EventListener<T, ?> eventListener,
        final Class<T> eventTypeClass,
        final Set<T> eventTypes
    ) {
        log.debug("Unregister listener {} for event type {}", eventListener.getClass().getSimpleName(), eventTypeClass);

        this.listeners.computeIfPresent(
                new ComparableEventType<>(eventTypeClass),
                (k, v) -> {
                    Set<EventListenerSubscription<?, ?>> removedEventListener = new HashSet<>();
                    v
                        .stream()
                        .filter(eventListenerSubscription -> eventListenerSubscription.eventListener.equals(eventListener))
                        .forEach(eventListenerSubscription -> {
                            eventListenerSubscription.events.removeAll(eventTypes);
                            if (eventListenerSubscription.events.isEmpty()) {
                                removedEventListener.add(eventListenerSubscription);
                            }
                        });
                    for (EventListenerSubscription<?, ?> eventListenerSubscription : removedEventListener) {
                        v.remove(eventListenerSubscription);
                    }

                    if (v.isEmpty()) {
                        return null;
                    }
                    return v;
                }
            );
    }

    private record EventListenerSubscription<T extends Enum<T>, S>(EventListener<T, S> eventListener, Set<T> events) {}

    private record ComparableEventType<T extends Enum<T>>(Class<? extends T> wrappedClass) implements Comparable<ComparableEventType<T>> {
        @Override
        public int compareTo(ComparableEventType<T> o) {
            return wrappedClass.getCanonicalName().compareTo(o.wrappedClass.getCanonicalName());
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof ComparableEventType)) {
                return false;
            }

            return compareTo((ComparableEventType<T>) o) == 0;
        }
    }
}
