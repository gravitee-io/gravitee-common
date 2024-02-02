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
package io.gravitee.common.cron;

import io.gravitee.common.utils.TimeProvider;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.support.SimpleTriggerContext;

/**
 * @author Yann TAVERNIER (yann.tavernier at graviteesource.com)
 * @author GraviteeSource Team
 */
@Slf4j
public class CronTrigger {

    private final SimpleTriggerContext triggerContext;
    private final org.springframework.scheduling.support.CronTrigger delegate;

    public CronTrigger(String schedule) {
        delegate = new org.springframework.scheduling.support.CronTrigger(schedule);
        triggerContext = new SimpleTriggerContext(TimeProvider.clock());
    }

    /**
     * Compute the delay to next execution
     * @return the delay in milliseconds to the next execution time
     */
    public long nextExecutionIn() {
        final Instant nextExecutionDate = delegate.nextExecution(triggerContext);

        if (nextExecutionDate == null) {
            return Long.MAX_VALUE;
        }

        return ChronoUnit.MILLIS.between(TimeProvider.instantNow(), nextExecutionDate);
    }
}
