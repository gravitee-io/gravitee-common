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
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * @author Jeoffrey HAEYAERT (jeoffrey.haeyaert at graviteesource.com)
 * @author GraviteeSource Team
 */
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class RxHelperTest {

    @Test
    void shouldStopWhenComplete() {
        Flowable.interval(10000, TimeUnit.MILLISECONDS).compose(RxHelper.mergeWithFirst(Flowable.empty())).test().assertComplete();
    }

    @Nested
    class DelayElement {

        @Test
        void should_delay_element_of_Flowable_by_10_seconds() {
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
                obs
                    .assertNotComplete()
                    .assertValueAt(0, value -> value.equals(exception))
                    .assertValueAt(1, value -> value.equals("attempt1"));

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
        void should_delay_element_of_Flowable_by_10_seconds_according_to_predicate() {
            try {
                final TestScheduler testScheduler = new TestScheduler();
                RxJavaPlugins.setComputationSchedulerHandler(s -> testScheduler);

                final RuntimeException exception = new RuntimeException();
                final TestSubscriber<Serializable> obs = Flowable
                    .fromArray(exception, "attempt1", "notDelayed", 12, "notDelayed")
                    .compose(RxHelper.delayElement(10, TimeUnit.SECONDS, o -> o == "notDelayed"))
                    .test()
                    .assertNotComplete();

                testScheduler.advanceTimeBy(10, TimeUnit.SECONDS);
                obs.assertNotComplete().assertValueAt(0, value -> value.equals(exception));

                testScheduler.advanceTimeBy(10, TimeUnit.SECONDS);
                obs
                    .assertNotComplete()
                    .assertValueAt(0, value -> value.equals(exception))
                    .assertValueAt(1, value -> value.equals("attempt1"))
                    .assertValueAt(2, value -> value.equals("notDelayed"));

                testScheduler.advanceTimeBy(10, TimeUnit.SECONDS);
                obs
                    .assertComplete()
                    .assertValueAt(0, value -> value.equals(exception))
                    .assertValueAt(1, value -> value.equals("attempt1"))
                    .assertValueAt(2, value -> value.equals("notDelayed"))
                    .assertValueAt(3, value -> value.equals(12))
                    .assertValueAt(4, value -> value.equals("notDelayed"));
            } finally {
                RxJavaPlugins.reset();
            }
        }
    }

    @Nested
    class RetryFlowable {

        @Test
        void should_retry_Flowable_and_success_when_attempted_less_than_the_limit() {
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
        void should_not_retry_Flowable_according_to_predicate_filter() {
            try {
                final TestScheduler testScheduler = new TestScheduler();
                RxJavaPlugins.setComputationSchedulerHandler(s -> testScheduler);

                AtomicInteger atomicCpt = new AtomicInteger(0);
                @NonNull
                TestSubscriber<Object> obs = Flowable
                    .generate(emitter -> {
                        int cpt = atomicCpt.incrementAndGet();
                        if (cpt <= 2) {
                            emitter.onError(new NonRetryableException());
                        } else {
                            emitter.onComplete();
                        }
                    })
                    .compose(RxHelper.retryFlowable(5, 10, TimeUnit.SECONDS, t -> !(t instanceof NonRetryableException)))
                    .test()
                    .assertNotComplete()
                    .assertNoValues();

                // an exception has been thrown, flow should be in error immediately
                obs.assertError(NonRetryableException.class).assertNoValues();
            } finally {
                RxJavaPlugins.reset();
            }
        }

        @Test
        void should_retry_Flowable_and_fail_when_attempted_more_than_the_limit_without_success() {
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
    }

    @Nested
    class RetryMaybe {

        @Test
        void should_retry_Maybe_and_success_when_attempted_less_than_the_limit() {
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
        void should_not_retry_Maybe_according_to_retry_predicate() {
            try {
                final TestScheduler testScheduler = new TestScheduler();
                RxJavaPlugins.setComputationSchedulerHandler(s -> testScheduler);

                AtomicInteger atomicCpt = new AtomicInteger(0);
                @NonNull
                TestObserver<Integer> obs = Maybe
                    .<Integer>create(emitter -> {
                        int cpt = atomicCpt.incrementAndGet();
                        if (cpt < 5) {
                            emitter.onError(new NonRetryableException());
                        } else {
                            emitter.onSuccess(cpt);
                        }
                    })
                    .compose(RxHelper.retryMaybe(5, 10, TimeUnit.SECONDS, t -> !(t instanceof NonRetryableException)))
                    .test();

                // an exception has been thrown, flow should be in error immediately
                obs.assertError(NonRetryableException.class).assertNoValues();
            } finally {
                RxJavaPlugins.reset();
            }
        }
    }

    @Nested
    class RetrySingle {

        @Test
        void should_retry_Single_and_success_when_attempted_less_than_the_limit() {
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
        void should_not_retry_Single_according_to_retry_predicate() {
            try {
                final TestScheduler testScheduler = new TestScheduler();
                RxJavaPlugins.setComputationSchedulerHandler(s -> testScheduler);

                AtomicInteger atomicCpt = new AtomicInteger(0);
                @NonNull
                TestObserver<Integer> obs = Single
                    .<Integer>create(emitter -> {
                        int cpt = atomicCpt.incrementAndGet();
                        if (cpt < 5) {
                            emitter.onError(new NonRetryableException());
                        } else {
                            emitter.onSuccess(cpt);
                        }
                    })
                    .compose(RxHelper.retrySingle(5, 10, TimeUnit.SECONDS, t -> !(t instanceof NonRetryableException)))
                    .test();

                // an exception has been thrown, flow should be in error immediately
                obs.assertError(NonRetryableException.class).assertNoValues();
            } finally {
                RxJavaPlugins.reset();
            }
        }
    }

    @Nested
    class RetryCompletable {

        @Test
        void should_retry_Completable_and_success_when_attempted_less_than_the_limit() {
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
        void should_retry_Completable_and_fail_when_attempted_more_than_the_limit_without_success() {
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

        @Test
        void should_not_retry_according_to_retry_predicate() {
            try {
                final TestScheduler testScheduler = new TestScheduler();
                RxJavaPlugins.setComputationSchedulerHandler(s -> testScheduler);

                AtomicInteger atomicCpt = new AtomicInteger(0);
                @NonNull
                TestObserver<Void> obs = Completable
                    .create(emitter -> {
                        int cpt = atomicCpt.incrementAndGet();
                        if (cpt <= 3) {
                            emitter.onError(new NonRetryableException());
                        } else {
                            emitter.onComplete();
                        }
                    })
                    .compose(RxHelper.retry(2, 10, TimeUnit.SECONDS, t -> !(t instanceof NonRetryableException)))
                    .test()
                    .assertNotComplete()
                    .assertNoValues();

                // an exception has been thrown, flow should be in error immediately
                obs.assertError(NonRetryableException.class).assertNoValues();
            } finally {
                RxJavaPlugins.reset();
            }
        }

        @Test
        void should_retry_first_exceptions_and_break_on_NonRetryableException_according_to_retry_predicate() {
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
                        } else if (cpt <= 5) {
                            emitter.onError(new NonRetryableException());
                        } else {
                            emitter.onComplete();
                        }
                    })
                    .compose(RxHelper.retry(5, 10, TimeUnit.SECONDS, t -> !(t instanceof NonRetryableException)))
                    .test()
                    .assertNotComplete()
                    .assertNoValues();

                // an exception has been thrown, so try to retry after ten seconds
                testScheduler.advanceTimeBy(10, TimeUnit.SECONDS);
                obs.assertNotComplete().assertNoValues();

                // two exception has been thrown, retry one more time
                testScheduler.advanceTimeBy(10, TimeUnit.SECONDS);
                obs.assertNotComplete().assertNoErrors().assertNoValues();

                // on the third exception, which is a NonRetryableException, flow should be in error
                testScheduler.advanceTimeBy(10, TimeUnit.SECONDS);
                obs.assertError(NonRetryableException.class);
            } finally {
                RxJavaPlugins.reset();
            }
        }
    }

    @Nested
    class RetryExponentialBackoff {

        @Test
        void should_retry_exponentially_and_finally_success_after_2_retries() {
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
                    .retryWhen(RxHelper.retryExponentialBackoff(10, TimeUnit.SECONDS))
                    .test()
                    .assertNotComplete()
                    .assertNoValues();

                // an exception has been thrown, so try to retry after ten seconds
                testScheduler.advanceTimeBy(10, TimeUnit.SECONDS);
                obs.assertNotComplete().assertNoValues();

                // Second retry should take longer than first one as factor equals 2
                testScheduler.advanceTimeBy(10, TimeUnit.SECONDS);
                obs.assertNotComplete().assertNoValues();

                testScheduler.advanceTimeBy(10, TimeUnit.SECONDS);
                obs.assertComplete().assertValueAt(0, value -> value.equals(3)).assertValueAt(1, value -> value.equals(4));
            } finally {
                RxJavaPlugins.reset();
            }
        }

        @Test
        void should_retry_and_finally_fail_after_3_retries() {
            try {
                final TestScheduler testScheduler = new TestScheduler();
                RxJavaPlugins.setComputationSchedulerHandler(s -> testScheduler);

                @NonNull
                TestSubscriber<Object> obs = Flowable
                    .generate(emitter -> emitter.onError(new RuntimeException("will never work")))
                    .retryWhen(RxHelper.retryExponentialBackoff(1, 1, TimeUnit.SECONDS, 1, 3, t -> true))
                    .test()
                    .assertNotComplete()
                    .assertNoValues();
                testScheduler.advanceTimeBy(4, TimeUnit.SECONDS);
                obs.assertError(err -> err.getMessage().equals("will never work"));
            } finally {
                RxJavaPlugins.reset();
            }
        }

        @Test
        void should_not_retry_exponentially_according_to_filter_predicate() {
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
                        } else if (cpt == 3) {
                            emitter.onError(new NonRetryableException());
                        } else if (cpt == 4) {
                            emitter.onNext(cpt);
                        } else {
                            emitter.onComplete();
                        }
                    })
                    .retryWhen(RxHelper.retryExponentialBackoff(10, 10, TimeUnit.SECONDS, 2, t -> !(t instanceof NonRetryableException)))
                    .test()
                    .assertNotComplete()
                    .assertNoValues();

                // a retryable exception has been thrown twice
                testScheduler.advanceTimeBy(20, TimeUnit.SECONDS);
                // an non-retryable exception has been thrown, flow should be in error immediately
                obs.assertError(NonRetryableException.class).assertNoValues();
            } finally {
                RxJavaPlugins.reset();
            }
        }
    }

    /**
     * Exception used in the predicates for retry method.
     */
    private static class NonRetryableException extends Throwable {}
}
