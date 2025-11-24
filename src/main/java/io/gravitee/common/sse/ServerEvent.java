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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public record ServerEvent(
    @Nullable String event,
    @Nullable String data,
    @Nullable String id,
    @Nullable Long retry,
    @Nullable Buffer cachedBuffer
) {
    public ServerEvent(@Nullable String event, @Nullable String data, @Nullable String id, @Nullable Long retry) {
        this(event, data, id, retry, null);
    }

    public ServerEvent(@Nullable String data) {
        this(null, data, null, null, null);
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

        for (Buffer lineBuffer : BufferUtils.split(buffer, new byte[] { '\n' })) {
            String line = lineBuffer.toString().strip();
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

        return new ServerEvent(event, strData, id, retry, buffer);
    }

    public Buffer toBuffer() {
        if (cachedBuffer != null) {
            return cachedBuffer;
        }

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

    /**
     * Create a new ServerEvent with the given data, attempting to preserve the original buffer's structure.
     * @param data the new data
     * @return a new ServerEvent instance with the updated data
     */
    public ServerEvent withData(String data) {
        if (Objects.equals(this.data, data)) {
            return this;
        }

        if (cachedBuffer == null) {
            return new ServerEvent(this.event, data, this.id, this.retry, null);
        }

        List<String> newLines = new ArrayList<>();
        boolean dataLinesReplaced = false;

        for (Buffer bufferLine : BufferUtils.split(this.cachedBuffer, new byte[] { '\n' })) {
            String lineStr = bufferLine.toString();
            if (lineStr.startsWith("data:")) {
                if (!dataLinesReplaced) {
                    if (data != null) {
                        if (data.isEmpty()) {
                            newLines.add("data: \n");
                        }

                        data.lines().map(line -> "data: " + line + "\n").forEach(newLines::add);
                    }
                    dataLinesReplaced = true;
                }
            } else {
                newLines.add(lineStr);
            }
        }

        // If data was not in the original buffer, add it before the final empty line
        if (!dataLinesReplaced && data != null) {
            int lastNonEmpty = newLines.size() - 1;
            while (lastNonEmpty >= 0 && newLines.get(lastNonEmpty).isBlank()) {
                lastNonEmpty--;
            }
            for (String dataLine : data.split("\n")) {
                newLines.add(lastNonEmpty + 1, "data: " + dataLine + "\n");
            }
        }

        return new ServerEvent(this.event, data, this.id, this.retry, Buffer.buffer(String.join("", newLines)));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServerEvent that = (ServerEvent) o;
        return (
            Objects.equals(event, that.event) &&
            Objects.equals(data, that.data) &&
            Objects.equals(id, that.id) &&
            Objects.equals(retry, that.retry)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(event, data, id, retry);
    }

    @Override
    public String toString() {
        return "ServerEvent[" + "event=" + event + ", data=" + data + ", id=" + id + ", retry=" + retry + ']';
    }
}
