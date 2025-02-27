/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import java.io.IOException;
import org.cactoos.list.ListOf;
import org.cactoos.map.MapEntry;
import org.cactoos.map.MapOf;
import org.eolang.parser.EoSyntax;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link LtUnlintNonExistingDefectWpa}.
 *
 * @since 0.0.42
 */
final class LtUnlintNonExistingDefectWpaTest {

    @Test
    void allowsUnlintingExistingWpaDefects() throws IOException {
        MatcherAssert.assertThat(
            "Lint should not complain, since program has WPA defects",
            new LtUnlintNonExistingDefectWpa(
                new ListOf<>(new LtUnitTestMissing())
            ).defects(
                new MapOf<>(
                    "foo",
                    new EoSyntax(
                        String.join(
                            "\n",
                            "+unlint unit-test-missing",
                            "",
                            "# Foo",
                            "[] > foo"
                        )
                    ).parsed()
                )
            ),
            Matchers.emptyIterable()
        );
    }

    @Test
    void catchesUnlintOfNonExistingWpaDefects() throws IOException {
        MatcherAssert.assertThat(
            "Defects are not empty, but they should be, since +unlint unlints non-existing defect",
            new LtUnlintNonExistingDefectWpa(
                new ListOf<>(new LtUnitTestMissing())
            ).defects(
                new MapOf<String, XML>(
                    new MapEntry<>(
                        "bar",
                        new EoSyntax(
                            String.join(
                                "\n",
                                "+unlint unit-test-missing",
                                "",
                                "# Bar",
                                "[] > bar"
                            )
                        ).parsed()
                    ),
                    new MapEntry<>("bar-tests", new XMLDocument("<program/>"))
                )
            ),
            Matchers.hasSize(Matchers.greaterThan(0))
        );
    }

    @Test
    void allowsNoUnlints() {
        MatcherAssert.assertThat(
            "Defects are not empty, but they should",
            new LtUnlintNonExistingDefectWpa(
                new ListOf<>(new LtUnitTestMissing())
            ).defects(
                new MapOf<String, XML>(
                    new MapEntry<>("f", new XMLDocument("<program/>")),
                    new MapEntry<>("f-tests", new XMLDocument("<program/>"))
                )
            ),
            Matchers.emptyIterable()
        );
    }
}
