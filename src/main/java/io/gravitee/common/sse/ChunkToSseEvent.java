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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Line endings (CRLF, LF or CR) and blank lines as defined by
 * https://html.spec.whatwg.org/multipage/server-sent-events.html#parsing-an-event-stream
 */
public class ChunkToSseEvent {

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

    /**
     * Splits a buffer at its first blank line into what precedes it and what follows it, the blank line itself being
     * dropped. Returns the buffer alone when it holds no blank line.
     */
    public static List<Buffer> split(Buffer buffer) {
        byte[] bytes = buffer.getBytes();
        int i = 0;
        while (i < bytes.length) {
            if (!isLineTerminator(bytes[i])) {
                i++;
                continue;
            }
            int afterFirst = endOfLineTerminator(bytes, i);
            if (afterFirst < bytes.length && isLineTerminator(bytes[afterFirst])) {
                int afterSecond = endOfLineTerminator(bytes, afterFirst);
                return List.of(
                    Buffer.buffer(Arrays.copyOfRange(bytes, 0, i)),
                    Buffer.buffer(Arrays.copyOfRange(bytes, afterSecond, bytes.length))
                );
            }
            i = afterFirst;
        }
        return List.of(buffer);
    }

    /** End of the line terminator at {@code i} of a complete buffer, where a CR ending the buffer stands alone. */
    private static int endOfLineTerminator(byte[] bytes, int i) {
        return bytes[i] == '\r' && i + 1 < bytes.length && bytes[i + 1] == '\n' ? i + 2 : i + 1;
    }

    private static boolean isLineTerminator(byte b) {
        return b == '\n' || b == '\r';
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
    }
}
