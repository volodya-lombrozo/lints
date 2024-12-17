/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2024 Objectionary.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.eolang.lints;

import com.jcabi.xml.XML;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.cactoos.Scalar;
import org.cactoos.experimental.Threads;
import org.cactoos.number.SumOf;
import org.cactoos.scalar.Sticky;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.RepeatedTest;

/**
 * Tests for {@link LintProgram}.
 * @since 0.23
 */
final class LintsTest {

    @RepeatedTest(50)
    void createsLintsUsingSameGeneratorManyTimesInParallel() {
        final Scalar<Iterable<Lint<XML>>> generator = new Sticky<>(XslLints::new);
        final int threads = 100;
        final CountDownLatch latch = new CountDownLatch(threads);
        MatcherAssert.assertThat(
            "We expect lints created using the same generator to be thread-safe",
            new SumOf(
                new Threads<>(
                    threads,
                    Stream.generate(() -> LintsTest.task(() -> new LintProgram(generator), latch))
                        .limit(threads)
                        .collect(Collectors.toList())
                )
            ).intValue(),
            Matchers.equalTo(threads)
        );
    }

    @RepeatedTest(50)
    void createsLintsUsingDefaultConstructorManyTimesInParallel() {
        final int threads = 100;
        final CountDownLatch latch = new CountDownLatch(threads);
        MatcherAssert.assertThat(
            "We expect lints created using default constructor to be thread-safe",
            new SumOf(
                new Threads<>(
                    threads,
                    Stream.generate(() -> LintsTest.task(LintProgram::new, latch))
                        .limit(threads)
                        .collect(Collectors.toList())
                )
            ).intValue(),
            Matchers.equalTo(threads)
        );
    }

    /**
     * Create a task.
     * @param supplier Linter supplier.
     * @param latch Latch to start all threads at the same time.
     * @return Task.
     */
    private static Scalar<Integer> task(final Supplier supplier, final CountDownLatch latch) {
        return () -> {
            try {
                latch.countDown();
                latch.await();
                supplier.get().iterator();
                return 1;
            } catch (final IOException exception) {
                throw new IllegalStateException(
                    "Some problem with io issue happened during the test",
                    exception
                );
            } catch (final InterruptedException exception) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException(
                    "Thread was interrupted in the middle of the test", exception
                );
            }
        };
    }

    /**
     * Supplier of lints.
     * @since 0.23
     */
    private interface Supplier {
        /**
         * Get lints.
         * @return Lints
         * @throws IOException If fails
         */
        LintProgram get() throws IOException;
    }
}
