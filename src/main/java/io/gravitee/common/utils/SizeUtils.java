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
package io.gravitee.common.utils;

/**
 * @author Jeoffrey HAEYAERT (jeoffrey.haeyaert at graviteesource.com)
 * @author GraviteeSource Team
 */
public class SizeUtils {

    /**
     * Convert a size defined as a string such as '10KB', '10MB', '10GB' or '10B' or just '10' into integer byte value.
     * For backward compatibility, we are considering that default unit is MB.
     *
     * @param stringValue size, can be a simple number (considered as MB) or a string containing value and unit like `20KB`.
     *
     * @return the value converted in bytes as a long or -1 if specified value is <code>null</code> (meaning infinite value).
     * @throws NumberFormatException in case the specified value cannot be converted.
     */
    public static long toBytes(String stringValue) throws NumberFormatException {
        long valueBytes = -1;
        if (stringValue != null) {
            try {
                int value = Integer.parseInt(stringValue);
                if (value >= 0) {
                    // By default, consider MB
                    valueBytes = Integer.parseInt(stringValue) * (1024 * 1024);
                }
            } catch (NumberFormatException nfe) {
                stringValue = stringValue.toUpperCase();

                try {
                    if (stringValue.endsWith("G") || stringValue.endsWith("GB")) {
                        long value = Long.parseLong(stringValue.substring(0, stringValue.indexOf('G')));
                        valueBytes = value * (1024 * 1024 * 1024);
                    } else if (stringValue.endsWith("MB") || stringValue.endsWith("M")) {
                        long value = Long.parseLong(stringValue.substring(0, stringValue.indexOf('M')));
                        valueBytes = value * (1024 * 1024);
                    } else if (stringValue.endsWith("KB") || stringValue.endsWith("K")) {
                        long value = Long.parseLong(stringValue.substring(0, stringValue.indexOf('K')));
                        valueBytes = value * (1024);
                    } else if (stringValue.endsWith("B")) {
                        valueBytes = Long.parseLong(stringValue.substring(0, stringValue.indexOf('B')));
                    } else {
                        throw nfe;
                    }
                } catch (NumberFormatException nfe2) {
                    throw nfe;
                }
            }
        }

        return valueBytes;
    }
}
