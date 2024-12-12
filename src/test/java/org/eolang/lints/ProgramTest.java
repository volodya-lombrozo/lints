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
import com.yegor256.Mktmp;
import com.yegor256.MktmpResolver;
import com.yegor256.xsline.Xsline;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import org.cactoos.io.InputOf;
import org.cactoos.io.ResourceOf;
import org.cactoos.text.TextOf;
import org.eolang.parser.EoSyntax;
import org.eolang.parser.TrParsing;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Test for {@link Program}.
 *
 * @since 0.0.1
 */
@ExtendWith(MktmpResolver.class)
final class ProgramTest {

    /**
     * Benchmarking results.
     */
    private static final File RESULTS = new File("target/lint-summary.txt");

    @Test
    void returnsEmptyListOfDefects() throws IOException {
        MatcherAssert.assertThat(
            "no defects found since the code is clean",
            new Program(
                new EoSyntax(
                    new InputOf(
                        String.join(
                            "\n",
                            "+home https://www.eolang.org",
                            "+package bar",
                            "+version 0.0.0",
                            "",
                            "# This is just a test object with no functionality.",
                            "[] > foo\n"
                        )
                    )
                ).parsed()
            ).defects(),
            Matchers.emptyIterable()
        );
    }

    @Test
    void simpleTest(@Mktmp final Path dir) throws IOException {
        final Path path = dir.resolve("foo.xmir");
        Files.write(
            path,
            new EoSyntax(
                new InputOf("# first.\n[] > foo\n# second.\n[] > foo\n")
            ).parsed().toString().getBytes(StandardCharsets.UTF_8)
        );
        MatcherAssert.assertThat(
            "the defect is found",
            new Program(path).defects().size(),
            Matchers.greaterThan(0)
        );
    }

    @Test
    void largerBrokenProgramTest() throws IOException {
        MatcherAssert.assertThat(
            "checking passes",
            new Program(
                new EoSyntax(
                    new InputOf(
                        String.join(
                            "\n",
                            "# This is the license",
                            "",
                            "+version 8.8.8-beta",
                            "+alias org.eolang.txt.sprintf",
                            "+alias org . eolang . txt . broken",
                            "+version 1.1-another maybe be wrong",
                            "+package Z.Y.Z",
                            "+home some-wrong-URL",
                            "+architect broken-email-here",
                            "",
                            "# комментарий здесь",
                            "[] > foo-bar",
                            "  (bar 42) > zzz",
                            "  44 > zzz",
                            "",
                            "[] > foo-bar",
                            "",
                            "[] > another",
                            "",
                            "42 > forty-two"
                        )
                    )
                ).parsed()
            ).defects(),
            Matchers.allOf(
                Matchers.iterableWithSize(Matchers.greaterThan(0)),
                Matchers.hasItem(
                    Matchers.hasToString(
                        Matchers.containsString(
                            "[alias-too-long ERROR]:5 The alias has too many parts"
                        )
                    )
                )
            )
        );
    }

    @Test
    void acceptsCanonicalCode() throws IOException {
        final XML xmir = new Xsline(new TrParsing()).pass(
            new EoSyntax(
                new ResourceOf(
                    "org/eolang/lints/canonical.eo"
                )
            ).parsed()
        );
        MatcherAssert.assertThat(
            String.format("no errors in canonical code in %s", xmir),
            new Program(xmir).defects(),
            Matchers.emptyIterable()
        );
    }

    @Test
    @Tag("benchmark")
    void lintsSmallProgram() throws Exception {
        final long start = System.currentTimeMillis();
        final Collection<Defect> defects = new Program(
            new EoSyntax(
                new ResourceOf(
                    "org/eolang/benchmark/small-program.eo"
                )
            ).parsed()
        ).defects();
        ProgramTest.writeResults(start);
        MatcherAssert.assertThat(
            "Defects are empty, but they should not be",
            defects,
            Matchers.hasSize(Matchers.greaterThan(0))
        );
    }

    private static void writeResults(final long start) throws Exception {
        Files.write(
            ProgramTest.RESULTS.toPath(),
            new TextOf(
                String.format(
                    "lintsSmallProgram took %s ms",
                    System.currentTimeMillis() - start
                )
            ).asString().getBytes()
        );
    }

}
