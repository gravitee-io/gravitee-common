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
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.FlowableTransformer;
import io.reactivex.rxjava3.core.Maybe;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class ChunkToSseEvent {

    // ref: https://html.spec.whatwg.org/multipage/server-sent-events.html#parsing-an-event-stream
    private static final Pattern EVENT_SEPARATOR = Pattern.compile("(\\r\\n|\\r|\\n){2}");

    public static @NonNull Flowable<ServerEvent> chunkToEvent(@NonNull Flowable<Buffer> upstream) {
        return Flowable.defer(() -> {
            var framer = new EventFramer();
            return upstream.concatMapIterable(framer::push).concatWith(Maybe.defer(framer::flush)).map(ServerEvent::parse);
        });
    }

    public static @NonNull FlowableTransformer<Buffer, Buffer> onServerEvent(
        @NonNull final FlowableTransformer<ServerEvent, ServerEvent> onChunks
    ) {
        return upstream -> chunkToEvent(upstream).compose(onChunks).map(ServerEvent::toBuffer);
    }

    public static List<Buffer> split(Buffer buffer) {
        String[] split = EVENT_SEPARATOR.split(buffer.toString(StandardCharsets.UTF_8), 2);
        return split.length == 2 ? List.of(Buffer.buffer(split[0]), Buffer.buffer(split[1])) : List.of(buffer);
    }

    /**
     * Cuts a byte stream into events, an event ending with the first blank line. Only bytes not scanned yet are
     * inspected, so the cost stays linear in the stream size however many chunks it arrives in; and events are cut on
     * bytes, never decoded and re-encoded, so a multi-byte character split across two chunks is forwarded intact.
     */
    private static final class EventFramer {

        private static final byte[] EMPTY = new byte[0];

        private byte[] pending = EMPTY;
        private int length;
        private int scanFrom;

        List<Buffer> push(Buffer chunk) {
            append(chunk.getBytes());

            List<Buffer> events = new ArrayList<>(1);
            int start = 0;
            for (int end = nextEventEnd(); end > 0; end = nextEventEnd()) {
                events.add(Buffer.buffer(Arrays.copyOfRange(pending, start, end)));
                start = end;
                scanFrom = end;
            }
            discardFirst(start);
            return events;
        }

        Maybe<Buffer> flush() {
            return length > 0 ? Maybe.just(Buffer.buffer(Arrays.copyOf(pending, length))) : Maybe.empty();
        }

        private void append(byte[] bytes) {
            if (pending.length < length + bytes.length) {
                pending = Arrays.copyOf(pending, Math.max(length + bytes.length, pending.length * 2));
            }
            System.arraycopy(bytes, 0, pending, length, bytes.length);
            length += bytes.length;
        }

        private void discardFirst(int count) {
            if (count == length) {
                pending = EMPTY;
            } else if (count > 0) {
                System.arraycopy(pending, count, pending, 0, length - count);
            }
            length -= count;
            scanFrom -= count;
        }

        /**
         * Index just past the next blank line, or -1 when more bytes are needed; {@code scanFrom} is then left on the
         * first byte to scan again. A CR ending the bytes received so far is undecided: it may be the first half of a
         * CRLF.
         */
        private int nextEventEnd() {
            int i = scanFrom;
            while (i < length) {
                if (!isLineTerminator(pending[i])) {
                    i++;
                    continue;
                }
                int afterFirst = lineTerminatorEnd(i);
                if (afterFirst < 0 || afterFirst == length) {
                    scanFrom = i;
                    return -1;
                }
                if (isLineTerminator(pending[afterFirst])) {
                    int afterSecond = lineTerminatorEnd(afterFirst);
                    if (afterSecond < 0) {
                        scanFrom = i;
                    }
                    return afterSecond;
                }
                i = afterFirst;
            }
            scanFrom = length;
            return -1;
        }

        private int lineTerminatorEnd(int i) {
            if (pending[i] == '\n') {
                return i + 1;
            }
            if (i + 1 == length) {
                return -1;
            }
            return pending[i + 1] == '\n' ? i + 2 : i + 1;
        }

        private static boolean isLineTerminator(byte b) {
            return b == '\n' || b == '\r';
        }
    }
}
