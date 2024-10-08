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

import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;

/**
 * @author David BRASSELY (david.brassely at graviteesource.com)
 * @author GraviteeSource Team
 */
public abstract class SpringFactoriesLoader<T> implements ApplicationContextAware {

    /**
     * Logger.
     */
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected ApplicationContext applicationContext;

    private Collection<? extends T> factories;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    protected abstract Class<T> getObjectType();

    protected Collection<? extends T> getFactoriesInstances() {
        if (factories == null) {
            logger.debug("Loading instances for type {}", getObjectType().getName());
            Class<T> type = getObjectType();
            factories = createSpringFactoriesInstances(applicationContext, type);
        } else {
            logger.debug("Instances for type {} already loaded. Skipping...", getObjectType().getName());
        }

        return factories;
    }

    // @VisibleForTesting
    static <U> List<U> createSpringFactoriesInstances(ApplicationContext applicationContext, Class<U> type) {
        var instances = new ArrayList<>(applicationContext.getBeansOfType(type).values());
        AnnotationAwareOrderComparator.sort(instances);
        return instances;
    }
}
