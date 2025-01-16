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
import org.cactoos.map.MapEntry;
import org.cactoos.map.MapOf;
import org.eolang.lints.ParsedEo;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link LtAtomIsNotUnique}.
 *
 * @since 0.0.31
 */
final class LtAtomIsNotUniqueTest {

    @Test
    void catchesAtomDuplicatesWithoutPackages() throws Exception {
        MatcherAssert.assertThat(
            "Defects are empty, but they should not",
            new LtAtomIsNotUnique().defects(
                new MapOf<String, XML>(
                    new MapEntry<>(
                        "foo",
                        new ParsedEo(
                            "org/eolang/lints/errors/atom-is-not-unique/foo.eo"
                        ).value()
                    ),
                    new MapEntry<>(
                        "bar",
                        new ParsedEo(
                            "org/eolang/lints/errors/atom-is-not-unique/bar.eo"
                        ).value()
                    )
                )
            ),
            Matchers.hasSize(2)
        );
    }

    @Test
    void catchesAtomDuplicatesWithinSamePackage() throws Exception {
        MatcherAssert.assertThat(
            "Defects are empty, but they should not",
            new LtAtomIsNotUnique().defects(
                new MapOf<String, XML>(
                    new MapEntry<>(
                        "foo-packaged",
                        new ParsedEo(
                            "org/eolang/lints/errors/atom-is-not-unique/foo-packaged.eo"
                        ).value()
                    ),
                    new MapEntry<>(
                        "bar-packaged",
                        new ParsedEo(
                            "org/eolang/lints/errors/atom-is-not-unique/bar-packaged.eo"
                        ).value()
                    )
                )
            ),
            Matchers.hasSize(2)
        );
    }

    @Test
    void catchesDuplicatesInSingleFile() throws Exception {
        MatcherAssert.assertThat(
            "Defects are empty, but they should not",
            new LtAtomIsNotUnique().defects(
                new MapOf<>(
                    new MapEntry<>(
                        "dup",
                        new ParsedEo(
                            "org/eolang/lints/errors/atom-is-not-unique/dup.eo"
                        ).value()
                    )
                )
            ),
            Matchers.hasSize(Matchers.greaterThan(0))
        );
    }

    @Test
    void allowsSameNameInDifferentPackages() throws Exception {
        MatcherAssert.assertThat(
            "Defects aren't empty, but they should",
            new LtAtomIsNotUnique().defects(
                new MapOf<String, XML>(
                    new MapEntry<>(
                        "x",
                        new ParsedEo(
                            "org/eolang/lints/errors/atom-is-not-unique/x.eo"
                        ).value()
                    ),
                    new MapEntry<>(
                        "x-packaged",
                        new ParsedEo(
                            "org/eolang/lints/errors/atom-is-not-unique/x-packaged.eo"
                        ).value()
                    )
                )
            ),
            Matchers.emptyIterable()
        );
    }

    @Test
    void allowsUniqueAtoms() throws Exception {
        MatcherAssert.assertThat(
            "Defects aren't empty, but they should",
            new LtAtomIsNotUnique().defects(
                new MapOf<String, XML>(
                    new MapEntry<>(
                        "a",
                        new ParsedEo(
                            "org/eolang/lints/errors/atom-is-not-unique/a.eo"
                        ).value()
                    ),
                    new MapEntry<>(
                        "b",
                        new ParsedEo(
                            "org/eolang/lints/errors/atom-is-not-unique/b.eo"
                        ).value()
                    )
                )
            ),
            Matchers.emptyIterable()
        );
    }

    @Test
    void allowsUniqueAtomsInSingleFile() throws Exception {
        MatcherAssert.assertThat(
            "Defects aren't empty, but they should",
            new LtAtomIsNotUnique().defects(
                new MapOf<>(
                    new MapEntry<>(
                        "spb",
                        new ParsedEo(
                            "org/eolang/lints/errors/atom-is-not-unique/spb.eo"
                        ).value()
                    )
                )
            ),
            Matchers.emptyIterable()
        );
    }

    @Test
    void allowsSameNameInComplexPackage() throws Exception {
        MatcherAssert.assertThat(
            "Defects aren't empty, but they should",
            new LtAtomIsNotUnique().defects(
                new MapOf<String, XML>(
                    new MapEntry<>(
                        "abc",
                        new ParsedEo(
                            "org/eolang/lints/errors/atom-is-not-unique/abc.eo"
                        ).value()
                    ),
                    new MapEntry<>(
                        "abc-packaged",
                        new ParsedEo(
                            "org/eolang/lints/errors/atom-is-not-unique/abc-packaged.eo"
                        ).value()
                    )
                )
            ),
            Matchers.emptyIterable()
        );
    }
}
