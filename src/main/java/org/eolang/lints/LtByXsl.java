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
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import org.cactoos.Input;
import org.cactoos.io.ResourceOf;
import org.cactoos.text.IoCheckedText;
import org.cactoos.text.TextOf;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Lint by XSL.
 *
 * @since 0.0.1
 */
final class LtByXsl implements Lint<XML> {

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
    private final Input doc;

    /**
     * Ctor.
     * @param xsl Relative path of XSL
     * @throws IOException If fails
     */
    LtByXsl(final String xsl) throws IOException {
        this(
            new ResourceOf(
                String.format("org/eolang/lints/%s.xsl", xsl)
            ),
            new ResourceOf(
                String.format("org/eolang/motives/%s.md", xsl)
            )
        );
    }

    /**
     * Ctor.
     * @param xsl Relative path of XSL
     * @param motive Relative path of a motive document
     * @throws IOException If fails
     */
    @SuppressWarnings("PMD.ConstructorOnlyInitializesOrCallOtherConstructors")
    LtByXsl(final Input xsl, final Input motive) throws IOException {
        final XML xml = new XMLDocument(new IoCheckedText(new TextOf(xsl)).asString());
        this.rule = xml.xpath("/xsl:stylesheet/@id").get(0);
        this.sheet = new MeasuredXsl(
            this.rule,
            new XSLDocument(xml, this.rule).with(new ClasspathSources())
        );
        this.doc = motive;
    }

    @Override
    public String name() {
        return this.rule;
    }

    @Override
    public Collection<Defect> defects(final XML xmir) {
        final XML report = this.sheet.transform(xmir);
        final Collection<Defect> defects = new LinkedList<>();
        for (final XML defect : LtByXsl.findDefects(report)) {
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
                    LtByXsl.findName(xmir),
                    this.lineno(defect),
                    defect.xpath("text()").get(0)
                )
            );
        }
        return defects;
    }

    @Override
    public String motive() throws Exception {
        return new TextOf(this.doc).asString();
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

    /**
     * Find the name of the program.
     * @param program XML program
     * @return Name of the program.
     * @todo #199:30min Use {@link XML#xpath(String)} Method Instead.
     *  This method is using a custom implementation to find the name of the program.
     *  We should replace it with the {@link XML#xpath(String)} method to make the code cleaner.
     *  You can use `program.xpath("/program/@name").stream().findFirst().orElse("unknown")`
     *  to find the name.
     *  This issue is blocked by
     *  <a href="https://github.com/jcabi/jcabi-xml/issues/289">jcabi/jcabi-xml#289</a>.
     */
    private static String findName(final XML program) {
        return Optional.of(
                program.inner().getFirstChild().getAttributes().getNamedItem("name"))
            .map(Node::getTextContent)
            .orElse("unknown");
    }

    /**
     * Find defects in the report.
     * @param report XML report.
     * @return Collection of defects.
     * @todo #199:30min Use {@link XML#nodes(String)} Method Instead.
     *  This method is using a custom implementation to find defects in the
     *  report. We should replace it with the {@link XML#nodes(String)} method
     *  to make the code cleaner.
     *  You can use `report.nodes("/defects/defect")` to find the defects.
     *  This issue is blocked by
     *  <a href="https://github.com/jcabi/jcabi-xml/issues/288">jcabi/jcabi-xml#288</a>.
     */
    private static Collection<XML> findDefects(final XML report) {
        final NodeList nodes = report.inner().getChildNodes();
        final int length = nodes.getLength();
        final List<XML> defects = new ArrayList<>(0);
        for (int index = 0; index < length; ++index) {
            final Node element = nodes.item(index);
            if (element.getNodeName().equals("defects")) {
                final NodeList dnodes = element.getChildNodes();
                final int all = dnodes.getLength();
                for (int idx = 0; idx < all; ++idx) {
                    final Node defect = dnodes.item(idx);
                    if (defect.getNodeName().equals("defect")) {
                        defects.add(new XMLDocument(defect.cloneNode(true)));
                    }
                }
            }
        }
        return defects;
    }
}
