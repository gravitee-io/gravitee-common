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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;

/**
 * @author David BRASSELY (david.brassely at graviteesource.com)
 * @author GraviteeSource Team
 */
public class Maps {

    private Maps() {
    }

    public static <K, V> MapBuilder<K, V> builder() {
        return builder(HashMap::new);
    }

    public static <K, V> MapBuilder<K, V> builder(Supplier<Map<K, V>> mapSupplier) {
        return new MapBuilder<>(mapSupplier.get());
    }

    public static <K, V> ConcurrentMapBuilder<K, V> concurrentBuilder() {
        return concurrentBuilder(ConcurrentHashMap::new);
    }

    public static <K, V> ConcurrentMapBuilder<K, V> concurrentBuilder(Supplier<ConcurrentMap<K, V>> mapSupplier) {
        return new ConcurrentMapBuilder<>(mapSupplier.get());
    }

    private static class BaseBuilder<M extends Map<K, V>, K, V> {

        protected final M map;

        public BaseBuilder(M map) {
            this.map = map;
        }

        public BaseBuilder<M, K, V> put(K key, V value) {
            map.put(key, value);
            return this;
        }

        public M build() {
            return map;
        }
    }

    public static class MapBuilder<K, V> extends BaseBuilder<Map<K, V>, K, V> {

        private boolean unmodifiable;

        public MapBuilder(Map<K, V> map) {
            super(map);
        }

        @Override
        public MapBuilder<K, V> put(K key, V value) {
            super.put(key, value);
            return this;
        }

        public MapBuilder<K, V> unmodifiable(boolean unmodifiable) {
            this.unmodifiable = unmodifiable;
            return this;
        }

        @Override
        public Map<K, V> build() {
            if (unmodifiable) {
                return Collections.unmodifiableMap(super.build());
            } else {
                return super.build();
            }
        }
    }

    public static class ConcurrentMapBuilder<K, V> extends BaseBuilder<ConcurrentMap<K, V>, K, V> {

        public ConcurrentMapBuilder(ConcurrentMap<K, V> map) {
            super(map);
        }

        @Override
        public ConcurrentMapBuilder<K, V> put(K key, V value) {
            super.put(key, value);
            return this;
        }
    }

}
