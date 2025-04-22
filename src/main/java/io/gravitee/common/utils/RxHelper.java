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
package io.gravitee.common.utils;

import io.reactivex.rxjava3.core.CompletableTransformer;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.FlowableTransformer;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.MaybeTransformer;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.SingleTransformer;
import io.reactivex.rxjava3.functions.Function;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.reactivestreams.Publisher;

/**
 * @author Jeoffrey HAEYAERT (jeoffrey.haeyaert at graviteesource.com)
 * @author GraviteeSource Team
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RxHelper {

    public static final Predicate<Throwable> TRUE_PREDICATE = t -> true;
    public static final Predicate<Object> FALSE_PREDICATE = o -> false;
    public static final String RETRY_PREDICATE_IS_NULL_ERROR = "retryPredicate is null";

    /**
     * Returns a {@link FlowableTransformer} that can be used in a composition.
     * It basically offers the same behavior as {@link io.reactivex.rxjava3.core.Flowable#mergeWith(Publisher)} but allows completing as soon as one of the sources completes (success, error or disposed).
     *
     * @param other the other source.
     * @param <R>   the type of the items emitted.
     * @return a {@link FlowableTransformer} that will be applied.
     */
    public static <R> FlowableTransformer<R, R> mergeWithFirst(Flowable<R> other) {
        return upstream -> other.materialize().mergeWith(upstream.materialize()).dematerialize(n -> n);
    }

    /**
     * Returns a {@link FlowableTransformer} that can be used in a composition.
     * It applies an interval for each element received in the {@link Flowable} upstream in opposition to {@link Flowable#delay(long, TimeUnit)} which will apply the delay only once for the {@link Flowable}
     *
     * @param delay    the delay to apply between each element of the {@link Flowable}
     * @param timeUnit the {@link TimeUnit}  of the delay
     * @return a {@link FlowableTransformer} that will be applied.
     */
    public static <R> FlowableTransformer<R, R> delayElement(int delay, TimeUnit timeUnit) {
        return delayElement(delay, timeUnit, FALSE_PREDICATE);
    }

    /**
     * Returns a {@link FlowableTransformer} that can be used in a composition.
     * It applies an interval for each element received in the {@link Flowable} upstream in opposition to {@link Flowable#delay(long, TimeUnit)} which will apply the delay only once for the {@link Flowable}
     *
     * @param delay              the delay to apply between each element of the {@link Flowable}
     * @param timeUnit           the {@link TimeUnit}  of the delay
     * @param skipDelayPredicate the predicate to test if delay should be skipped, else delay is applied
     * @return a {@link FlowableTransformer} that will be applied.
     */
    public static <R> FlowableTransformer<R, R> delayElement(int delay, TimeUnit timeUnit, Predicate<? super R> skipDelayPredicate) {
        return upstream ->
            upstream.concatMapSingle(e -> {
                if (skipDelayPredicate.test(e)) {
                    return Single.just(e);
                } else {
                    return Single.just(e).delay(delay, timeUnit);
                }
            });
    }

    /**
     * Returns a {@link FlowableTransformer} that can be used in a composition.
     * It retries the {@link Flowable} X times with delay between each attempt
     *
     * @param times         the attempts number
     * @param retryInterval the delay between each retry
     * @param timeUnit      the {@link TimeUnit} of the retryInterval
     * @return a {@link FlowableTransformer} that will be applied.
     */
    public static <R> FlowableTransformer<R, R> retryFlowable(int times, int retryInterval, TimeUnit timeUnit) {
        return retryFlowable(times, retryInterval, timeUnit, TRUE_PREDICATE);
    }

    /**
     * Returns a {@link FlowableTransformer} that can be used in a composition.
     * It retries the {@link Flowable} X times with delay between each attempt
     *
     * @param times          the attempts number
     * @param retryInterval  the delay between each retry
     * @param timeUnit       the {@link TimeUnit} of the retryInterval
     * @param retryPredicate the predicate to test if instance of {@link Throwable} has to be retried, else, emits directly the error
     * @return a {@link FlowableTransformer} that will be applied.
     */
    public static <R> FlowableTransformer<R, R> retryFlowable(
        int times,
        int retryInterval,
        TimeUnit timeUnit,
        Predicate<Throwable> retryPredicate
    ) {
        Objects.requireNonNull(retryPredicate, RETRY_PREDICATE_IS_NULL_ERROR);
        return upstream -> upstream.retryWhen(retryLinear(times, retryInterval, timeUnit, retryPredicate));
    }

    /**
     * Same as {@link #retryFlowable(int, int, TimeUnit)} but with a {@link Maybe} instead.
     *
     * @param times         the attempts number
     * @param retryInterval the delay between each retry
     * @param timeUnit      the {@link TimeUnit} of the retryInterval
     * @param <R>           the value type.
     * @return a {@link MaybeTransformer} that will be applied.
     */
    public static <R> MaybeTransformer<R, R> retryMaybe(int times, int retryInterval, TimeUnit timeUnit) {
        return retryMaybe(times, retryInterval, timeUnit, TRUE_PREDICATE);
    }

    /**
     * Same as {@link #retryFlowable(int, int, TimeUnit)} but with a {@link Maybe} instead.
     *
     * @param times          the attempts number
     * @param retryInterval  the delay between each retry
     * @param timeUnit       the {@link TimeUnit} of the retryInterval
     * @param <R>            the value type.
     * @param retryPredicate the predicate to test if instance of {@link Throwable} has to be retried, else, emits directly the error
     * @return a {@link MaybeTransformer} that will be applied.
     */
    public static <R> MaybeTransformer<R, R> retryMaybe(
        int times,
        int retryInterval,
        TimeUnit timeUnit,
        Predicate<Throwable> retryPredicate
    ) {
        Objects.requireNonNull(retryPredicate, RETRY_PREDICATE_IS_NULL_ERROR);
        return upstream -> upstream.retryWhen(retryLinear(times, retryInterval, timeUnit, retryPredicate));
    }

    /**
     * Same as {@link #retryMaybe(int, int, TimeUnit)} but with a {@link Single} instead.
     *
     * @param times         the attempts number
     * @param retryInterval the delay between each retry
     * @param timeUnit      the {@link TimeUnit} of the retryInterval
     * @param <R>           the value type.
     * @return a {@link MaybeTransformer} that will be applied.
     */
    public static <R> SingleTransformer<R, R> retrySingle(int times, int retryInterval, TimeUnit timeUnit) {
        return retrySingle(times, retryInterval, timeUnit, TRUE_PREDICATE);
    }

    /**
     * Same as {@link #retryMaybe(int, int, TimeUnit)} but with a {@link Single} instead.
     *
     * @param times          the attempts number
     * @param retryInterval  the delay between each retry
     * @param timeUnit       the {@link TimeUnit} of the retryInterval
     * @param <R>            the value type.
     * @param retryPredicate the predicate to test if instance of {@link Throwable} has to be retried, else, emits directly the error
     * @return a {@link MaybeTransformer} that will be applied.
     */
    public static <R> SingleTransformer<R, R> retrySingle(
        int times,
        int retryInterval,
        TimeUnit timeUnit,
        Predicate<Throwable> retryPredicate
    ) {
        Objects.requireNonNull(retryPredicate, RETRY_PREDICATE_IS_NULL_ERROR);
        return upstream -> upstream.retryWhen(retryLinear(times, retryInterval, timeUnit, retryPredicate));
    }

    /**
     * Returns a {@link CompletableTransformer} that can be used in a composition.
     * It retries the {@link Flowable} X times with delay between each attempt
     *
     * @param times         the attempts number
     * @param retryInterval the delay between each retry
     * @param timeUnit      the {@link TimeUnit} of the retryInterval
     * @return a {@link CompletableTransformer} that will be applied.
     * @deprecated See {@link #retryCompletable(int, int, TimeUnit)}
     */
    @Deprecated(forRemoval = true)
    public static CompletableTransformer retry(int times, int retryInterval, TimeUnit timeUnit) {
        return retryCompletable(times, retryInterval, timeUnit, TRUE_PREDICATE);
    }

    /**
     * Returns a {@link CompletableTransformer} that can be used in a composition.
     * It retries the {@link Flowable} X times with delay between each attempt
     *
     * @param times          the attempts number
     * @param retryInterval  the delay between each retry
     * @param timeUnit       the {@link TimeUnit} of the retryInterval
     * @param retryPredicate the predicate to test if instance of {@link Throwable} has to be retried, else, emits directly the error
     * @return a {@link CompletableTransformer} that will be applied.
     * @deprecated See {@link #retryCompletable(int, int, TimeUnit, Predicate)}
     */
    @Deprecated(forRemoval = true)
    public static CompletableTransformer retry(int times, int retryInterval, TimeUnit timeUnit, Predicate<Throwable> retryPredicate) {
        return retryCompletable(times, retryInterval, timeUnit, retryPredicate);
    }

    /**
     * Returns a {@link CompletableTransformer} that can be used in a composition.
     * It retries the {@link Flowable} X times with delay between each attempt
     *
     * @param times         the attempts number
     * @param retryInterval the delay between each retry
     * @param timeUnit      the {@link TimeUnit} of the retryInterval
     * @return a {@link CompletableTransformer} that will be applied.
     */
    public static CompletableTransformer retryCompletable(int times, int retryInterval, TimeUnit timeUnit) {
        // By default, we want to retry every throwable
        return retryCompletable(times, retryInterval, timeUnit, TRUE_PREDICATE);
    }

    /**
     * Returns a {@link CompletableTransformer} that can be used in a composition.
     * It retries the {@link Flowable} X times with delay between each attempt
     *
     * @param times          the attempts number
     * @param retryInterval  the delay between each retry
     * @param timeUnit       the {@link TimeUnit} of the retryInterval
     * @param retryPredicate the predicate to test if instance of {@link Throwable} has to be retried, else, emits directly the error
     * @return a {@link CompletableTransformer} that will be applied.
     */
    public static CompletableTransformer retryCompletable(
        int times,
        int retryInterval,
        TimeUnit timeUnit,
        Predicate<Throwable> retryPredicate
    ) {
        Objects.requireNonNull(retryPredicate, RETRY_PREDICATE_IS_NULL_ERROR);
        return upstream -> upstream.retryWhen(retryLinear(times, retryInterval, timeUnit, retryPredicate));
    }

    /**
     * It retries the {@link Flowable} X times with delay between each attempt
     *
     * @param times          the attempts number
     * @param retryInterval  the delay between each retry
     * @param timeUnit       the {@link TimeUnit} of the retryInterval
     * @param retryPredicate the predicate to test if instance of {@link Throwable} has to be retried, else, emits directly the error
     */
    public static Function<? super Flowable<Throwable>, ? extends Publisher<?>> retryLinear(
        int times,
        int retryInterval,
        TimeUnit timeUnit,
        Predicate<Throwable> retryPredicate
    ) {
        Objects.requireNonNull(retryPredicate, RETRY_PREDICATE_IS_NULL_ERROR);
        // Negate the retryPredicate to skip the Throwable in operators
        final Predicate<Throwable> skipThrowable = retryPredicate.negate();
        return throwables ->
            throwables
                // No need to delay element if we do not apply retry
                .compose(delayElement(retryInterval, timeUnit, skipThrowable))
                // No need to check limit if we do not apply retry, error should be emitted directly
                .compose(takeThenThrow(times, skipThrowable));
    }

    /**
     * It will progressively wait longer intervals between consecutive retries.
     * The initial delay is used as the beginning, then the factor of 2 is used to build the second delay without any max limitation.
     *
     * @param initialDelay the initial delay to wait
     * @param timeUnit     the {@link TimeUnit} of the initialDelay
     * @return a {@link Function} that will be applied.
     */
    public static Function<? super Flowable<Throwable>, ? extends Publisher<?>> retryExponentialBackoff(
        final long initialDelay,
        final TimeUnit timeUnit
    ) {
        return retryExponentialBackoff(initialDelay, -1, timeUnit);
    }

    /**
     * It will progressively wait longer intervals between consecutive retries.
     * The initial delay is used as the beginning, then the factor of 2 is used to build the second delay until it reaches the maxDelay.
     *
     * @param initialDelay the initial delay to wait
     * @param maxDelay     the max delay
     * @param timeUnit     the {@link TimeUnit} of the initialDelay and maxDelay
     * @return a {@link Function} that will be applied.
     */
    public static Function<? super Flowable<Throwable>, ? extends Publisher<?>> retryExponentialBackoff(
        final long initialDelay,
        final long maxDelay,
        final TimeUnit timeUnit
    ) {
        return retryExponentialBackoff(initialDelay, maxDelay, timeUnit, 2);
    }

    /**
     * It will progressively wait longer intervals between consecutive retries.
     * The initial delay is used as the beginning, then the factor is used to build the second delay until it reaches the maxDelay.
     *
     * @param initialDelay the initial delay to wait
     * @param maxDelay     the max delay
     * @param timeUnit     the {@link TimeUnit} of the initialDelay and maxDelay
     * @param factor       factor used to compute next delay
     * @return a {@link Function} that will be applied.
     */
    public static Function<? super Flowable<Throwable>, ? extends Publisher<?>> retryExponentialBackoff(
        final long initialDelay,
        final long maxDelay,
        final TimeUnit timeUnit,
        final double factor
    ) {
        return retryExponentialBackoff(initialDelay, maxDelay, timeUnit, factor, TRUE_PREDICATE);
    }

    /**
     * It will progressively wait longer intervals between consecutive retries.
     * The initial delay is used as the beginning, then the factor is used to build the second delay until it reaches the maxDelay.
     *
     * @param initialDelay   the initial delay to wait
     * @param maxDelay       the max delay
     * @param timeUnit       the {@link TimeUnit} of the initialDelay and maxDelay
     * @param factor         factor used to compute next delay
     * @param retryPredicate the predicate to test if instance of {@link Throwable} has to be retried, else, emits directly the error
     * @return a {@link Function} that will be applied.
     */
    public static Function<? super Flowable<Throwable>, ? extends Publisher<?>> retryExponentialBackoff(
        final long initialDelay,
        final long maxDelay,
        final TimeUnit timeUnit,
        final double factor,
        Predicate<Throwable> retryPredicate
    ) {
        return retryExponentialBackoff(initialDelay, maxDelay, timeUnit, factor, -1, retryPredicate);
    }

    /**
     * It will progressively wait longer intervals between consecutive retries.
     * The initial delay is used as the beginning, then the factor is used to build the second delay until it reaches the maxDelay and maxAttempt
     *
     * @param initialDelay   the initial delay to wait
     * @param maxDelay       the max delay
     * @param timeUnit       the {@link TimeUnit} of the initialDelay and maxDelay
     * @param factor         factor used to compute next delay
     * @param maxAttempt     maxAttempt max number of attempt
     * @param retryPredicate the predicate to test if instance of {@link Throwable} has to be retried, else, emits directly the error
     * @return a {@link Function} that will be applied.
     */
    public static Function<? super Flowable<Throwable>, ? extends Publisher<?>> retryExponentialBackoff(
        final long initialDelay,
        final long maxDelay,
        final TimeUnit timeUnit,
        final double factor,
        final int maxAttempt,
        Predicate<Throwable> retryPredicate
    ) {
        Objects.requireNonNull(retryPredicate, RETRY_PREDICATE_IS_NULL_ERROR);
        record Attempt(Throwable throwable, int occurrence) {}

        return attempts ->
            attempts
                .flatMapSingle(throwable -> {
                    if (retryPredicate.test(throwable)) {
                        return Single.just(throwable);
                    } else {
                        return Single.error(throwable);
                    }
                })
                .zipWith(Flowable.range(1, Integer.MAX_VALUE), Attempt::new)
                .map(attempt -> {
                    if (maxAttempt > 0 && attempt.occurrence() > maxAttempt) {
                        throw attempt.throwable();
                    }
                    long delayMs = Math.round(Math.pow(factor, (double) attempt.occurrence() - 1) * timeUnit.toMillis(initialDelay));
                    if (maxDelay != -1) {
                        long maxDelayMs = timeUnit.toMillis(maxDelay);
                        delayMs = Math.min(maxDelayMs, delayMs);
                    }
                    return delayMs;
                })
                .flatMap(delayMs -> Flowable.timer(delayMs, TimeUnit.MILLISECONDS));
    }

    /**
     * Returns a {@link FlowableTransformer} that can be used in a composition.
     * It ignores the N first throwable then return the N+1 in error.
     *
     * @param limit                  the number of throwables to ignore
     * @param throwDirectlyPredicate the predicate to check if limit should be computed or not. If it returns true, then {@link Maybe#error(Throwable)} is immediately emitted
     * @return a {@link FlowableTransformer} that will be applied.
     */
    private static <R extends Throwable> FlowableTransformer<R, R> takeThenThrow(final int limit, Predicate<R> throwDirectlyPredicate) {
        // By default, check the limit before returning a {@link Maybe#error}
        final AtomicInteger tries = new AtomicInteger(0);
        return upstream ->
            upstream.flatMapMaybe(throwable -> {
                if (tries.incrementAndGet() > limit || throwDirectlyPredicate.test(throwable)) {
                    return Maybe.error(throwable);
                } else {
                    return Maybe.just(throwable);
                }
            });
    }
}
