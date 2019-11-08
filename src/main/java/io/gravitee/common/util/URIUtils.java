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

/**
 * @author Nicolas GERAUD (nicolas.geraud at graviteesource.com)
 * @author GraviteeSource Team
 */
public class URIUtils {

    private static final char FRAGMENT_SEPARATOR_CHAR = '#';
    private static final char QUERYPARAM_SEPARATOR_CHAR1 = '&';
    private static final char QUERYPARAM_SEPARATOR_CHAR2 = ';';
    private static final char QUERYPARAM_VALUE_SEPARATOR_CHAR = '=';
    private static final char QUERY_SEPARATOR_CHAR = '?';

    public static MultiValueMap<String, String> parameters(String uri) {
        MultiValueMap<String, String> queryParameters;
        int questionMarkIndex = uri.indexOf(QUERY_SEPARATOR_CHAR);
        if (questionMarkIndex < 0 || uri.length() == (questionMarkIndex + 1)) {
            queryParameters = new LinkedMultiValueMap<>(0);
        } else {
            queryParameters = new LinkedMultiValueMap<>();
            int end = uri.length();
            int i;
            int paramNameStart = questionMarkIndex + 1;
            int paramValueStart = -1;
            loop:
            for (i = questionMarkIndex + 1; i < end; i++) {
                switch (uri.charAt(i)) {
                    case QUERYPARAM_VALUE_SEPARATOR_CHAR:
                        if (paramNameStart == i) {
                            paramNameStart = i + 1;
                        } else if (paramValueStart < paramNameStart) {
                            paramValueStart = i + 1;
                        }
                        break;
                    case QUERYPARAM_SEPARATOR_CHAR1:
                    case QUERYPARAM_SEPARATOR_CHAR2:
                        addParameter(queryParameters, uri, paramNameStart, paramValueStart, i);
                        paramNameStart = i + 1;
                        break;

                    case FRAGMENT_SEPARATOR_CHAR:
                        break loop;
                    default:
                        // continue
                }
            }
            if (paramNameStart < i) {
                addParameter(queryParameters, uri, paramNameStart, paramValueStart, i);
            }
        }
        return queryParameters;
    }


    private static void addParameter(MultiValueMap<String, String> queryParameters, String uri, int paramNameStart, int paramValueStart, int end) {
        if (paramValueStart == -1 || paramValueStart < paramNameStart) {
            queryParameters.add(uri.substring(paramNameStart, end), null);
        } else {
            String k = uri.substring(paramNameStart, paramValueStart - 1);
            String v = uri.substring(paramValueStart, end);
            queryParameters.add(k, v);
        }
    }
}
