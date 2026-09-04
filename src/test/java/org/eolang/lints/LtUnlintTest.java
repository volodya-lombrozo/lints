/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import fixtures.EoProgram;
import java.io.IOException;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test for {@link LtUnlint}.
 * @since 0.0.1
 */
final class LtUnlintTest {

    @Test
    void lintsOneFile() throws IOException {
        MatcherAssert.assertThat(
            "failed to return one error",
            new LtUnlint(new LtAlways()).defects(
                new EoProgram("org/eolang/lints/foo-without-dot.eo").parse()
            ),
            Matchers.hasSize(1)
        );
    }

    @Test
    void suppressesTheDefect() throws IOException {
        MatcherAssert.assertThat(
            "failed to return empty list",
            new LtUnlint(new LtAlways()).defects(
                new EoProgram("org/eolang/lints/unlint-always.eo").parse()
            ),
            Matchers.emptyIterable()
        );
    }
}
