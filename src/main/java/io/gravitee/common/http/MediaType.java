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

import org.springframework.util.*;

import java.nio.charset.Charset;
import java.util.*;

/**
 * @author Azize Elamrani (azize dot elamrani at gmail dot com)
 * @author David BRASSELY (david.brassely at graviteesource.com)
 * @author GraviteeSource Team
 */
public class MediaType {

    private final String type;

    private final String subtype;

    private final Map<String, String> parameters;

    /**
     * The media type {@code charset} parameter name.
     */
    public static final String CHARSET_PARAMETER = "charset";

    /**
     * The value of a type or subtype wildcard {@value #MEDIA_TYPE_WILDCARD}.
     */
    public static final String MEDIA_TYPE_WILDCARD = "*";
    public final static String WILDCARD = "*/*";
    public static final MediaType MEDIA_ALL = new MediaType("*", "*");

    /**
     * A {@code String} constant representing {@value #APPLICATION_XML} media type.
     */
    public final static String APPLICATION_XML = "application/xml";
    public static final MediaType MEDIA_APPLICATION_XML = new MediaType("application", "xml");

    /**
     * A {@code String} constant representing {@value #APPLICATION_ATOM_XML} media type.
     */
    public final static String APPLICATION_ATOM_XML = "application/atom+xml";
    public static final MediaType MEDIA_APPLICATION_ATOM_XML = new MediaType("application", "atom+xml");

    /**
     * A {@code String} constant representing {@value #APPLICATION_XHTML_XML} media type.
     */
    public final static String APPLICATION_XHTML_XML = "application/xhtml+xml";
    public static final MediaType MEDIA_APPLICATION_XHTML_XML = new MediaType("application", "xhtml+xml");

    /**
     * A {@code String} constant representing {@value #APPLICATION_SVG_XML} media type.
     */
    public final static String APPLICATION_SVG_XML = "application/svg+xml";
    public static final MediaType MEDIA_APPLICATION_SVG_XML = new MediaType("application", "svg+xml");

    /**
     * A {@code String} constant representing {@value #APPLICATION_JSON} media type.
     */
    public final static String APPLICATION_JSON = "application/json";
    public static final MediaType MEDIA_APPLICATION_JSON = new MediaType("application", "json");

    /**
     * A {@code String} constant representing {@value #APPLICATION_JWT} media type.
     */
    public final static String APPLICATION_JWT = "application/jwt";
    public static final MediaType MEDIA_APPLICATION_JWT = new MediaType("application", "jwt");

    /**
     * A {@code String} constant representing {@value #APPLICATION_FORM_URLENCODED} media type.
     */
    public final static String APPLICATION_FORM_URLENCODED = "application/x-www-form-urlencoded";
    public static final MediaType MEDIA_APPLICATION_FORM_URLENCODED = new MediaType("application", "x-www-form-urlencoded");

    /**
     * A {@code String} constant representing {@value #MULTIPART_FORM_DATA} media type.
     */
    public final static String MULTIPART_FORM_DATA = "multipart/form-data";
    public static final MediaType MEDIA_MULTIPART_FORM_DATA = new MediaType("multipart", "form-data");

    /**
     * A {@code String} constant representing {@value #APPLICATION_OCTET_STREAM} media type.
     */
    public final static String APPLICATION_OCTET_STREAM = "application/octet-stream";
    public static final MediaType MEDIA_APPLICATION_OCTET_STREAM = new MediaType("application", "octet-stream");

    /**
     * A {@code String} constant representing {@value #TEXT_PLAIN} media type.
     */
    public final static String TEXT_PLAIN = "text/plain";
    public static final MediaType MEDIA_TEXT_PLAIN = new MediaType("text", "plain");

    /**
     * A {@code String} constant representing {@value #TEXT_XML} media type.
     */
    public final static String TEXT_XML = "text/xml";
    public static final MediaType MEDIA_TEXT_XML = new MediaType("text", "xml");

    /**
     * A {@code String} constant representing {@value #TEXT_HTML} media type.
     */
    public final static String TEXT_HTML = "text/html";
    public static final MediaType MEDIA_TEXT_HTML = new MediaType("text", "html");

    public static final String TEXT_EVENT_STREAM = "text/event-stream";
    public static final MediaType MEDIA_TEXT_EVENT_STREAM = new MediaType("text", "event-stream");

    public static final String APPLICATION_GRPC = "application/grpc";
    public static final MediaType MEDIA_APPLICATION_GRPC = new MediaType("application", "grpc");

    public String QUALITY_FACTOR_PARAMETER = "q";

    public MediaType(String type, String subtype) {
        this(type, subtype, Collections.emptyMap());
    }

    public MediaType(String type, String subtype, Map<String, String> parameters) {
        this.type = type;
        this.subtype = subtype;
        this.parameters = parameters;
    }

    public boolean isWildcardType() {
        return MEDIA_TYPE_WILDCARD.equals(this.getType());
    }

    public boolean isWildcardSubtype() {
        return MEDIA_TYPE_WILDCARD.equals(this.getSubtype()) || this.getSubtype().startsWith("*+");
    }

    public boolean isConcrete() {
        return !this.isWildcardType() && !this.isWildcardSubtype();
    }

    public String getType() {
        return this.type;
    }

    public String getSubtype() {
        return this.subtype;
    }

    public Charset getCharset() {
        String charset = this.getParameter(CHARSET_PARAMETER);
        return charset != null ? Charset.forName(this.unquote(charset)) : null;
    }

    public String getParameter(String name) {
        return (String)this.parameters.get(name);
    }

    public Map<String, String> getParameters() {
        return this.parameters;
    }

    private boolean isQuotedString(String s) {
        if (s.length() < 2) {
            return false;
        } else {
            return s.startsWith("\"") && s.endsWith("\"") || s.startsWith("'") && s.endsWith("'");
        }
    }

    protected String unquote(String s) {
        return this.isQuotedString(s) ? s.substring(1, s.length() - 1) : s;
    }

    public double getQualityFactor() {
        String qualityFactor = this.getParameter(QUALITY_FACTOR_PARAMETER);
        return qualityFactor != null ? Double.parseDouble(this.unquote(qualityFactor)) : 1.0D;
    }

    public static List<MediaType> parseMediaTypes(List<String> mediaTypes) {
        if (CollectionUtils.isEmpty(mediaTypes)) {
            return Collections.emptyList();
        } else if (mediaTypes.size() == 1) {
            return parseMediaTypes(mediaTypes.get(0));
        } else {
            List<MediaType> result = new ArrayList<>(8);
            Iterator var2 = mediaTypes.iterator();

            while (var2.hasNext()) {
                String mediaType = (String)var2.next();
                result.addAll(parseMediaTypes(mediaType));
            }

            return result;
        }
    }

    public static List<MediaType> parseMediaTypes(String mediaTypes) {
        if (mediaTypes == null || mediaTypes.isEmpty()) {
            return Collections.emptyList();
        } else {
            List<String> tokenizedTypes = MimeTypeUtils.tokenize(mediaTypes);
            List<MediaType> result = new ArrayList<>(tokenizedTypes.size());
            Iterator var3 = tokenizedTypes.iterator();

            while (var3.hasNext()) {
                String type = (String)var3.next();
                if (StringUtils.hasText(type)) {
                    result.add(parseMediaType(type));
                }
            }

            return result;
        }
    }

    public static MediaType parseMediaType(String mediaType) {
        MimeType type;
        try {
            type = MimeTypeUtils.parseMimeType(mediaType);
        } catch (InvalidMimeTypeException var4) {
            return null;
        }

        try {
            return new MediaType(type.getType(), type.getSubtype(), type.getParameters());
        } catch (IllegalArgumentException var3) {
            return null;
        }
    }

    public static void sortByQualityValue(List<MediaType> mediaTypes) {
        if (mediaTypes != null && mediaTypes.size() > 1) {
            mediaTypes.sort(QUALITY_FACTOR_COMPARATOR);
        }
    }

    @Override
    public int hashCode() {
        int result = this.type.hashCode();
        result = 31 * result + this.subtype.hashCode();
        result = 31 * result + this.parameters.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof MediaType)) {
            return false;
        }
        MediaType otherType = (MediaType) other;
        return (this.type.equalsIgnoreCase(otherType.type) &&
                this.subtype.equalsIgnoreCase(otherType.subtype));
    }

    public String toMediaString() {
        return this.type + '/' + this.subtype;
    }

    public static final Comparator<MediaType> QUALITY_FACTOR_COMPARATOR = (mediaType1, mediaType2) -> {
        double quality1 = mediaType1.getQualityFactor();
        double quality2 = mediaType2.getQualityFactor();
        int qualityComparison = Double.compare(quality2, quality1);
        if (qualityComparison != 0) {
            return qualityComparison;
        } else if (mediaType1.isWildcardType() && !mediaType2.isWildcardType()) {
            return 1;
        } else if (mediaType2.isWildcardType() && !mediaType1.isWildcardType()) {
            return -1;
        } else if (!mediaType1.getType().equals(mediaType2.getType())) {
            return 0;
        } else if (mediaType1.isWildcardSubtype() && !mediaType2.isWildcardSubtype()) {
            return 1;
        } else if (mediaType2.isWildcardSubtype() && !mediaType1.isWildcardSubtype()) {
            return -1;
        } else if (!mediaType1.getSubtype().equals(mediaType2.getSubtype())) {
            return 0;
        } else {
            int paramsSize1 = mediaType1.getParameters().size();
            int paramsSize2 = mediaType2.getParameters().size();
            return Integer.compare(paramsSize2, paramsSize1);
        }
    };
}
