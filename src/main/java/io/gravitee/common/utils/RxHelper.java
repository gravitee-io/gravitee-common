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
package io.gravitee.common.utils;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.FlowableTransformer;
import org.reactivestreams.Publisher;

/**
 * @author Jeoffrey HAEYAERT (jeoffrey.haeyaert at graviteesource.com)
 * @author GraviteeSource Team
 */
public class RxHelper {

    /**
     * Returns a {@link FlowableTransformer} that can be used in a composition.
     * It basically offers the same behavior as {@link io.reactivex.rxjava3.core.Flowable#mergeWith(Publisher)} but allows completing as soon as one of the sources completes (success, error or disposed).
     *
     * @param other the other source.
     * @param <R> the type of the items emitted.
     *
     * @return a {@link FlowableTransformer} that will be applied.
     */
    public static <R> FlowableTransformer<R, R> mergeWithFirst(Flowable<R> other) {
        return upstream -> other.materialize().mergeWith(upstream.materialize()).dematerialize(n -> n);
    }
}
