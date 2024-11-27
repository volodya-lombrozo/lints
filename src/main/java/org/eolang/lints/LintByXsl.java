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

import com.jcabi.xml.ClasspathSources;
import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import com.jcabi.xml.XSL;
import com.jcabi.xml.XSLDocument;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.cactoos.Input;
import org.cactoos.io.ResourceOf;
import org.cactoos.text.IoCheckedText;
import org.cactoos.text.TextOf;

/**
 * Lint by XSL.
 *
 * @since 0.0.1
 */
final class LintByXsl implements Lint {

    /**
     * The name of the rule.
     */
    private final String rule;

    /**
     * The stylesheet.
     */
    private final XSL sheet;

    /**
     * Motive document.
     */
    private final Input motivedoc;

    /**
     * Ctor.
     * @param xsl Relative path of XSL
     * @param motive Relative path of motive document
     * @throws IOException If fails
     */
    @SuppressWarnings("PMD.ConstructorOnlyInitializesOrCallOtherConstructors")
    LintByXsl(final Input xsl, final Input motive) throws IOException {
        final XML xml = new XMLDocument(
            new IoCheckedText(
                new TextOf(xsl)
            ).asString()
        );
        this.rule = xml.xpath("/xsl:stylesheet/@id").get(0);
        this.sheet = new XSLDocument(xml).with(new ClasspathSources());
        this.motivedoc = motive;
    }

    /**
     * Ctor.
     * @param xsl Relative path of XSL
     * @throws IOException If fails
     */
    LintByXsl(final String xsl) throws IOException {
        this(
            new ResourceOf(
                String.format("org/eolang/lints/%s.xsl", xsl)
            ),
            new ResourceOf(
                String.format("org/eolang/motives/%s.md", xsl)
            )
        );
    }

    @Override
    public Collection<Defect> defects(final XML xmir) {
        final XML report = this.sheet.transform(LintByXsl.sanitized(xmir));
        final Collection<Defect> defects = new LinkedList<>();
        for (final XML defect : report.nodes("/defects/defect")) {
            final List<String> severity = defect.xpath("@severity");
            if (severity.isEmpty()) {
                throw new IllegalStateException(
                    String.format("No severity reported by %s", this.rule)
                );
            }
            defects.add(
                new Defect.Default(
                    this.rule,
                    Severity.parsed(severity.get(0)),
                    this.lineno(defect),
                    defect.xpath("text()").get(0)
                )
            );
        }
        return defects;
    }

    @Override
    public String motive() throws Exception {
        return new TextOf(this.motivedoc).asString();
    }

    /**
     * Do a quick sanity check of the provided XML.
     * @param xmir The XMIR
     * @return The same XMIR
     */
    private static XML sanitized(final XML xmir) {
        final String[] paths = {
            "/program/metas/meta[not(@line) or @line='']",
        };
        for (final String xpath : paths) {
            if (!xmir.nodes(xpath).isEmpty()) {
                throw new IllegalArgumentException(
                    String.format(
                        "There is something wrong with the XMIR, found '%s':%n%s",
                        xpath, xmir
                    )
                );
            }
        }
        return xmir;
    }

    /**
     * Get line number of the defect.
     * @param defect XML defect
     * @return Line number
     */
    private int lineno(final XML defect) {
        final List<String> lines = defect.xpath("@line");
        if (lines.isEmpty()) {
            throw new IllegalStateException(
                String.format("No line number reported by %s", this.rule)
            );
        }
        final String line = lines.get(0);
        if (line.isEmpty()) {
            throw new IllegalStateException(
                String.format("Empty line number reported by %s", this.rule)
            );
        }
        final int lineno;
        try {
            lineno = Integer.parseInt(line);
        } catch (final NumberFormatException ex) {
            throw new IllegalStateException(
                String.format(
                    "Wrong line number reported by %s: '%s'",
                    this.rule, lines.get(0)
                ),
                ex
            );
        }
        return lineno;
    }

}
