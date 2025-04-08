/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package benchmarks;

import com.jcabi.xml.XML;
import fixtures.BytecodeClass;
import fixtures.ProgramSize;
import java.util.concurrent.TimeUnit;
import org.cactoos.scalar.Unchecked;
import org.eolang.lints.Program;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

/**
 * Benchmark for {@link Program}.
 *
 * @since 0.0.34
 * @todo #143:35min Add JMH benchmarks result reporting.
 *  Currently, there is no reporting of benchmark results. Let's add
 *  <a href="https://github.com/volodya-lombrozo/jmh-benchmark-action">this</a> GitHub Actions
 *  plugin and configure it to run on each PR.
 * @checkstyle DesignForExtensionCheck (10 lines)
 * @checkstyle NonStaticMethodCheck (100 lines)
 */
@Fork(1)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 1)
@Measurement(iterations = 3, time = 1, timeUnit = TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
public class ProgramBench {

    @Benchmark
    public final void scansXmir(final BenchmarkState state) {
        new Program(state.xmir).defects();
    }

    /**
     * Benchmark state.
     * @since 0.0.45
     */
    @State(Scope.Benchmark)
    public static class BenchmarkState {

        /**
         * Program size.
         */
        @Param({"S", "M", "L", "XL", "XXL"})
        private String size;

        /**
         * XMIR.
         */
        private XML xmir;

        /**
         * Initialize the state.
         */
        @Setup(Level.Trial)
        public void init() {
            this.xmir = new Unchecked<>(new BytecodeClass(ProgramSize.valueOf(this.size))).value();
        }
    }
}
