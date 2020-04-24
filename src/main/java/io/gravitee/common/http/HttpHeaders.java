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

    /**
     * See {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.1">HTTP/1.1 documentation</a>}.
     */
    public static final String ACCEPT = "Accept";
    /**
     * See {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.2">HTTP/1.1 documentation</a>}.
     */
    public static final String ACCEPT_CHARSET = "Accept-Charset";
    /**
     * See {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.3">HTTP/1.1 documentation</a>}.
     */
    public static final String ACCEPT_ENCODING = "Accept-Encoding";
    /**
     * See {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.4">HTTP/1.1 documentation</a>}.
     */
    public static final String ACCEPT_LANGUAGE = "Accept-Language";
    /**
     * See {@link <a href="https://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.5">HTTP/1.1 documentation</a>}.
     */
    public static final String ACCEPT_RANGES = "Accept-Ranges";
    /**
     * See {@link <a href="https://www.w3.org/TR/cors/#access-control-allow-credentials-response-header">CORS documentation</a>}.
     */
    public static final String ACCESS_CONTROL_ALLOW_CREDENTIALS = "Access-Control-Allow-Credentials";
    /**
     * See {@link <a href="https://www.w3.org/TR/cors/#access-control-allow-headers-response-header">CORS documentation</a>}.
     */
    public static final String ACCESS_CONTROL_ALLOW_HEADERS = "Access-Control-Allow-Headers";
    /**
     * See {@link <a href="https://www.w3.org/TR/cors/#access-control-allow-methods-response-header">CORS documentation</a>}.
     */
    public static final String ACCESS_CONTROL_ALLOW_METHODS = "Access-Control-Allow-Methods";
    /**
     * See {@link <a href="https://www.w3.org/TR/cors/#http-access-control-allow-origin">CORS documentation</a>}.
     */
    public static final String ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";
    /**
     * See {@link <a href="https://www.w3.org/TR/cors/#access-control-expose-headers-response-header">CORS documentation</a>}.
     */
    public static final String ACCESS_CONTROL_EXPOSE_HEADERS = "Access-Control-Expose-Headers";
    /**
     * See {@link <a href="https://www.w3.org/TR/cors/#access-control-max-age-response-header">CORS documentation</a>}.
     */
    public static final String ACCESS_CONTROL_MAX_AGE = "Access-Control-Max-Age";
    /**
     * See {@link <a href="https://www.w3.org/TR/cors/#access-control-request-headers-request-header">CORS documentation</a>}.
     */
    public static final String ACCESS_CONTROL_REQUEST_HEADERS = "Access-Control-Request-Headers";
    /**
     * See {@link <a href="https://www.w3.org/TR/cors/#access-control-request-method-request-header">CORS documentation</a>}.
     */
    public static final String ACCESS_CONTROL_REQUEST_METHOD = "Access-Control-Request-Method";
    /**
     * See {@link <a href="https://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.6">HTTP/1.1 documentation</a>}.
     */
    public static final String AGE = "Age";
    /**
     * See {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.7">HTTP/1.1 documentation</a>}.
     */
    public static final String ALLOW = "Allow";
    /**
     * See {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.8">HTTP/1.1 documentation</a>}.
     */
    public static final String AUTHORIZATION = "Authorization";
    /**
     * See {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.9">HTTP/1.1 documentation</a>}.
     */
    public static final String CACHE_CONTROL = "Cache-Control";
    /**
     * See {@link <a href="https://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.10">HTTP/1.1 documentation</a>}.
     */
    public static final String CONNECTION = "Connection";
    /**
     * See {@link <a href="http://tools.ietf.org/html/rfc2183">IETF RFC-2183</a>}.
     */
    public static final String CONTENT_DISPOSITION = "Content-Disposition";
    /**
     * See {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.11">HTTP/1.1 documentation</a>}.
     */
    public static final String CONTENT_ENCODING = "Content-Encoding";
    /**
     * See {@link <a href="http://tools.ietf.org/html/rfc2392">IETF RFC-2392</a>}.
     */
    public static final String CONTENT_ID = "Content-ID";
    /**
     * See {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.12">HTTP/1.1 documentation</a>}.
     */
    public static final String CONTENT_LANGUAGE = "Content-Language";
    /**
     * See {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.13">HTTP/1.1 documentation</a>}.
     */
    public static final String CONTENT_LENGTH = "Content-Length";
    /**
     * See {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.14">HTTP/1.1 documentation</a>}.
     */
    public static final String CONTENT_LOCATION = "Content-Location";
    /**
     * See {@link <a href="https://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.15">HTTP/1.1 documentation</a>}.
     */
    public static final String CONTENT_MD5 = "Content-MD5";
    /**
     * See {@link <a href="https://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.16">HTTP/1.1 documentation</a>}.
     */
    public static final String CONTENT_RANGE = "Content-Range";
    /**
     * See {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.17">HTTP/1.1 documentation</a>}.
     */
    public static final String CONTENT_TYPE = "Content-Type";
    /**
     * See {@link <a href="http://www.ietf.org/rfc/rfc2109.txt">IETF RFC 2109</a>}.
     */
    public static final String COOKIE = "Cookie";
    /**
     * See {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.18">HTTP/1.1 documentation</a>}.
     */
    public static final String DATE = "Date";
    /**
     * See {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.19">HTTP/1.1 documentation</a>}.
     */
    public static final String ETAG = "ETag";
    /**
     * See {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.21">HTTP/1.1 documentation</a>}.
     */
    public static final String EXPIRES = "Expires";
    /**
     * See {@link <a href="https://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.20">HTTP/1.1 documentation</a>}.
     */
    public static final String EXPECT = "Expect";
    /**
     * See {@link <a href="https://tools.ietf.org/html/rfc7239">IETF RFC 7239</a>}.
     */
    public static final String FORWARDED = "Forwarded";
    /**
     * See {@link <a href="https://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.22">HTTP/1.1 documentation</a>}.
     */
    public static final String FROM = "From";
    /**
     * See {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.23">HTTP/1.1 documentation</a>}.
     */
    public static final String HOST = "Host";
    /**
     * See {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.24">HTTP/1.1 documentation</a>}.
     */
    public static final String IF_MATCH = "If-Match";
    /**
     * See {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.25">HTTP/1.1 documentation</a>}.
     */
    public static final String IF_MODIFIED_SINCE = "If-Modified-Since";
    /**
     * See {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.26">HTTP/1.1 documentation</a>}.
     */
    public static final String IF_NONE_MATCH = "If-None-Match";
    /**
     * See {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.28">HTTP/1.1 documentation</a>}.
     */
    public static final String IF_UNMODIFIED_SINCE = "If-Unmodified-Since";
    /**
     * See {@link <a href="https://www.w3.org/Protocols/HTTP/1.1/draft-ietf-http-v11-spec-01#Keep-Alive">HTTP/1.1 documentation</a>}.
     */
    public static final String KEEP_ALIVE = "Keep-Alive";
    /**
     * See {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.29">HTTP/1.1 documentation</a>}.
     */
    public static final String LAST_MODIFIED = "Last-Modified";
    /**
     * See {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.30">HTTP/1.1 documentation</a>}.
     */
    public static final String LOCATION = "Location";
    /**
     * See {@link <a href="http://tools.ietf.org/html/rfc5988#page-6">Web Linking (IETF RFC-5988) documentation</a>}.
     */
    public static final String LINK = "Link";
    /**
     * See {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.31">HTTP/1.1 documentation</a>}.
     */
    public static final String MAX_FORWARDS = "Max-Forwards";
    /**
     * See {@link <a href="https://www.w3.org/Protocols/rfc1341/3_MIME-Version.html">HTTP/1.1 documentation</a>}.
     */
    public static final String MIME_VERSION = "MIME-Version";
    /**
     * https://tools.ietf.org/id/draft-abarth-origin-03.html#rfc.section.2
     */
    public static final String ORIGIN = "Origin";
    /**
     * See {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.32">HTTP/1.1 documentation</a>}.
     */
    public static final String PRAGMA = "Pragma";
    /**
     * See {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.33">HTTP/1.1 documentation</a>}.
     */
    public static final String PROXY_AUTHENTICATE = "Proxy-Authenticate";
    /**
     * See {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.34">HTTP/1.1 documentation</a>}.
     */
    public static final String PROXY_AUTHORIZATION = "Proxy-Authorization";
    public static final String PROXY_CONNECTION = "Proxy-Connection";
    /**
     * See {@link <a href="https://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.35">HTTP/1.1 documentation</a>}.
     */
    public static final String RANGE = "Range";
    /**
     * See {@link <a href="https://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.36">HTTP/1.1 documentation</a>}.
     */
    public static final String REFERER = "Referer";
    /**
     * See {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.37">HTTP/1.1 documentation</a>}.
     */
    public static final String RETRY_AFTER = "Retry-After";
    /**
     * See {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.38">HTTP/1.1 documentation</a>}.
     */
    public static final String SERVER = "Server";
    /**
     * See {@link <a href="http://www.ietf.org/rfc/rfc2109.txt">IETF RFC 2109</a>}.
     */
    public static final String SET_COOKIE = "Set-Cookie";
    /**
     * See {@link <a href="https://www.ietf.org/rfc/rfc2965.txt">IETF RFC 2965</a>}.
     */
    public static final String SET_COOKIE2 = "Set-Cookie2";
    /**
     * See {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.39">HTTP/1.1 documentation</a>}.
     */
    public static final String TE = "TE";
    /**
     * See {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.40">HTTP/1.1 documentation</a>}.
     */
    public static final String TRAILER = "Trailer";
    /**
     * See {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.41">HTTP/1.1 documentation</a>}.
     */
    public static final String TRANSFER_ENCODING = "Transfer-Encoding";
    /**
     * See {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.42">HTTP/1.1 documentation</a>}.
     */
    public static final String UPGRADE = "Upgrade";
    /**
     * See {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.43">HTTP/1.1 documentation</a>}.
     */
    public static final String USER_AGENT = "User-Agent";
    /**
     * See {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.44">HTTP/1.1 documentation</a>}.
     */
    public static final String VARY = "Vary";
    /**
     * See {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.45">HTTP/1.1 documentation</a>}.
     */
    public static final String VIA = "Via";
    /**
     * See {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.46">HTTP/1.1 documentation</a>}.
     */
    public static final String WARNING = "Warning";
    /**
     * See {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.47">HTTP/1.1 documentation</a>}.
     */
    public static final String WWW_AUTHENTICATE = "WWW-Authenticate";

    public static final String X_FORWARDED_FOR = "X-Forwarded-For";
    public static final String X_FORWARDED_PROTO = "X-Forwarded-Proto";
    public static final String X_FORWARDED_SERVER = "X-Forwarded-Server";
    public static final String X_FORWARDED_HOST = "X-Forwarded-Host";
    public static final String X_FORWARDED_PORT = "X-Forwarded-Port";

    private final Map<String, List<String>> headers;

    /**
     * Constructs a new, empty instance of the {@code HttpHeaders} object.
     */
    public HttpHeaders() {
        this.headers = new LinkedCaseInsensitiveMap<>(8);
    }

    public HttpHeaders(int initialCapacity) {
        this.headers = new LinkedCaseInsensitiveMap<>(initialCapacity);
    }

    public HttpHeaders(HttpHeaders httpHeaders) {
        this(httpHeaders.size());
        httpHeaders.forEach((headerName, headerValues) -> put(headerName, new LinkedList(headerValues)));
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

    public List<MediaType> getAccept() {
        return MediaType.parseMediaTypes(this.get("Accept"));
    }
}
