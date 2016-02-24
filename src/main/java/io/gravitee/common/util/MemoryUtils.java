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

import java.security.AccessController;
import java.security.PrivilegedAction;

/**
 * MemoryUtils provides an abstraction over memory properties and operations.
 */
public class MemoryUtils
{
    private static final int cacheLineBytes;
    static
    {
        final int defaultValue = 64;
        int value = defaultValue;
        try
        {
            value = Integer.parseInt(AccessController.doPrivileged(new PrivilegedAction<String>()
            {
                @Override
                public String run()
                {
                    return System.getProperty("org.eclipse.jetty.util.cacheLineBytes", String.valueOf(defaultValue));
                }
            }));
        }
        catch (Exception ignored)
        {
        }
        cacheLineBytes = value;
    }

    private MemoryUtils()
    {
    }

    public static int getCacheLineBytes()
    {
        return cacheLineBytes;
    }

    public static int getIntegersPerCacheLine()
    {
        return getCacheLineBytes() >> 2;
    }

    public static int getLongsPerCacheLine()
    {
        return getCacheLineBytes() >> 3;
    }
}

