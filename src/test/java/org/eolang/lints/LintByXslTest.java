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
import com.jcabi.xml.XMLDocument;
import com.yegor256.MayBeSlow;
import com.yegor256.WeAreOnline;
import com.yegor256.xsline.Shift;
import com.yegor256.xsline.StClasspath;
import com.yegor256.xsline.TrDefault;
import com.yegor256.xsline.Train;
import com.yegor256.xsline.Xsline;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import org.cactoos.io.InputOf;
import org.eolang.jucs.ClasspathSource;
import org.eolang.parser.CheckPack;
import org.eolang.parser.EoSyntax;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.yaml.snakeyaml.Yaml;

/**
 * Test for {@link LintByXsl}.
 *
 * @since 0.0.1
 */
final class LintByXslTest {

    @Test
    void lintsOneFile() throws IOException {
        MatcherAssert.assertThat(
            "the objects is found",
            new LintByXsl("critical/duplicate-names").defects(
                new EoSyntax(
                    new InputOf("# first\n[] > foo\n# first\n[] > foo\n")
                ).parsed()
            ),
            Matchers.hasSize(Matchers.greaterThan(0))
        );
    }

    @ParameterizedTest
    @ExtendWith(MayBeSlow.class)
    @ExtendWith(WeAreOnline.class)
    @ClasspathSource(value = "org/eolang/lints/eo-packs/", glob = "**.yaml")
    void testsAllLintsByEo(final String pack) throws IOException {
        final CheckPack check = new CheckPack(pack);
        if (check.skip()) {
            Assumptions.abort(String.format("%s is not ready", pack));
        }
        MatcherAssert.assertThat(
            String.format("The check pack has failed: %n%s", pack),
            check.failures(),
            Matchers.empty()
        );
    }

    @ParameterizedTest
    @SuppressWarnings("unchecked")
    @ExtendWith(MayBeSlow.class)
    @ExtendWith(WeAreOnline.class)
    @ClasspathSource(value = "org/eolang/lints/xmir-packs/", glob = "**.yaml")
    void testsAllLintsByXmir(final String pack) {
        final Yaml yaml = new Yaml();
        final Map<String, Object> map = yaml.load(pack);
        final Iterable<String> xsls = (Iterable<String>) map.get("xsls");
        Train<Shift> train = new TrDefault<>();
        if (xsls != null) {
            for (final String xsl : xsls) {
                train = train.with(new StClasspath(xsl));
            }
        }
        final XML xmir = new XMLDocument(map.get("xmir").toString());
        final XML out = new Xsline(train).pass(xmir);
        final Collection<String> failures = new LinkedList<>();
        for (final String xpath : (Iterable<String>) map.get("tests")) {
            if (out.nodes(xpath).isEmpty()) {
                failures.add(xpath);
            }
        }
        MatcherAssert.assertThat(
            String.format("Input XML:%n%s%nBroken XML:%n%s", xmir, out),
            failures,
            Matchers.emptyIterable()
        );
    }

    @Test
    void returnsMotive() throws Exception {
        MatcherAssert.assertThat(
            "The motive was not found or empty",
            new LintByXsl("critical/duplicate-names").motive().isEmpty(),
            new IsEqual<>(false)
        );
    }

}
