/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import java.io.IOException;
import org.cactoos.list.ListOf;
import org.eolang.parser.EoSyntax;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link LtReservedName}.
 *
 * @since 0.0.43
 */
final class LtReservedNameTest {

    @Test
    void catchesReservedName() throws IOException {
        MatcherAssert.assertThat(
            "Defects are empty, but they should not",
            new LtReservedName(new ListOf<>("org.eolang.true"))
                .defects(
                    new EoSyntax(
                        "foo",
                        String.join(
                            "\n",
                            "# Foo.",
                            "[] > foo",
                            "  42 > true"
                        )
                    ).parsed()
                ),
            Matchers.hasSize(Matchers.greaterThan(0))
        );
    }
}
