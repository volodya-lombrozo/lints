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
package org.eolang.lints.errors;

import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import org.cactoos.io.ResourceOf;
import org.cactoos.map.MapEntry;
import org.cactoos.map.MapOf;
import org.eolang.parser.EoSyntax;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link LtObjectIsNotUnique}.
 *
 * @since 0.0.30
 */
final class LtObjectIsNotUniqueTest {

    @Test
    void catchesDuplicates() throws Exception {
        ;
        MatcherAssert.assertThat(
            "Defects are empty, but they should not",
            new LtObjectIsNotUnique().defects(
                new MapOf<String, XML>(
                    new MapEntry<>(
                        "foo", LtObjectIsNotUniqueTest.xmir("foo")
                    ),
                    new MapEntry<>(
                        "bar-with-foo", LtObjectIsNotUniqueTest.xmir("bar-with-foo")
                    )
                )
            ),
            Matchers.hasSize(Matchers.greaterThan(0))
        );
    }

    @Test
    void allowsAllUnique() {
        MatcherAssert.assertThat(
            "Defects aren't empty, but they should",
            new LtObjectIsNotUnique().defects(
                new MapOf<String, XML>(
                    new MapEntry<>("foo", new XMLDocument("")),
                    new MapEntry<>("bar", new XMLDocument(""))
                )
            ),
            Matchers.emptyIterable()
        );
    }

    private static XML xmir(final String name) throws Exception {
        return new EoSyntax(
            name,
            new ResourceOf(
                String.format(
                    "org/eolang/lints/errors/object-is-not-unique/%s.eo",
                    name
                )
            )
        ).parsed();
    }
}
