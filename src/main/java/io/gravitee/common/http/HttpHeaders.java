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

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

/**
 * @author David BRASSELY (david.brassely at graviteesource.com)
 * @author GraviteeSource Team
 */
public interface HttpHeaders {

    /**
     * See {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.1">HTTP/1.1 documentation</a>}.
     */
    String ACCEPT = "Accept";
    /**
     * See {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.2">HTTP/1.1 documentation</a>}.
     */
    String ACCEPT_CHARSET = "Accept-Charset";
    /**
     * See {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.3">HTTP/1.1 documentation</a>}.
     */
    String ACCEPT_ENCODING = "Accept-Encoding";
    /**
     * See {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.4">HTTP/1.1 documentation</a>}.
     */
    String ACCEPT_LANGUAGE = "Accept-Language";
    /**
     * See {@link <a href="https://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.5">HTTP/1.1 documentation</a>}.
     */
    String ACCEPT_RANGES = "Accept-Ranges";
    /**
     * See {@link <a href="https://www.w3.org/TR/cors/#access-control-allow-credentials-response-header">CORS documentation</a>}.
     */
    String ACCESS_CONTROL_ALLOW_CREDENTIALS = "Access-Control-Allow-Credentials";
    /**
     * See {@link <a href="https://www.w3.org/TR/cors/#access-control-allow-headers-response-header">CORS documentation</a>}.
     */
    String ACCESS_CONTROL_ALLOW_HEADERS = "Access-Control-Allow-Headers";
    /**
     * See {@link <a href="https://www.w3.org/TR/cors/#access-control-allow-methods-response-header">CORS documentation</a>}.
     */
    String ACCESS_CONTROL_ALLOW_METHODS = "Access-Control-Allow-Methods";
    /**
     * See {@link <a href="https://www.w3.org/TR/cors/#http-access-control-allow-origin">CORS documentation</a>}.
     */
    String ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";
    /**
     * See {@link <a href="https://www.w3.org/TR/cors/#access-control-expose-headers-response-header">CORS documentation</a>}.
     */
    String ACCESS_CONTROL_EXPOSE_HEADERS = "Access-Control-Expose-Headers";
    /**
     * See {@link <a href="https://www.w3.org/TR/cors/#access-control-max-age-response-header">CORS documentation</a>}.
     */
    String ACCESS_CONTROL_MAX_AGE = "Access-Control-Max-Age";
    /**
     * See {@link <a href="https://www.w3.org/TR/cors/#access-control-request-headers-request-header">CORS documentation</a>}.
     */
    String ACCESS_CONTROL_REQUEST_HEADERS = "Access-Control-Request-Headers";
    /**
     * See {@link <a href="https://www.w3.org/TR/cors/#access-control-request-method-request-header">CORS documentation</a>}.
     */
    String ACCESS_CONTROL_REQUEST_METHOD = "Access-Control-Request-Method";
    /**
     * See {@link <a href="https://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.6">HTTP/1.1 documentation</a>}.
     */
    String AGE = "Age";
    /**
     * See {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.7">HTTP/1.1 documentation</a>}.
     */
    String ALLOW = "Allow";
    /**
     * See {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.8">HTTP/1.1 documentation</a>}.
     */
    String AUTHORIZATION = "Authorization";
    /**
     * See {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.9">HTTP/1.1 documentation</a>}.
     */
    String CACHE_CONTROL = "Cache-Control";
    /**
     * See {@link <a href="https://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.10">HTTP/1.1 documentation</a>}.
     */
    String CONNECTION = "Connection";
    /**
     * See {@link <a href="http://tools.ietf.org/html/rfc2183">IETF RFC-2183</a>}.
     */
    String CONTENT_DISPOSITION = "Content-Disposition";
    /**
     * See {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.11">HTTP/1.1 documentation</a>}.
     */
    String CONTENT_ENCODING = "Content-Encoding";
    /**
     * See {@link <a href="http://tools.ietf.org/html/rfc2392">IETF RFC-2392</a>}.
     */
    String CONTENT_ID = "Content-ID";
    /**
     * See {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.12">HTTP/1.1 documentation</a>}.
     */
    String CONTENT_LANGUAGE = "Content-Language";
    /**
     * See {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.13">HTTP/1.1 documentation</a>}.
     */
    String CONTENT_LENGTH = "Content-Length";
    /**
     * See {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.14">HTTP/1.1 documentation</a>}.
     */
    String CONTENT_LOCATION = "Content-Location";
    /**
     * See {@link <a href="https://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.15">HTTP/1.1 documentation</a>}.
     */
    String CONTENT_MD5 = "Content-MD5";
    /**
     * See {@link <a href="https://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.16">HTTP/1.1 documentation</a>}.
     */
    String CONTENT_RANGE = "Content-Range";
    /**
     * See {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.17">HTTP/1.1 documentation</a>}.
     */
    String CONTENT_TYPE = "Content-Type";
    /**
     * See {@link <a href="http://www.ietf.org/rfc/rfc2109.txt">IETF RFC 2109</a>}.
     */
    String COOKIE = "Cookie";
    /**
     * See {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.18">HTTP/1.1 documentation</a>}.
     */
    String DATE = "Date";
    /**
     * See {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.19">HTTP/1.1 documentation</a>}.
     */
    String ETAG = "ETag";
    /**
     * See {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.21">HTTP/1.1 documentation</a>}.
     */
    String EXPIRES = "Expires";
    /**
     * See {@link <a href="https://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.20">HTTP/1.1 documentation</a>}.
     */
    String EXPECT = "Expect";
    /**
     * See {@link <a href="https://tools.ietf.org/html/rfc7239">IETF RFC 7239</a>}.
     */
    String FORWARDED = "Forwarded";
    /**
     * See {@link <a href="https://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.22">HTTP/1.1 documentation</a>}.
     */
    String FROM = "From";
    /**
     * See {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.23">HTTP/1.1 documentation</a>}.
     */
    String HOST = "Host";
    /**
     * See {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.24">HTTP/1.1 documentation</a>}.
     */
    String IF_MATCH = "If-Match";
    /**
     * See {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.25">HTTP/1.1 documentation</a>}.
     */
    String IF_MODIFIED_SINCE = "If-Modified-Since";
    /**
     * See {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.26">HTTP/1.1 documentation</a>}.
     */
    String IF_NONE_MATCH = "If-None-Match";
    /**
     * See {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.28">HTTP/1.1 documentation</a>}.
     */
    String IF_UNMODIFIED_SINCE = "If-Unmodified-Since";
    /**
     * See {@link <a href="https://www.w3.org/Protocols/HTTP/1.1/draft-ietf-http-v11-spec-01#Keep-Alive">HTTP/1.1 documentation</a>}.
     */
    String KEEP_ALIVE = "Keep-Alive";
    /**
     * See {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.29">HTTP/1.1 documentation</a>}.
     */
    String LAST_MODIFIED = "Last-Modified";
    /**
     * See {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.30">HTTP/1.1 documentation</a>}.
     */
    String LOCATION = "Location";
    /**
     * See {@link <a href="http://tools.ietf.org/html/rfc5988#page-6">Web Linking (IETF RFC-5988) documentation</a>}.
     */
    String LINK = "Link";
    /**
     * See {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.31">HTTP/1.1 documentation</a>}.
     */
    String MAX_FORWARDS = "Max-Forwards";
    /**
     * See {@link <a href="https://www.w3.org/Protocols/rfc1341/3_MIME-Version.html">HTTP/1.1 documentation</a>}.
     */
    String MIME_VERSION = "MIME-Version";
    /**
     * https://tools.ietf.org/id/draft-abarth-origin-03.html#rfc.section.2
     */
    String ORIGIN = "Origin";
    /**
     * See {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.32">HTTP/1.1 documentation</a>}.
     */
    String PRAGMA = "Pragma";
    /**
     * See {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.33">HTTP/1.1 documentation</a>}.
     */
    String PROXY_AUTHENTICATE = "Proxy-Authenticate";
    /**
     * See {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.34">HTTP/1.1 documentation</a>}.
     */
    String PROXY_AUTHORIZATION = "Proxy-Authorization";
    String PROXY_CONNECTION = "Proxy-Connection";
    /**
     * See {@link <a href="https://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.35">HTTP/1.1 documentation</a>}.
     */
    String RANGE = "Range";
    /**
     * See {@link <a href="https://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.36">HTTP/1.1 documentation</a>}.
     */
    String REFERER = "Referer";
    /**
     * See {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.37">HTTP/1.1 documentation</a>}.
     */
    String RETRY_AFTER = "Retry-After";
    /**
     * See {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.38">HTTP/1.1 documentation</a>}.
     */
    String SERVER = "Server";
    /**
     * See {@link <a href="http://www.ietf.org/rfc/rfc2109.txt">IETF RFC 2109</a>}.
     */
    String SET_COOKIE = "Set-Cookie";
    /**
     * See {@link <a href="https://www.ietf.org/rfc/rfc2965.txt">IETF RFC 2965</a>}.
     */
    String SET_COOKIE2 = "Set-Cookie2";
    /**
     * See {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.39">HTTP/1.1 documentation</a>}.
     */
    String TE = "TE";
    /**
     * See {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.40">HTTP/1.1 documentation</a>}.
     */
    String TRAILER = "Trailer";
    /**
     * See {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.41">HTTP/1.1 documentation</a>}.
     */
    String TRANSFER_ENCODING = "Transfer-Encoding";
    /**
     * See {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.42">HTTP/1.1 documentation</a>}.
     */
    String UPGRADE = "Upgrade";
    /**
     * See {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.43">HTTP/1.1 documentation</a>}.
     */
    String USER_AGENT = "User-Agent";
    /**
     * See {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.44">HTTP/1.1 documentation</a>}.
     */
    String VARY = "Vary";
    /**
     * See {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.45">HTTP/1.1 documentation</a>}.
     */
    String VIA = "Via";
    /**
     * See {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.46">HTTP/1.1 documentation</a>}.
     */
    String WARNING = "Warning";
    /**
     * See {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.47">HTTP/1.1 documentation</a>}.
     */
    String WWW_AUTHENTICATE = "WWW-Authenticate";

    String X_FORWARDED_FOR = "X-Forwarded-For";
    String X_FORWARDED_PROTO = "X-Forwarded-Proto";
    String X_FORWARDED_SERVER = "X-Forwarded-Server";
    String X_FORWARDED_HOST = "X-Forwarded-Host";

    int size();

    boolean isEmpty();

    /**
     * @deprecated this method will break in future version, please use {@code contains()} instead.
     */
    @Deprecated
    default boolean containsKey(String name) {
        return contains(name);
    }

    boolean contains(String name);

    List<String> get(String name);

    HttpHeaders put(String name, List<String> values);

    List<String> remove(String name);

    void clear();

    /**
     * @deprecated this method will break in future version, please use {@code names()} instead.
     */
    @Deprecated
    default Set<String> keySet() {
        return names();
    }

    Set<String> names();

    /**
     * @deprecated this method will break in future version, please use {@code entries()} instead.
     */
    @Deprecated
    default List<Map.Entry<String, List<String>>> entrySet() {
        return entries();
    }

    List<Map.Entry<String, List<String>>> entries();

    void forEach(BiConsumer<String, String> action);

    String getFirst(String name);

    HttpHeaders add(String name, String value);

    HttpHeaders set(String name, String value);

    HttpHeaders setAll(Map<String, String> values);
}
