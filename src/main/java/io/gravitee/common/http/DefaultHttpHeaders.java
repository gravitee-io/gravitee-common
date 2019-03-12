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
package io.gravitee.common.http;

import io.gravitee.common.util.LinkedCaseInsensitiveMap;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

/**
 * @author David BRASSELY (david.brassely at graviteesource.com)
 * @author GraviteeSource Team
 */
public class DefaultHttpHeaders implements HttpHeaders {

    private final Map<String, List<String>> headers;

    /**
     * Constructs a new, empty instance of the {@code DefaultHttpHeaders} object.
     */
    public DefaultHttpHeaders() {
        this.headers = new LinkedCaseInsensitiveMap<>(8);
    }

    private DefaultHttpHeaders(int initialCapacity) {
        this.headers = new LinkedCaseInsensitiveMap<>(initialCapacity);
    }

    public DefaultHttpHeaders(HttpHeaders httpHeaders) {
        this(httpHeaders.size());
        httpHeaders.forEach(this::add);
    }

    @Override
    public int size() {
        return headers.size();
    }

    @Override
    public boolean isEmpty() {
        return headers.isEmpty();
    }

    @Override
    public boolean contains(String name) {
        return headers.containsKey(name);
    }

    @Override
    public List<String> get(String name) {
        return headers.get(name);
    }

    @Override
    public HttpHeaders put(String name, List<String> value) {
        headers.put(name, value);
        return this;
    }

    @Override
    public List<String> remove(String name) {
        return headers.remove(name);
    }

    @Override
    public void clear() {
        headers.clear();
    }

    @Override
    public Set<String> names() {
        return headers.keySet();
    }

    @Override
    public List<Map.Entry<String, List<String>>> entries() {
        //TODO: implement it
        return null; //headers.entrySet();
    }

    @Override
    public void forEach(BiConsumer<String, String> action) {
        headers.forEach((name, values) -> values.forEach(value -> action.accept(name, value)));
    }

    @Override
    public String getFirst(String headerName) {
        List<String> headerValues = this.headers.get(headerName);
        return (headerValues != null ? headerValues.get(0) : null);
    }

    @Override
    public HttpHeaders add(String headerName, String headerValue) {
        List<String> headerValues = this.headers.get(headerName);
        if (headerValues == null) {
            headerValues = new LinkedList<>();
            this.headers.put(headerName, headerValues);
        }
        headerValues.add(headerValue);
        return this;
    }

    @Override
    public HttpHeaders set(String headerName, String headerValue) {
        List<String> headerValues = new LinkedList<String>();
        headerValues.add(headerValue);
        this.headers.put(headerName, headerValues);
        return this;
    }

    @Override
    public HttpHeaders setAll(Map<String, String> values) {
        for (Map.Entry<String, String> entry : values.entrySet()) {
            set(entry.getKey(), entry.getValue());
        }

        return this;
    }
/*
    @Override
    public Map<String, String> toSingleValueMap() {
        LinkedHashMap<String, String> singleValueMap = new LinkedHashMap<String,String>(this.headers.size());
        for (Entry<String, List<String>> entry : this.headers.entrySet()) {
            singleValueMap.put(entry.getKey(), entry.getValue().get(0));
        }
        return singleValueMap;
    }
*/

    @Override
    public boolean equals(Object o) {
        return headers.equals(o);
    }

    @Override
    public int hashCode() {
        return headers.hashCode();
    }
}
