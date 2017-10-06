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
package io.gravitee.common.util;

import java.util.Collection;
import java.util.LinkedHashSet;

/**
 * Set implementation that is case insensitive.
 *
 * @author David BRASSELY (david.brassely at graviteesource.com)
 * @author GraviteeSource Team
 */
public class LinkedCaseInsensitiveSet extends LinkedHashSet<String> {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new, empty linked hash set with the default initial
     * capacity (16) and load factor (0.75).
     */
    public LinkedCaseInsensitiveSet() {
        super();
    }

    /**
     * Constructor initializing the set with the given collection.
     *
     * @param source The source collection to use for initialization.
     */
    public LinkedCaseInsensitiveSet(Collection<? extends String> source) {
        super(source);
    }

    @Override
    public boolean add(String element) {
        return super.add(element.toLowerCase());
    }

    /**
     * Verify containment by ignoring case.
     */
    public boolean contains(String element) {
        return super.contains(element.toLowerCase());
    }

    @Override
    public boolean contains(Object o) {
        return contains(o.toString());
    }
}