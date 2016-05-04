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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author David BRASSELY (david at gravitee.io)
 * @author GraviteeSource Team
 */
public class TemplatedValueHashMap extends HashMap<String, String> {

    private final static Pattern PATTERN = Pattern.compile("\\{\\{(.*?)\\}\\}");

    @Override
    public String get(Object key) {
        String value;
        return (value = super.get(key)) == null ? null : resolve(value);
    }

    @Override
    public String getOrDefault(Object key, String defaultValue) {
        String value;
        return (value = super.getOrDefault(key, defaultValue)) == null ? null : resolve(value);
    }

    private String resolve(String initialValue) {
        if (initialValue == null || initialValue.isEmpty()) {
            return initialValue;
        }

        Matcher matcher = PATTERN.matcher(initialValue);
        StringBuffer sb = new StringBuffer(initialValue.length());
        while (matcher.find()) {
            String text = matcher.group(1);
            String value = this.get(text);
            if (value != null) {
                matcher.appendReplacement(sb, Matcher.quoteReplacement(value));
            } else {
                throw new IllegalStateException("A value is missing for key [" + text + "]");
            }
        }

        matcher.appendTail(sb);
        return sb.toString();
    }
}
