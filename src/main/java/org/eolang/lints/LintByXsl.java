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
     * Ctor.
     * @param xsl Relative path of XSL
     * @throws IOException If fails
     */
    @SuppressWarnings("PMD.ConstructorOnlyInitializesOrCallOtherConstructors")
    LintByXsl(final Input xsl) throws IOException {
        final XML xml = new XMLDocument(
            new IoCheckedText(
                new TextOf(xsl)
            ).asString()
        );
        this.rule = xml.xpath("/xsl:stylesheet/@id").get(0);
        this.sheet = new XSLDocument(xml).with(new ClasspathSources());
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
            )
        );
    }

    @Override
    public Collection<Defect> defects(final XML xmir) {
        final XML report = this.sheet.transform(xmir);
        final Collection<Defect> defects = new LinkedList<>();
        for (final XML defect : report.nodes("/defects/defect")) {
            defects.add(
                new Defect.Default(
                    this.rule,
                    Severity.parsed(defect.xpath("@severity").get(0)),
                    Integer.parseInt(defect.xpath("@line").get(0)),
                    defect.xpath("text()").get(0)
                )
            );
        }
        return defects;
    }

}
