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
import io.reactivex.rxjava3.core.MaybeObserver;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;

public class ChunkToSseEvent {

    // ref: https://html.spec.whatwg.org/multipage/server-sent-events.html#parsing-an-event-stream
    private static final Pattern EVENT_SEPARATOR = Pattern.compile("(\\r\\n|\\r|\\n){2}");

    public static @NonNull Flowable<ServerEvent> chunkToEvent(@NonNull Flowable<Buffer> upstream) {
        var sb = new AtomicReference<>(Buffer.buffer());
        return upstream
            .concatMap(buffer -> {
                sb.get().appendBuffer(buffer);
                final String content = sb.get().toString(StandardCharsets.UTF_8);
                final var ready = new ArrayList<Buffer>();
                final var matcher = EVENT_SEPARATOR.matcher(content);
                int lastEnd = 0;
                while (matcher.find()) {
                    ready.add(Buffer.buffer(content.substring(lastEnd, matcher.start())));
                    lastEnd = matcher.end();
                }

                if (lastEnd > 0) {
                    sb.set(Buffer.buffer(content.substring(lastEnd)));
                }

                return Flowable.fromIterable(ready);
            })
            .concatWith((MaybeObserver<? super Buffer> observer) -> {
                if (sb.get().length() > 0) {
                    observer.onSuccess(sb.get());
                } else {
                    observer.onComplete();
                }
            })
            .map(ServerEvent::parse);
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
}
