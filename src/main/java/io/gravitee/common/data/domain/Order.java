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
package io.gravitee.common.data.domain;

/**
 * @author Titouan COMPIEGNE (titouan.compiegne at graviteesource.com)
 * @author GraviteeSource Team
 */
public class Order {

    private final String property;
    private final Direction direction;
    private final Mode mode;

    public Order(String property, Direction direction, Mode mode) {
        this.property = property;
        this.direction = direction;
        this.mode = mode;
    }

    public String getProperty() {
        return property;
    }

    public Direction getDirection() {
        return direction;
    }

    public Mode getMode() {
        return mode;
    }

    public enum Direction {
        ASC("asc"),
        DESC("desc");

        private final String direction;

        Direction(String direction) {
            this.direction = direction;
        }

        String getValue() {
            return this.direction;
        }
    }

    public enum Mode {
        AVG("avg");

        private final String mode;

        Mode(String mode) {
            this.mode = mode;
        }

        String getValue() {
            return mode;
        }
    }
}
