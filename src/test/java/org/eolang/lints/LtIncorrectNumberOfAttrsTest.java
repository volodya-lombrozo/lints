/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import com.jcabi.xml.XML;
import java.io.IOException;
import org.cactoos.map.MapEntry;
import org.cactoos.map.MapOf;
import org.eolang.parser.EoSyntax;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link LtIncorrectNumberOfAttrs}.
 * @since 0.0.43
 */
final class LtIncorrectNumberOfAttrsTest {

    @Test
    void catchesIncorrectNumberOfAttributes() throws IOException {
        MatcherAssert.assertThat(
            "Defects are empty, but they should not",
            new LtIncorrectNumberOfAttrs().defects(
                new MapOf<String, XML>(
                    new MapEntry<>(
                        "foo",
                        new EoSyntax(
                            String.join(
                                "\n",
                                "# Foo with one attribute.",
                                "[a] > foo"
                            )
                        ).parsed()
                    ),
                    new MapEntry<>(
                        "app",
                        new EoSyntax(
                            String.join(
                                "\n",
                                "# App uses foo with two attributes instead.",
                                "[a b] > app",
                                "  foo a b"
                            )
                        ).parsed()
                    )
                )
            ),
            Matchers.hasSize(Matchers.greaterThan(0))
        );
    }
}
