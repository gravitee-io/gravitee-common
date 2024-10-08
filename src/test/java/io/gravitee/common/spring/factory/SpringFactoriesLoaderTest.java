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
package io.gravitee.common.spring.factory;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import javax.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

@Configuration
class SpringFactoriesLoaderTest {

    @Service
    static class MyServiceDeps {}

    interface AService {
        ApplicationContext getApplicationContext();

        MyServiceDeps getMyServiceDeps();
    }

    @RequiredArgsConstructor
    @Setter
    @Getter
    @Order(12)
    @Service
    static class MyService implements ApplicationContextAware, AService {

        private final MyServiceDeps myServiceDeps;
        private ApplicationContext applicationContext;
    }

    @RequiredArgsConstructor
    @Setter
    @Getter
    @Order(1)
    @Service
    static class MyAnotherService implements ApplicationContextAware, AService {

        private final MyServiceDeps myServiceDeps;
        private ApplicationContext applicationContext;
    }

    @RequiredArgsConstructor
    @Getter
    @Order(3)
    @Service
    static class MyYetAnotherService implements AService, InitializingBean {

        private MyServiceDeps myServiceDeps;
        private final ApplicationContext applicationContext;

        @Override
        public void afterPropertiesSet() {
            myServiceDeps = new MyServiceDeps();
        }
    }

    @RequiredArgsConstructor
    @Getter
    @Order(5)
    @Service
    static class MyYetYetAnotherService implements AService {

        private MyServiceDeps myServiceDeps;
        private final ApplicationContext applicationContext;

        @PostConstruct
        public void setup() {
            myServiceDeps = new MyServiceDeps();
        }
    }

    @Test
    void createSpringFactoriesInstances() {
        // Given
        ApplicationContext ctx = new AnnotationConfigApplicationContext(SpringFactoriesLoaderTest.class);

        // When
        List<AService> svc = SpringFactoriesLoader.createSpringFactoriesInstances(ctx, AService.class);

        // Then
        assertThat(svc).hasSize(4);
        for (AService s : svc) {
            assertThat(s.getApplicationContext()).isNotNull();
            assertThat(s.getMyServiceDeps()).isNotNull();
        }
        assertThat(svc.stream().mapToInt(o -> o.getClass().getAnnotation(Order.class).value())).isSorted();
    }
}
