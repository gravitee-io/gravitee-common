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
import io.gravitee.common.util.MultiValueMap;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * @author David BRASSELY (brasseld at gmail.com)
 */
public class HttpHeaders implements MultiValueMap<String, String> {

    public static final String CONNECTION = "Connection";
    public static final String CACHE_CONTROL = "Cache-Control";
    public static final String DATE = "Date";
    public static final String PRAGMA = "Pragma";
    public static final String PROXY_CONNECTION = "Proxy-Connection";
    public static final String TRAILER = "Trailer";
    public static final String TRANSFER_ENCODING = "Transfer-Encoding";
    public static final String UPGRADE = "Upgrade";
    public static final String VIA = "Via";
    public static final String WARNING = "Warning";
    public static final String NEGOTIATE = "Negotiate";
    public static final String ALLOW = "Allow";
    public static final String CONTENT_ENCODING = "Content-Encoding";
    public static final String CONTENT_LANGUAGE = "Content-Language";
    public static final String CONTENT_LENGTH = "Content-Length";
    public static final String CONTENT_LOCATION = "Content-Location";
    public static final String CONTENT_MD5 = "Content-MD5";
    public static final String CONTENT_RANGE = "Content-Range";
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String EXPIRES = "Expires";
    public static final String LAST_MODIFIED = "Last-Modified";
    public static final String ACCEPT = "Accept";
    public static final String ACCEPT_CHARSET = "Accept-Charset";
    public static final String ACCEPT_ENCODING = "Accept-Encoding";
    public static final String ACCEPT_LANGUAGE = "Accept-Language";
    public static final String AUTHORIZATION = "Authorization";
    public static final String EXPECT = "Expect";
    public static final String FORWARDED = "Forwarded";
    public static final String FROM = "From";
    public static final String HOST = "Host";
    public static final String IF_MATCH = "If-Match";
    public static final String IF_MODIFIED_SINCE = "If-Modified-Since";
    public static final String IF_NONE_MATCH = "If-None-Match";
    public static final String IF_RANGE = "If-Range";
    public static final String IF_UNMODIFIED_SINCE = "If-Unmodified-Since";
    public static final String KEEP_ALIVE = "Keep-Alive";
    public static final String MAX_FORWARDS = "Max-Forwards";
    public static final String PROXY_AUTHORIZATION = "Proxy-Authorization";
    public static final String RANGE = "Range";
    public static final String REQUEST_RANGE = "Request-Range";
    public static final String REFERER = "Referer";
    public static final String TE = "TE";
    public static final String USER_AGENT = "User-Agent";
    public static final String X_FORWARDED_FOR = "X-Forwarded-For";
    public static final String X_FORWARDED_PROTO = "X-Forwarded-Proto";
    public static final String X_FORWARDED_SERVER = "X-Forwarded-Server";
    public static final String X_FORWARDED_HOST = "X-Forwarded-Host";
    public static final String ACCEPT_RANGES = "Accept-Ranges";
    public static final String AGE = "Age";
    public static final String ETAG = "ETag";
    public static final String LOCATION = "Location";
    public static final String PROXY_AUTHENTICATE = "Proxy-Authenticate";
    public static final String RETRY_AFTER = "Retry-After";
    public static final String SERVER = "Server";
    public static final String SERVLET_ENGINE = "Servlet-Engine";
    public static final String VARY = "Vary";
    public static final String WWW_AUTHENTICATE = "WWW-Authenticate";
    public static final String COOKIE = "Cookie";
    public static final String SET_COOKIE = "Set-Cookie";
    public static final String SET_COOKIE2 = "Set-Cookie2";
    public static final String MIME_VERSION = "MIME-Version";
    public static final String IDENTITY = "identity";
    public static final String X_POWERED_BY = "X-Powered-By";
    public static final String ACCESS_CONTROL_ALLOW_CREDENTIALS = "Access-Control-Allow-Credentials";
    public static final String ACCESS_CONTROL_ALLOW_HEADERS = "Access-Control-Allow-Headers";
    public static final String ACCESS_CONTROL_ALLOW_METHODS = "Access-Control-Allow-Methods";
    public static final String ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";
    public static final String ACCESS_CONTROL_EXPOSE_HEADERS = "Access-Control-Expose-Headers";
    public static final String ACCESS_CONTROL_MAX_AGE = "Access-Control-Max-Age";
    public static final String ACCESS_CONTROL_REQUEST_HEADERS = "Access-Control-Request-Headers";
    public static final String ACCESS_CONTROL_REQUEST_METHOD = "Access-Control-Request-Method";

    private final Map<String, List<String>> headers;

    /**
     * Constructs a new, empty instance of the {@code HttpHeaders} object.
     */
    public HttpHeaders() {
        this.headers = new LinkedCaseInsensitiveMap<>(8);
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
    public boolean containsKey(Object key) {
        return headers.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return headers.containsValue(value);
    }

    @Override
    public List<String> get(Object key) {
        return headers.get(key);
    }

    @Override
    public List<String> put(String key, List<String> value) {
        return headers.put(key, value);
    }

    @Override
    public List<String> remove(Object key) {
        return headers.remove(key);
    }

    @Override
    public void putAll(Map<? extends String, ? extends List<String>> m) {
        headers.putAll(m);
    }

    @Override
    public void clear() {
        headers.clear();
    }

    @Override
    public Set<String> keySet() {
        return headers.keySet();
    }

    @Override
    public Collection<List<String>> values() {
        return headers.values();
    }

    @Override
    public Set<Entry<String, List<String>>> entrySet() {
        return headers.entrySet();
    }

    @Override
    public boolean equals(Object o) {
        return headers.equals(o);
    }

    @Override
    public int hashCode() {
        return headers.hashCode();
    }

    @Override
    public List<String> getOrDefault(Object key, List<String> defaultValue) {
        return headers.getOrDefault(key, defaultValue);
    }

    @Override
    public void forEach(BiConsumer<? super String, ? super List<String>> action) {
        headers.forEach(action);
    }

    @Override
    public void replaceAll(BiFunction<? super String, ? super List<String>, ? extends List<String>> function) {
        headers.replaceAll(function);
    }

    @Override
    public List<String> putIfAbsent(String key, List<String> value) {
        return headers.putIfAbsent(key, value);
    }

    @Override
    public boolean remove(Object key, Object value) {
        return headers.remove(key, value);
    }

    @Override
    public boolean replace(String key, List<String> oldValue, List<String> newValue) {
        return headers.replace(key, oldValue, newValue);
    }

    @Override
    public List<String> replace(String key, List<String> value) {
        return headers.replace(key, value);
    }

    @Override
    public List<String> computeIfAbsent(String key, Function<? super String, ? extends List<String>> mappingFunction) {
        return headers.computeIfAbsent(key, mappingFunction);
    }

    @Override
    public List<String> computeIfPresent(String key, BiFunction<? super String, ? super List<String>, ? extends List<String>> remappingFunction) {
        return headers.computeIfPresent(key, remappingFunction);
    }

    @Override
    public List<String> compute(String key, BiFunction<? super String, ? super List<String>, ? extends List<String>> remappingFunction) {
        return headers.compute(key, remappingFunction);
    }

    @Override
    public List<String> merge(String key, List<String> value, BiFunction<? super List<String>, ? super List<String>, ? extends List<String>> remappingFunction) {
        return headers.merge(key, value, remappingFunction);
    }

    @Override
    public String getFirst(String headerName) {
        List<String> headerValues = this.headers.get(headerName);
        return (headerValues != null ? headerValues.get(0) : null);
    }

    @Override
    public void add(String headerName, String headerValue) {
        List<String> headerValues = this.headers.get(headerName);
        if (headerValues == null) {
            headerValues = new LinkedList<>();
            this.headers.put(headerName, headerValues);
        }
        headerValues.add(headerValue);
    }

    @Override
    public void set(String headerName, String headerValue) {
        List<String> headerValues = new LinkedList<String>();
        headerValues.add(headerValue);
        this.headers.put(headerName, headerValues);
    }

    @Override
    public void setAll(Map<String, String> values) {
        for (Entry<String, String> entry : values.entrySet()) {
            set(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public Map<String, String> toSingleValueMap() {
        LinkedHashMap<String, String> singleValueMap = new LinkedHashMap<String,String>(this.headers.size());
        for (Entry<String, List<String>> entry : this.headers.entrySet()) {
            singleValueMap.put(entry.getKey(), entry.getValue().get(0));
        }
        return singleValueMap;
    }

    /**
     * Return the length of the body in bytes, as specified by the
     * {@code Content-Length} header.
     * <p>Returns -1 when the content-length is unknown.
     */
    public long contentLength() {
        String value = getFirst(CONTENT_LENGTH);
        return (value != null ? Long.parseLong(value) : -1);
    }

    /**
     * Set the length of the body in bytes, as specified by the
     * {@code Content-Length} header.
     */
    public void contentLength(long contentLength) {
        set(CONTENT_LENGTH, Long.toString(contentLength));
    }

    /**
     * Return the type of the body, as specified
     * by the {@code Content-Type} header.
     * <p>Returns {@code null} when the content-type is unknown.
     */
    public String contentType() {
        return getFirst(CONTENT_TYPE);
    }

    /**
     * Set the length of the body in bytes, as specified by the
     * {@code Content-Length} header.
     */
    public void contentType(String contentType) {
        set(CONTENT_TYPE, contentType);
    }
}
