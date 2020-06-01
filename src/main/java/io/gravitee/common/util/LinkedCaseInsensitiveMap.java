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

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * @author David BRASSELY (brasseld at gmail.com)
 */
public class LinkedCaseInsensitiveMap<V> extends LinkedHashMap<String, V> {

    private final Map<String, String> caseInsensitiveKeys;

    private final Locale locale;


    /**
     * Create a new LinkedCaseInsensitiveMap for the default Locale.
     * @see java.lang.String#toLowerCase()
     */
    public LinkedCaseInsensitiveMap() {
        this(null);
    }

    /**
     * Create a new LinkedCaseInsensitiveMap that stores lower-case keys
     * according to the given Locale.
     * @param locale the Locale to use for lower-case conversion
     * @see java.lang.String#toLowerCase(java.util.Locale)
     */
    public LinkedCaseInsensitiveMap(Locale locale) {
        super();
        this.caseInsensitiveKeys = new HashMap<>();
        this.locale = (locale != null ? locale : Locale.getDefault());
    }

    /**
     * Create a new LinkedCaseInsensitiveMap that wraps a {@link LinkedHashMap}
     * with the given initial capacity and stores lower-case keys according
     * to the default Locale.
     * @param initialCapacity the initial capacity
     * @see java.lang.String#toLowerCase()
     */
    public LinkedCaseInsensitiveMap(int initialCapacity) {
        this(initialCapacity, null);
    }

    /**
     * Create a new LinkedCaseInsensitiveMap that wraps a {@link LinkedHashMap}
     * with the given initial capacity and stores lower-case keys according
     * to the given Locale.
     * @param initialCapacity the initial capacity
     * @param locale the Locale to use for lower-case conversion
     * @see java.lang.String#toLowerCase(java.util.Locale)
     */
    public LinkedCaseInsensitiveMap(int initialCapacity, Locale locale) {
        super(initialCapacity);
        this.caseInsensitiveKeys = new HashMap<>(initialCapacity);
        this.locale = (locale != null ? locale : Locale.getDefault());
    }


    @Override
    public V put(String key, V value) {
        String oldKey = this.caseInsensitiveKeys.put(convertKey(key), key);
        if (oldKey != null && !oldKey.equals(key)) {
            super.remove(oldKey);
        }
        return super.put(key, value);
    }

    @Override
    public void putAll(Map<? extends String, ? extends V> map) {
        if (map.isEmpty()) {
            return;
        }
        for (Map.Entry<? extends String, ? extends V> entry : map.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public boolean containsKey(Object key) {
        return (key instanceof String && this.caseInsensitiveKeys.containsKey(convertKey((String) key)));
    }

    @Override
    public V get(Object key) {
        if (key instanceof String) {
            return super.get(this.caseInsensitiveKeys.get(convertKey((String) key)));
        }
        else {
            return null;
        }
    }

    @Override
    public V remove(Object key) {
        if (key instanceof String ) {
            return super.remove(this.caseInsensitiveKeys.remove(convertKey((String) key)));
        }
        else {
            return null;
        }
    }

    @Override
    public void clear() {
        this.caseInsensitiveKeys.clear();
        super.clear();
    }

    @Override
    public V getOrDefault(Object key, V defaultValue) {
        if (key instanceof String ) {
            return super.getOrDefault(this.caseInsensitiveKeys.get(convertKey((String) key)), defaultValue);
        }
        else {
            return null;
        }
    }

    @Override
    public V putIfAbsent(String key, V value) {
        if (!this.caseInsensitiveKeys.containsKey(convertKey(key))) {
            this.caseInsensitiveKeys.put(convertKey(key), key);
            return super.put(key, value);
        }
        return get(key);
    }

    @Override
    public V computeIfAbsent(String key, Function<? super String, ? extends V> mappingFunction) {
        if (!this.caseInsensitiveKeys.containsKey(convertKey(key))) {
            this.caseInsensitiveKeys.put(convertKey(key), key);
            return super.computeIfAbsent(key, mappingFunction);
        }
        return get(key);
    }

    @Override
    public V computeIfPresent(String key, BiFunction<? super String, ? super V, ? extends V> remappingFunction) {
        String oldKey = this.caseInsensitiveKeys.put(convertKey(key), key);
        if (oldKey != null) {
            if (!oldKey.equals(key)) {
                super.remove(oldKey);
            }
            return super.compute(key, remappingFunction);
        }
        return null;
    }

    @Override
    public V replace(String key, V value) {
        String oldKey = this.caseInsensitiveKeys.put(convertKey(key), key);
        if (oldKey != null) {
            V oldValue = super.get(oldKey);
            if (oldKey.equals(key)) {
                super.replace(key, value);
            } else {
                super.remove(oldKey);
                super.put(key, value);
            }
            return oldValue;
        }
        return null;
    }

    /**
     * Convert the given key to a case-insensitive key.
     * <p>The default implementation converts the key
     * to lower-case according to this Map's Locale.
     * @param key the user-specified key
     * @return the key to use for storing
     * @see java.lang.String#toLowerCase(java.util.Locale)
     */
    protected String convertKey(String key) {
        return key.toLowerCase(this.locale);
    }

}
