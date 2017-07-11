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
package io.gravitee.common.node;

import io.gravitee.common.component.Lifecycle;
import io.gravitee.common.component.LifecycleComponent;
import io.gravitee.common.service.AbstractService;
import io.gravitee.common.util.ListReverser;
import io.gravitee.common.util.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.management.ManagementFactory;
import java.util.List;

/**
 * @author David BRASSELY (david.brassely at graviteesource.com)
 * @author GraviteeSource Team
 */
public abstract class AbstractNode extends AbstractService<Node> implements Node, ApplicationContextAware {

    protected final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    protected ApplicationContext applicationContext;

    @Override
    protected void doStart() throws Exception {
        LOGGER.info("{} is now starting...", name());
        long startTime = System.currentTimeMillis(); // Get the start Time

        List<Class<? extends LifecycleComponent>> components = getLifecycleComponents();
        for(Class<? extends LifecycleComponent> componentClass: components) {
            LOGGER.info("\tStarting component: {}", componentClass.getSimpleName());

            try {
                LifecycleComponent lifecyclecomponent = applicationContext.getBean(componentClass);
                lifecyclecomponent.start();
            } catch (Exception e) {
                LOGGER.error("An error occurs while starting component {}", componentClass.getSimpleName(), e);
                throw e;
            }
        }

        long endTime = System.currentTimeMillis(); // Get the end Time

        String processId = ManagementFactory.getRuntimeMXBean().getName();
        if (processId.contains("@")) {
            processId = processId.split("@")[0];
        }

        LOGGER.info("{} id[{}] version[{}] pid[{}] build[{}#{}] jvm[{}/{}/{}] started in {} ms.", name(), id(),
                Version.RUNTIME_VERSION.MAJOR_VERSION, processId, Version.RUNTIME_VERSION.BUILD_NUMBER,
                Version.RUNTIME_VERSION.REVISION, ManagementFactory.getRuntimeMXBean().getVmVendor(),
                ManagementFactory.getRuntimeMXBean().getVmName(), ManagementFactory.getRuntimeMXBean().getVmVersion(),
                (endTime - startTime));
    }

    @Override
    protected void doStop() throws Exception {
        LOGGER.info("{} is stopping", name());

        ListReverser<Class<? extends LifecycleComponent>> components =
                new ListReverser<>(getLifecycleComponents());
        for(Class<? extends LifecycleComponent> componentClass: components) {
            try {
                LifecycleComponent lifecyclecomponent = applicationContext.getBean(componentClass);
                if (lifecyclecomponent.lifecycleState() == Lifecycle.State.STARTED) {
                    LOGGER.info("\tStopping component: {}", componentClass.getSimpleName());
                    lifecyclecomponent.stop();
                }
            } catch (Exception e) {
                LOGGER.error("An error occurs while stopping component {}", componentClass.getSimpleName(), e);
            }
        }

        LOGGER.info("{} stopped", name());
    }

    public abstract String name();

    protected abstract List<Class<? extends LifecycleComponent>> getLifecycleComponents();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
