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

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import io.gravitee.common.event.EventListener;
import io.gravitee.common.event.SampleEventType;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * @author Guillaume LAMIRAND (guillaume.lamirand at graviteesource.com)
 * @author GraviteeSource Team
 */
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class EventManagerImplTest {

    private EventManagerImpl cut;

    @BeforeEach
    public void beforeEach() {
        cut = new EventManagerImpl();
    }

    @Nested
    class SubscribeEventsTest {

        @Test
        void should_receive_any_event_when_subscribing_to_event_type_class() {
            Set<String> contentReceived = new HashSet<>();
            cut.subscribeForEvents(
                (EventListener<SampleEventType, String>) event -> contentReceived.add(event.content()),
                SampleEventType.class
            );

            cut.publishEvent(SampleEventType.DEPLOY, "value1");
            cut.publishEvent(SampleEventType.UNDEPLOY, "value2");

            await()
                .atMost(30, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    assertThat(contentReceived).hasSize(2);
                    assertThat(contentReceived).containsOnly("value1", "value2");
                });
        }

        @Test
        void should_receive_event_with_only_subscribe_event_type_when_subscribing_to_one_event_type() {
            Set<String> contentReceived = new HashSet<>();
            cut.subscribeForEvents(
                (EventListener<SampleEventType, String>) event -> contentReceived.add(event.content()),
                SampleEventType.DEPLOY
            );

            cut.publishEvent(SampleEventType.DEPLOY, "value1");
            cut.publishEvent(SampleEventType.UNDEPLOY, "value2");

            await()
                .atMost(30, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    assertThat(contentReceived).hasSize(1);
                    assertThat(contentReceived).containsOnly("value1");
                });
        }
    }

    @Nested
    class UnsubscribeEventsTest {

        @Test
        void should_not_receive_any_event_after_subscribing_with_event_type_class_and_unsubscribing_with_event_type_class() {
            Set<String> contentReceived = new HashSet<>();
            EventListener<SampleEventType, String> eventListener = event -> contentReceived.add(event.content());
            cut.subscribeForEvents(eventListener, SampleEventType.class);
            cut.unsubscribeForEvents(eventListener, SampleEventType.class);
            cut.publishEvent(SampleEventType.DEPLOY, "value1");

            await().atMost(30, TimeUnit.SECONDS).untilAsserted(() -> assertThat(contentReceived).isEmpty());
        }

        @Test
        void should_not_receive_any_event_after_subscribing_with_event_type_class_and_unsubscribing_with_event_type() {
            Set<String> contentReceived = new HashSet<>();
            EventListener<SampleEventType, String> eventListener = event -> contentReceived.add(event.content());
            cut.subscribeForEvents(eventListener, SampleEventType.class);
            cut.unsubscribeForEvents(eventListener, SampleEventType.DEPLOY);
            cut.publishEvent(SampleEventType.DEPLOY, "value1");
            cut.publishEvent(SampleEventType.UNDEPLOY, "value2");

            await()
                .atMost(30, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    assertThat(contentReceived).hasSize(1);
                    assertThat(contentReceived).containsOnly("value2");
                });
        }

        @Test
        void should_not_receive_any_event_after_subscribing_with_event_type_and_unsubscribing_with_event_type_class() {
            Set<String> contentReceived = new HashSet<>();
            EventListener<SampleEventType, String> eventListener = event -> contentReceived.add(event.content());
            cut.subscribeForEvents(eventListener, SampleEventType.DEPLOY);
            cut.unsubscribeForEvents(eventListener, SampleEventType.class);
            cut.publishEvent(SampleEventType.DEPLOY, "value1");

            await().atMost(30, TimeUnit.SECONDS).untilAsserted(() -> assertThat(contentReceived).isEmpty());
        }

        @Test
        void should_not_receive_any_event_after_subscribing_with_event_type_and_unsubscribing_with_event_type() {
            Set<String> contentReceived = new HashSet<>();
            EventListener<SampleEventType, String> eventListener = event -> contentReceived.add(event.content());
            cut.subscribeForEvents(eventListener, SampleEventType.DEPLOY, SampleEventType.UNDEPLOY);
            cut.unsubscribeForEvents(eventListener, SampleEventType.DEPLOY);
            cut.publishEvent(SampleEventType.DEPLOY, "value1");
            cut.publishEvent(SampleEventType.UNDEPLOY, "value2");

            await()
                .atMost(30, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    assertThat(contentReceived).hasSize(1);
                    assertThat(contentReceived).containsOnly("value2");
                });
        }
    }
}
