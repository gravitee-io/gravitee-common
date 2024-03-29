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
package io.gravitee.common.http;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * @author David BRASSELY (david.brassely at graviteesource.com)
 * @author GraviteeSource Team
 */
public class MediaTypeTest {

    @Test
    public void shouldParseNull() {
        MediaType type = MediaType.parseMediaType(null);
        assertNull(type);
    }

    @Test
    public void shouldParseEmpty() {
        MediaType type = MediaType.parseMediaType("");
        assertNull(type);
    }

    @Test
    public void shouldParseSingle() {
        MediaType type = MediaType.parseMediaType("application/json");
        assertNotNull(type);
        assertEquals(MediaType.MEDIA_APPLICATION_JSON, type);
    }

    @Test
    public void shouldParseMultipleEmptyString() {
        List<MediaType> types = MediaType.parseMediaTypes("");
        assertEquals(Collections.emptyList(), types);
    }

    @Test
    public void shouldParseMultipleEmptyList() {
        List<MediaType> types = MediaType.parseMediaTypes(Collections.emptyList());
        assertEquals(Collections.emptyList(), types);
    }

    @Test
    public void shouldParseMultiple() {
        List<MediaType> types = MediaType.parseMediaTypes("text/html, application/xhtml+xml, application/xml;q=0.9, */*;q=0.8");
        assertNotNull(types);
        assertEquals(4, types.size());

        assertEquals(0.9d, types.get(2).getQualityFactor(), 0.0d);
        assertEquals(0.8d, types.get(3).getQualityFactor(), 0.0d);
    }

    @Test
    public void shouldParseMultiple_sortByQualityFactor() {
        List<MediaType> types = MediaType.parseMediaTypes("text/html, application/xhtml+xml, */*;q=0.8, application/xml;q=0.9");
        MediaType.sortByQualityValue(types);

        assertNotNull(types);
        assertEquals(4, types.size());

        assertEquals(0.9d, types.get(2).getQualityFactor(), 0.0d);
        assertEquals(0.8d, types.get(3).getQualityFactor(), 0.0d);
    }
}
