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
package io.gravitee.common.data.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.function.Function;
import org.junit.jupiter.api.Test;

class PageTest {

    @Test
    void mapValues() {
        Page<Integer> input = new Page<>(List.of(1, 2, 3), 1, 3, 3);

        Page<String> map = input.map((Function<? super Integer, String>) i -> Integer.toString(i));

        assertThat(map.getContent()).containsExactly("1", "2", "3");
        assertThat(map.getTotalElements()).isEqualTo(3);
        assertThat(map.getPageElements()).isEqualTo(3);
    }
}
