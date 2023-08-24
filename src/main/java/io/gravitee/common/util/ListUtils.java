/*
 * Copyright © 2015 The Gravitee team (http://gravitee.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.gravitee.common.util;

import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author Yann TAVERNIER (yann.tavernier at graviteesource.com)
 * @author GraviteeSource Team
 */
public class ListUtils {

    private static final String COMMA_SEPARATOR = ",";
    private static final Pattern COMMA_SPLITTER = Pattern.compile(COMMA_SEPARATOR);

    /**
     * Return the object as an immutable {@link List}.
     * This method acts differently depending on the type of the object's value.
     * Elements of the {@link List} are mapped as follows:
     * <p>
     * <b>For String:</b>
     *     <ul>
     *          <li>comma separated string are spitted and returned trimmed</li>
     *          <li>parsable JSON Array elements are returned as strings (including objects)</li>
     *          <li>if none of the above, the string is simply wrapped and trimmed</li>
     *      </ul>
     * </p>
     * <p>
     *     <b>For Collection:</b>
     *     <ul>
     *         <li>Elements of the collection are wrapped in a new List</li>
     *     </ul>
     * </p>
     * <p>
     *     <b>For Array:</b>
     *     <ul>
     *         <li>Each elements of the array is wrapped into the returned List</li>
     *     </ul>
     * </p>
     * <p>
     * <b>For any other cases:</b> the value is simply wrapped into the returned List.
     * <b>Notes:</b>
     * Returns an immutable collection
     * </p>
     *
     * @param <T>  the expected type of the value. Specify {@link Object} if you expect value of any type.
     * @return an immutable List with the attribute values.
     */
    public static <T> List<T> toList(Object object) {
        if (object == null) {
            return null;
        }

        if (object instanceof String string) {
            if (isJSONArray(string)) {
                JsonArray jsonArray = (JsonArray) Json.decodeValue(string);
                return (List<T>) jsonArray.getList().stream().map(Objects::toString).collect(Collectors.toUnmodifiableList());
            } else {
                final String[] splits = COMMA_SPLITTER.split(string);
                for (int i = 0; i < splits.length; i++) {
                    splits[i] = splits[i].trim();
                }
                return (List<T>) List.of(splits);
            }
        }

        if (object instanceof Collection) {
            // copy to immutable list
            return List.copyOf((Collection<? extends T>) object);
        }

        if (object.getClass().isArray()) {
            return List.of((T[]) object);
        }

        return List.of((T) object);
    }

    private static boolean isJSONArray(String jsonCandidate) {
        final String trimmedJsonCandidate = jsonCandidate.trim();
        return trimmedJsonCandidate.startsWith("[") && trimmedJsonCandidate.endsWith("]");
    }
}
