/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import com.github.lombrozo.xnav.Xnav;
import fixtures.EoProgram;
import java.io.IOException;
import org.cactoos.io.InputOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Test;

/**
 * Test for {@link LtSyntaxVersion}.
 * Most scenarios are covered by pure EO test packs under
 * {@code src/test/resources/org/eolang/lints/packs/single/syntax-version/},
 * exercised via {@link LtByXslTest#checksAllLints(String, String)}. The two
 * tests below stay here because they depend on the actual EO parser version
 * at build time, which a static YAML pack cannot express.
 *
 * @since 0.2.11
 */
final class LtSyntaxVersionTest {

    @Test
    void allowsSyntaxVersionEqualToParser() throws IOException {
        final String src = LtSyntaxVersionTest.program(
            String.format("+syntax %s", LtSyntaxVersionTest.actualVersion())
        );
        MatcherAssert.assertThat(
            "declaring the exact parser version must not cause defects",
            new LtSyntaxVersion().defects(new EoProgram(src, new InputOf(src)).parse()),
            Matchers.emptyIterable()
        );
    }

    @Test
    void explainsMotive() throws IOException {
        MatcherAssert.assertThat(
            "the motive doesn't mention the +syntax meta",
            new LtSyntaxVersion().motive().contains("+syntax"),
            new IsEqual<>(true)
        );
    }

    private static String actualVersion() {
        final String src = LtSyntaxVersionTest.program("+home https://example.com");
        return new Xnav(new EoProgram(src, new InputOf(src)).parse().inner())
            .path("/object").findFirst().get()
            .attribute("version").text().get();
    }

    private static String program(final String meta) {
        return String.join(System.lineSeparator(), meta, "", "[] > foo", "");
    }
}
