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

/**
 * @author Azize Elamrani (azize dot elamrani at gmail dot com)
 */
public class MediaType {

    /**
     * The media type {@code charset} parameter name.
     */
    public static final String CHARSET_PARAMETER = "charset";
    /**
     * The value of a type or subtype wildcard {@value #MEDIA_TYPE_WILDCARD}.
     */
    public static final String MEDIA_TYPE_WILDCARD = "*";
    /**
     * A {@code String} constant representing wildcard {@value #WILDCARD} media type .
     */
    public final static String WILDCARD = "*/*";
    /**
     * A {@code String} constant representing {@value #APPLICATION_XML} media type.
     */
    public final static String APPLICATION_XML = "application/xml";
    /**
     * A {@code String} constant representing {@value #APPLICATION_ATOM_XML} media type.
     */
    public final static String APPLICATION_ATOM_XML = "application/atom+xml";
    /**
     * A {@code String} constant representing {@value #APPLICATION_XHTML_XML} media type.
     */
    public final static String APPLICATION_XHTML_XML = "application/xhtml+xml";
    /**
     * A {@code String} constant representing {@value #APPLICATION_SVG_XML} media type.
     */
    public final static String APPLICATION_SVG_XML = "application/svg+xml";
    /**
     * A {@code String} constant representing {@value #APPLICATION_JSON} media type.
     */
    public final static String APPLICATION_JSON = "application/json";
    /**
     * A {@code String} constant representing {@value #APPLICATION_JWT} media type.
     */
    public final static String APPLICATION_JWT = "application/jwt";
    /**
     * A {@code String} constant representing {@value #APPLICATION_FORM_URLENCODED} media type.
     */
    public final static String APPLICATION_FORM_URLENCODED = "application/x-www-form-urlencoded";
    /**
     * A {@code String} constant representing {@value #MULTIPART_FORM_DATA} media type.
     */
    public final static String MULTIPART_FORM_DATA = "multipart/form-data";
    /**
     * A {@code String} constant representing {@value #APPLICATION_OCTET_STREAM} media type.
     */
    public final static String APPLICATION_OCTET_STREAM = "application/octet-stream";
    /**
     * A {@code String} constant representing {@value #TEXT_PLAIN} media type.
     */
    public final static String TEXT_PLAIN = "text/plain";
    /**
     * A {@code String} constant representing {@value #TEXT_XML} media type.
     */
    public final static String TEXT_XML = "text/xml";
    /**
     * A {@code String} constant representing {@value #TEXT_HTML} media type.
     */
    public final static String TEXT_HTML = "text/html";
}
