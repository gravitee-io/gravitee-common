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
package io.gravitee.common.sse;

import io.gravitee.gateway.api.buffer.Buffer;
import io.reactivex.rxjava3.annotations.Nullable;

public record ServerEvent(@Nullable String event, @Nullable String data, @Nullable String id, @Nullable Long retry) {
    public ServerEvent(@Nullable String data) {
        this(null, data, null, null);
    }

    public boolean isEmpty() {
        return event == null && data == null && id == null && retry == null;
    }

    public static ServerEvent parse(@Nullable Buffer buffer) {
        if (buffer == null) {
            return new ServerEvent(null, null, null, null);
        }
        String event = null;
        StringBuilder data = new StringBuilder();
        String id = null;
        Long retry = null;
        for (String line : buffer.toString().split("\n")) {
            if (line.startsWith("event:")) {
                event = line.substring(6).trim();
            } else if (line.startsWith("data:")) {
                if (!data.isEmpty()) {
                    data.append('\n');
                }
                data.append(line.substring(5).trim());
            } else if (line.startsWith("id:")) {
                id = line.substring(3).trim();
            } else if (line.startsWith("retry:")) {
                try {
                    retry = Long.parseLong(line.substring(6).trim());
                } catch (NumberFormatException ignored) {
                    // Per SSE spec, ignore the field if the value is not a valid integer.
                }
            }
        }

        String strData = data.isEmpty() ? null : data.toString();

        return new ServerEvent(event, strData, id, retry);
    }

    public Buffer toBuffer() {
        StringBuilder buffer = new StringBuilder();
        if (event != null) {
            buffer.append("event: ").append(event).append('\n');
        }
        if (data != null) {
            for (String line : data.split("\n")) {
                buffer.append("data: ").append(line).append('\n');
            }
        }
        if (id != null) {
            buffer.append("id: ").append(id).append('\n');
        }
        if (retry != null) {
            buffer.append("retry: ").append(retry).append('\n');
        }
        return buffer.isEmpty() ? Buffer.buffer() : Buffer.buffer(buffer.append('\n').toString());
    }
}
