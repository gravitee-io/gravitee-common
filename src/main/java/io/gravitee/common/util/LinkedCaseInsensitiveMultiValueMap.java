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
package io.gravitee.common.util;

import java.io.Serializable;
import java.util.*;

/**
 * @author Florent CHAMFROY (florent.chamfroy at graviteesource.com)
 * @author GraviteeSource Team
 */
public class LinkedCaseInsensitiveMultiValueMap<V> extends LinkedMultiValueMap<String, V> {

    /**
     * Create a new LinkedCaseInsensitiveMultiValueMap that wraps a {@link LinkedCaseInsensitiveMap} for the default Locale.
     * @see java.lang.String#toLowerCase()
     */
    public LinkedCaseInsensitiveMultiValueMap() {
        this.targetMap = new LinkedCaseInsensitiveMap();
    }

    /**
     * Create a new LinkedCaseInsensitiveMultiValueMap that wraps a {@link LinkedCaseInsensitiveMap} according to the given Locale.
     * @param locale the Locale to use for lower-case conversion
     * @see java.lang.String#toLowerCase(java.util.Locale)
     */
    public LinkedCaseInsensitiveMultiValueMap(Locale locale) {
        this.targetMap = new LinkedCaseInsensitiveMap(locale);
    }

    /**
     * Create a new LinkedCaseInsensitiveMultiValueMap that wraps a {@link LinkedCaseInsensitiveMap}
     * with the given initial capacity  and stores lower-case keys according to the default Locale.
     * @param initialCapacity the initial capacity
     * @see java.lang.String#toLowerCase()
     */
    public LinkedCaseInsensitiveMultiValueMap(int initialCapacity) {
        this.targetMap = new LinkedCaseInsensitiveMap(initialCapacity);
    }

    /**
     * Create a new LinkedCaseInsensitiveMultiValueMap that wraps a {@link LinkedCaseInsensitiveMap}
     * with the given initial capacity  and stores lower-case keys according to the given Locale.
     * @param initialCapacity the initial capacity
     * @param locale the Locale to use for lower-case conversion
     * @see java.lang.String#toLowerCase(java.util.Locale)
     */
    public LinkedCaseInsensitiveMultiValueMap(int initialCapacity, Locale locale) {
        this.targetMap = new LinkedCaseInsensitiveMap(initialCapacity, locale);
    }
}
