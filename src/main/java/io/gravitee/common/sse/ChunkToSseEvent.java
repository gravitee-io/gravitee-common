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
import io.reactivex.rxjava3.core.MaybeObserver;
import java.util.concurrent.atomic.AtomicReference;

public class ChunkToSseEvent {

    private static final byte[] EVENT_SEPARATOR = "\n\n".getBytes();

    public static @NonNull Flowable<ServerEvent> chunkToEvent(@NonNull Flowable<Buffer> upstream) {
        var sb = new AtomicReference<>(Buffer.buffer());
        return upstream
            .concatMap(buffer -> {
                sb.get().appendBuffer(buffer);
                var split = BufferUtils.split(sb.get(), EVENT_SEPARATOR);
                if (split.size() > 1) {
                    var last = split.get(split.size() - 1);
                    sb.set(last);
                }
                return Flowable.fromIterable(split).take(split.size() - 1);
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
}
