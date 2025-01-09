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
package org.eolang.lints.critical;

import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import com.yegor256.Mktmp;
import com.yegor256.MktmpResolver;
import java.nio.file.Files;
import java.nio.file.Path;
import org.cactoos.map.MapEntry;
import org.cactoos.map.MapOf;
import org.eolang.lints.ParsedEo;
import org.eolang.lints.Programs;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Tests for {@link LtIncorrectAlias}.
 *
 * @since 0.0.30
 */
final class LtIncorrectAliasTest {

    /**
     * Prefix to EO snippet objects.
     */
    private static final String EO_PREFIX = "org/eolang/lints/critical/incorrect-alias/";

    @Test
    void catchesBrokenAlias() throws Exception {
        MatcherAssert.assertThat(
            "Defects are empty, but shouldn't be",
            new LtIncorrectAlias().defects(
                new MapOf<>(
                    new MapEntry<>(
                        "bar",
                        new ParsedEo(LtIncorrectAliasTest.EO_PREFIX, "bar").value()
                    )
                )
            ),
            Matchers.hasSize(Matchers.greaterThan(0))
        );
    }

    @Test
    void passesIfFileExists() throws Exception {
        MatcherAssert.assertThat(
            "Defects aren't empty, but should be",
            new LtIncorrectAlias().defects(
                new MapOf<String, XML>(
                    new MapEntry<>(
                        "bar",
                        new ParsedEo(LtIncorrectAliasTest.EO_PREFIX, "bar").value()
                    ),
                    new MapEntry<>("ttt/foo", new XMLDocument("<program/>"))
                )
            ),
            Matchers.hasSize(0)
        );
    }

    @ParameterizedTest
    @ValueSource(
        strings = {
            "no-aliases",
            "no-package"
        }
    )
    void ignoresProgram(final String name) throws Exception {
        MatcherAssert.assertThat(
            "Defects aren't empty, but should be",
            new LtIncorrectAlias().defects(
                new MapOf<>(
                    new MapEntry<>(
                        "foo",
                        new ParsedEo(LtIncorrectAliasTest.EO_PREFIX, name).value()
                    )
                )
            ),
            Matchers.hasSize(0)
        );
    }

    @Test
    @ExtendWith(MktmpResolver.class)
    void acceptsValidDirectory(@Mktmp final Path dir) throws Exception {
        Files.write(
            dir.resolve("bar.xmir"),
            new ParsedEo(LtIncorrectAliasTest.EO_PREFIX, "bar").value().toString()
                .getBytes()
        );
        Files.createDirectory(dir.resolve("ttt"));
        Files.write(dir.resolve("ttt/foo.xmir"), "<program/>".getBytes());
        Files.write(dir.resolve("bar-test.xmir"), "<program/>".getBytes());
        Files.write(dir.resolve("ttt/foo-test.xmir"), "<program/>".getBytes());
        MatcherAssert.assertThat(
            "Defects are not empty, but should be",
            new Programs(dir).defects(),
            Matchers.emptyIterable()
        );
    }
}
