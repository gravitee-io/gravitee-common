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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.support.GenericConversionService;
import org.springframework.core.env.MapPropertySource;
import org.springframework.util.Assert;

import java.util.Map;

/**
 * @author Nicolas GERAUD (nicolas.geraud at graviteesource.com) 
 * @author GraviteeSource Team
 *
 * @see org.springframework.core.env.SystemEnvironmentPropertySource
 */
public class RelaxedPropertySource extends MapPropertySource {

    @Autowired
    GenericConversionService conversionService;

    /**
     * Create a new {@code SystemEnvironmentPropertySource} with the given name and
     * delegating to the given {@code MapPropertySource}.
     */
    public RelaxedPropertySource(String name, Map<String, Object> source) {
        super(name, source);
    }


    /**
     * Return {@code true} if a property with the given name or any underscore/uppercase variant
     * thereof exists in this property source.
     */
    @Override
    public boolean containsProperty(String name) {
        boolean contains = (getProperty(name) != null);
        if (!contains && name.contains("[")) {
            return (getProperty(encodedArray(name)) != null);
        }

        return contains;
    }

    /**
     * This implementation returns {@code true} if a property with the given name or
     * any underscore/uppercase variant thereof exists in this property source.
     */
    @Override
    public Object getProperty(String name) {
        String actualName = resolvePropertyName(name);
        if (logger.isDebugEnabled() && !name.equals(actualName)) {
            logger.debug(String.format("PropertySource [%s] does not contain '%s', but found equivalent '%s'",
                    getName(), name, actualName));
        }
        return super.getProperty(actualName);
    }

    /**
     * Check to see if this property source contains a property with the given name, or
     * any underscore / uppercase variation thereof. Return the resolved name if one is
     * found or otherwise the original name. Never returns {@code null}.
     */
    private String resolvePropertyName(String name) {
        Assert.notNull(name, "Property name must not be null");
        String resolvedName = checkPropertyName(name);
        if (resolvedName != null) {
            return resolvedName;
        }
        String uppercasedName = name.toUpperCase();
        if (!name.equals(uppercasedName)) {
            resolvedName = checkPropertyName(uppercasedName);
            if (resolvedName != null) {
                return resolvedName;
            }
        }

        return name;
    }

    private String checkPropertyName(String name) {
        // Check name as-is
        if (containsKey(name)) {
            return name;
        }

        // Check name with just dots replaced
        String noDotName = name.replace('.', '_');
        if (!name.equals(noDotName) && containsKey(noDotName)) {
            return noDotName;
        }
        // Check name with just hyphens replaced
        String noHyphenName = name.replace("-", "");
        if (!name.equals(noHyphenName) && containsKey(noHyphenName)) {
            return noHyphenName;
        }
        // Check name with dots and hyphens replaced
        String noHyphenNameAndDotName = noDotName.replace("-", "");
        if (!name.equals(noHyphenNameAndDotName) && containsKey(noHyphenNameAndDotName)) {
            return noHyphenNameAndDotName;
        }
        // Check if name is an array
        if(name.split(":")[0].contains("[")) {
            return checkPropertyName(encodedArray(name));
        }

        // Give up
        return null;
    }
    private String encodedArray(String name) {
        String[] keyWithDefault = name.split(":");
        String encodedKey = keyWithDefault[0];
        if(keyWithDefault[0].contains("[")) {
            encodedKey = encodedKey
                    .replace("[", ".")
                    .replace("]", "");
        }
        return keyWithDefault.length == 1 ? encodedKey : encodedKey + ":" + keyWithDefault[1];
    }

    private boolean containsKey(String name) {
        return (isSecurityManagerPresent() ? this.source.keySet().contains(name) : this.source.containsKey(name));
    }

    protected boolean isSecurityManagerPresent() {
        return (System.getSecurityManager() != null);
    }

}
