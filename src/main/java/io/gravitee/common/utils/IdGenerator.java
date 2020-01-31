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
package io.gravitee.common.utils;

import java.text.Normalizer;

/**
 * @author David BRASSELY (brasseld at graviteesource.com)
 * @author Jeoffrey HAEYAERT (jeoffrey.haeyaert at graviteesource.com)
 * @author GraviteeSource Team
 */
public final class IdGenerator {

    /**
     * Generate a human readable identifier from a name.
     * It basically replace all accented characters by its corresponding regular letter, removes all non alphabetic characters,
     * replace all spaces with a dash ('-').
     * Ex: 'Héhé It's my wonderful name !' will result to "hehe-its-my-wonderful-name".
     *
     * <b>Important note</b>: this method only apply a basic transformation and do not guaranty the uniqueness of id.
     * So, 'It is not unique !' and 'it IS not UNIQUE' will result to the same 'it-is-not-unique' id.
     * Then, you still have to check uniqueness on your side.
     *
     * @param name the name to generate from.
     *
     * @return a string which can be used as human readable id.
     */
    public static String generate(String name) {

        if(name == null) {
            return null;
        }

        return Normalizer.normalize(name, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .toLowerCase()
                .replaceAll("[^a-z\\d\\s-]", "")
                .trim()
                .replaceAll("[^a-z\\d]+", "-");
    }
}
