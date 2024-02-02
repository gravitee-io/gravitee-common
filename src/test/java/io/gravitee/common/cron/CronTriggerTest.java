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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.gravitee.common.utils.TimeProvider;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * @author Yann TAVERNIER (yann.tavernier at graviteesource.com)
 * @author GraviteeSource Team
 */
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class CronTriggerTest {

    private static final Instant INSTANT_NOW = Instant.parse("2024-02-01T10:15:12Z");

    @BeforeAll
    static void beforeAll() {
        TimeProvider.overrideClock(Clock.fixed(INSTANT_NOW, ZoneId.systemDefault()));
    }

    @AfterAll
    static void afterAll() {
        TimeProvider.reset();
    }

    @ParameterizedTest
    @CsvSource(
        {
            // Every 30 seconds
            "*/30 * * * * *, 2024-02-01T10:15:12Z, 2024-02-01T10:15:30Z",
            // Every 30 seconds
            "*/30 * * * * *, 2024-02-01T10:15:29Z, 2024-02-01T10:15:30Z",
            // Every 60 seconds
            "*/60 * * * * *, 2024-02-01T10:15:29Z, 2024-02-01T10:16:00Z",
            // Every even minute
            "0 */2 * ? * *, 2024-02-01T10:15:29Z, 2024-02-01T10:16:00Z",
            // Every even minute
            "0 */2 * ? * *, 2024-02-01T10:14:29Z, 2024-02-01T10:16:00Z",
        }
    )
    void should_get_next_execution_time(String cronExpression, String fakeNow, String expectedNextExecutionTime) {
        final Instant instantNow = Instant.parse(fakeNow);
        TimeProvider.overrideClock(Clock.fixed(instantNow, ZoneId.systemDefault()));
        final long result = new CronTrigger(cronExpression).nextExecutionIn();

        assertThat(instantNow.plus(result, ChronoUnit.MILLIS)).isEqualTo(Instant.parse(expectedNextExecutionTime));
    }

    @Test
    void should_throw_on_invalid_cron() {
        assertThatThrownBy(() -> new CronTrigger("invalid")).isInstanceOf(IllegalArgumentException.class);
    }
}
