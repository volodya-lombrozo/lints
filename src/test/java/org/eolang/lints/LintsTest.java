package org.eolang.lints;

import com.jcabi.xml.XML;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.cactoos.Scalar;
import org.cactoos.experimental.Threads;
import org.cactoos.number.SumOf;
import org.cactoos.scalar.Sticky;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.RepeatedTest;

final class LintsTest {

    @RepeatedTest(50)
    void createsLintsUsingSameGeneratorManyTimesInParallel() {
        final Scalar<Iterable<Lint<XML>>> generator = new Sticky<>(
            () -> new ProgramLints().value()
        );
        final int threads = 100;
        final CountDownLatch latch = new CountDownLatch(threads);
        MatcherAssert.assertThat(
            new SumOf(
                new Threads<>(
                    threads,
                    Stream.generate(() -> LintsTest.task(() -> new Lints(generator), latch))
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
            new SumOf(
                new Threads<>(
                    threads,
                    Stream.generate(() -> LintsTest.task(Lints::new, latch))
                        .limit(threads)
                        .collect(Collectors.toList())
                )
            ).intValue(),
            Matchers.equalTo(threads)
        );
    }

    private static Scalar<Integer> task(Supplier supplier, final CountDownLatch latch) {
        return () -> {
            try {
                latch.countDown();
                latch.await();
                supplier.get().iterator();
                return 1;
            } catch (final IOException exception) {
                throw new RuntimeException(exception);
            } catch (final InterruptedException exception) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(exception);
            }
        };
    }

    private interface Supplier {
        Lints get() throws IOException;
    }

}