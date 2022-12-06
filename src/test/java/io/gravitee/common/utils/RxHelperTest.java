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

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.observers.TestObserver;
import io.reactivex.rxjava3.plugins.RxJavaPlugins;
import io.reactivex.rxjava3.schedulers.TestScheduler;
import io.reactivex.rxjava3.subscribers.TestSubscriber;
import java.io.Serializable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * @author Jeoffrey HAEYAERT (jeoffrey.haeyaert at graviteesource.com)
 * @author GraviteeSource Team
 */
class RxHelperTest {

    @Test
    void shouldStopWhenComplete() {
        Flowable.interval(10000, TimeUnit.MILLISECONDS).compose(RxHelper.mergeWithFirst(Flowable.empty())).test().assertComplete();
    }

    @Test
    @DisplayName("Should delay element of Flowable by 10 seconds")
    void shouldDelayElement() {
        try {
            final TestScheduler testScheduler = new TestScheduler();
            RxJavaPlugins.setComputationSchedulerHandler(s -> testScheduler);

            final RuntimeException exception = new RuntimeException();
            final TestSubscriber<Serializable> obs = Flowable
                .fromArray(exception, "attempt1", 12)
                .compose(RxHelper.delayElement(10, TimeUnit.SECONDS))
                .test()
                .assertNotComplete();

            testScheduler.advanceTimeBy(10, TimeUnit.SECONDS);
            obs.assertNotComplete().assertValueAt(0, value -> value.equals(exception));

            testScheduler.advanceTimeBy(10, TimeUnit.SECONDS);
            obs.assertNotComplete().assertValueAt(0, value -> value.equals(exception)).assertValueAt(1, value -> value.equals("attempt1"));

            testScheduler.advanceTimeBy(10, TimeUnit.SECONDS);
            obs
                .assertComplete()
                .assertValueAt(0, value -> value.equals(exception))
                .assertValueAt(1, value -> value.equals("attempt1"))
                .assertValueAt(2, value -> value.equals(12));
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    @DisplayName("Should retry Flowable and success when attempted less than the limit")
    void shouldRetryFlowable() {
        try {
            final TestScheduler testScheduler = new TestScheduler();
            RxJavaPlugins.setComputationSchedulerHandler(s -> testScheduler);

            AtomicInteger atomicCpt = new AtomicInteger(0);
            @NonNull
            TestSubscriber<Object> obs = Flowable
                .generate(emitter -> {
                    int cpt = atomicCpt.incrementAndGet();
                    if (cpt <= 2) {
                        emitter.onError(new RuntimeException());
                    } else if (cpt <= 4) {
                        emitter.onNext(cpt);
                    } else {
                        emitter.onComplete();
                    }
                })
                .compose(RxHelper.retryFlowable(5, 10, TimeUnit.SECONDS))
                .test()
                //                          .awaitCount(4)
                .assertNotComplete()
                .assertNoValues();

            // an exception has been thrown, so try to retry after ten seconds
            testScheduler.advanceTimeBy(10, TimeUnit.SECONDS);
            obs.assertNotComplete().assertNoValues();

            // both exception has been thrown, values emit normally
            testScheduler.advanceTimeBy(10, TimeUnit.SECONDS);
            obs.assertComplete().assertValueAt(0, value -> value.equals(3)).assertValueAt(1, value -> value.equals(4));
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    @DisplayName("Should retry Maybe and success when attempted less than the limit")
    void shouldRetryMaybe() {
        try {
            final TestScheduler testScheduler = new TestScheduler();
            RxJavaPlugins.setComputationSchedulerHandler(s -> testScheduler);

            AtomicInteger atomicCpt = new AtomicInteger(0);
            @NonNull
            TestObserver<Integer> obs = Maybe
                .<Integer>create(emitter -> {
                    int cpt = atomicCpt.incrementAndGet();
                    if (cpt < 5) {
                        emitter.onError(new RuntimeException());
                    } else {
                        emitter.onSuccess(cpt);
                    }
                })
                .compose(RxHelper.retryMaybe(5, 10, TimeUnit.SECONDS))
                .test();

            for (int i = 0; i < 4; i++) {
                // 4 errors are expected before a success.
                obs.assertNotComplete().assertNoValues();
                testScheduler.advanceTimeBy(10, TimeUnit.SECONDS);
            }

            // Finally, last attempt should work.
            testScheduler.advanceTimeBy(10, TimeUnit.SECONDS);
            obs.assertComplete().assertValue(5);
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    @DisplayName("Should retry Single and success when attempted less than the limit")
    void shouldRetrySingle() {
        try {
            final TestScheduler testScheduler = new TestScheduler();
            RxJavaPlugins.setComputationSchedulerHandler(s -> testScheduler);

            AtomicInteger atomicCpt = new AtomicInteger(0);
            @NonNull
            TestObserver<Integer> obs = Single
                .<Integer>create(emitter -> {
                    int cpt = atomicCpt.incrementAndGet();
                    if (cpt < 5) {
                        emitter.onError(new RuntimeException());
                    } else {
                        emitter.onSuccess(cpt);
                    }
                })
                .compose(RxHelper.retrySingle(5, 10, TimeUnit.SECONDS))
                .test();

            for (int i = 0; i < 4; i++) {
                // 4 errors are expected before a success.
                obs.assertNotComplete().assertNoValues();
                testScheduler.advanceTimeBy(10, TimeUnit.SECONDS);
            }

            // Finally, last attempt should work.
            testScheduler.advanceTimeBy(10, TimeUnit.SECONDS);
            obs.assertComplete().assertValue(5);
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    @DisplayName("Should retry Flowable and fail when attempted more than the limit without success")
    void shouldBeInErrorWhenExceedingRetryFlowableAttempts() {
        try {
            final TestScheduler testScheduler = new TestScheduler();
            RxJavaPlugins.setComputationSchedulerHandler(s -> testScheduler);

            AtomicInteger atomicCpt = new AtomicInteger(0);
            @NonNull
            TestSubscriber<Object> obs = Flowable
                .generate(emitter -> {
                    int cpt = atomicCpt.incrementAndGet();
                    if (cpt <= 3) {
                        emitter.onError(new RuntimeException());
                    } else {
                        emitter.onComplete();
                    }
                })
                .compose(RxHelper.retryFlowable(2, 10, TimeUnit.SECONDS))
                .test()
                .assertNotComplete()
                .assertNoValues();

            // an exception has been thrown, so try to retry after ten seconds
            testScheduler.advanceTimeBy(10, TimeUnit.SECONDS);
            obs.assertNotComplete().assertNoValues();

            // two exception has been thrown, retry one more time
            testScheduler.advanceTimeBy(10, TimeUnit.SECONDS);
            obs.assertNotComplete().assertNoErrors().assertNoValues();

            // on the third exception, flow should be in error
            testScheduler.advanceTimeBy(10, TimeUnit.SECONDS);
            obs.assertError(RuntimeException.class);
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    @DisplayName("Should retry Completable and success when attempted less than the limit")
    void shouldRetryCompletable() {
        try {
            final TestScheduler testScheduler = new TestScheduler();
            RxJavaPlugins.setComputationSchedulerHandler(s -> testScheduler);

            AtomicInteger atomicCpt = new AtomicInteger(0);
            @NonNull
            TestObserver<Void> obs = Completable
                .create(emitter -> {
                    int cpt = atomicCpt.incrementAndGet();
                    if (cpt <= 2) {
                        emitter.onError(new RuntimeException());
                    } else if (cpt <= 4) {
                        emitter.onComplete();
                    }
                })
                .compose(RxHelper.retry(5, 10, TimeUnit.SECONDS))
                .test()
                .assertNotComplete()
                .assertNoValues();

            // an exception has been thrown, so try to retry after ten seconds
            testScheduler.advanceTimeBy(10, TimeUnit.SECONDS);
            obs.assertNotComplete();

            // both exception has been thrown, values emit normally
            testScheduler.advanceTimeBy(10, TimeUnit.SECONDS);
            obs.assertComplete();
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    @DisplayName("Should retry Completable and fail when attempted more than the limit without success")
    void shouldBeInErrorWhenExceedingRetryCompletableAttempts() {
        try {
            final TestScheduler testScheduler = new TestScheduler();
            RxJavaPlugins.setComputationSchedulerHandler(s -> testScheduler);

            AtomicInteger atomicCpt = new AtomicInteger(0);
            @NonNull
            TestObserver<Void> obs = Completable
                .create(emitter -> {
                    int cpt = atomicCpt.incrementAndGet();
                    if (cpt <= 3) {
                        emitter.onError(new RuntimeException());
                    } else {
                        emitter.onComplete();
                    }
                })
                .compose(RxHelper.retry(2, 10, TimeUnit.SECONDS))
                .test()
                .assertNotComplete()
                .assertNoValues();

            // an exception has been thrown, so try to retry after ten seconds
            testScheduler.advanceTimeBy(10, TimeUnit.SECONDS);
            obs.assertNotComplete().assertNoValues();

            // two exception has been thrown, retry one more time
            testScheduler.advanceTimeBy(10, TimeUnit.SECONDS);
            obs.assertNotComplete().assertNoErrors().assertNoValues();

            // on the third exception, flow should be in error
            testScheduler.advanceTimeBy(10, TimeUnit.SECONDS);
            obs.assertError(RuntimeException.class);
        } finally {
            RxJavaPlugins.reset();
        }
    }
}
