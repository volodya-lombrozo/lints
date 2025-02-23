/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import java.util.List;
import matchers.DefectMatcher;
import org.eolang.parser.EoSyntax;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link LtIncorrectUnlint}.
 *
 * @since 0.0.38
 */
final class LtIncorrectUnlintTest {
    @Test
    void catchesIncorrectUnlints() throws Exception {
        MatcherAssert.assertThat(
            "unlint must point to existing lint",
            new LtIncorrectUnlint(List.of("hello")).defects(
                new EoSyntax(
                    "+unlint abracadabra\n+unlint vingardium-leviosa"
                ).parsed()
            ),
            Matchers.allOf(
                Matchers.<Defect>iterableWithSize(2),
                Matchers.everyItem(new DefectMatcher())
            )
        );
    }

    @Test
    void allowsCorrectUnlints() throws Exception {
        MatcherAssert.assertThat(
            "Defects are not empty, but they shouldn't be",
            new LtIncorrectUnlint(List.of("ascii-only")).defects(
                new EoSyntax(
                    "+unlint ascii-only"
                ).parsed()
            ),
            Matchers.emptyIterable()
        );
    }
}
