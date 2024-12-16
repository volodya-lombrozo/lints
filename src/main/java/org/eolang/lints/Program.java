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
import io.github.secretx33.resourceresolver.PathMatchingResourcePatternResolver;
import io.github.secretx33.resourceresolver.Resource;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import org.cactoos.Scalar;
import org.cactoos.iterable.Joined;
import org.cactoos.scalar.IoChecked;
import org.cactoos.scalar.Sticky;
import org.eolang.lints.comments.AsciiOnly;
import org.eolang.lints.misc.UnitTestIsNotVerb;

/**
 * A single XMIR program to analyze.
 *
 * @see <a href="https://news.eolang.org/2022-11-25-xmir-guide.html">XMIR</a>
 * @since 0.1.0
 */
public final class Program {

    /**
     * Lints to use.
     */
    private static final Scalar<Iterable<Lint<XML>>> LINTS = new Sticky<>(
        () -> new ProgramLints().value()
    );

    /**
     * The XMIR program to analyze.
     */
    private final XML xmir;

    private final Scalar<Iterable<Lint<XML>>> lints;

    /**
     * Ctor.
     * @param file The absolute path of the XMIR file
     * @throws FileNotFoundException If file not found
     */
    public Program(final Path file) throws FileNotFoundException {
        this(new XMLDocument(file));
    }

    /**
     * Ctor.
     * @param xml The XMIR
     */
    public Program(final XML xml) {
        this(xml, Program.LINTS);
    }

    public Program(final XML xmir, final Scalar<Iterable<Lint<XML>>> lints) {
        this.xmir = xmir;
        this.lints = lints;
    }

    /**
     * Find defects possible defects in the XM§IR file.
     * @return All defects found
     */
    public Collection<Defect> defects() throws IOException {
        final Collection<Defect> messages = new LinkedList<>();
        try {
            for (final Lint<XML> lint : new IoChecked<>(this.lints).value()) {
                messages.addAll(lint.defects(this.xmir));
            }
        } catch (final NullPointerException exception) {
            final Resource[] resources = new PathMatchingResourcePatternResolver().getResources(
                "classpath*:org/eolang/lints/**/*.xsl"
            );
            final String context = String.join(
                "\n",
                String.format("Thread: %s", Thread.currentThread().getName()),
                String.format("XMIR: %s", this.xmir),
                String.format("Lints: %s", this.lints),
                String.format("Classpath size: %d", resources == null ? -1 : resources.length),
                String.format("Classpath: %s", Arrays.deepToString(resources)),
                String.format("new ProgramLints().value(): %s", new ProgramLints().value())
            );
            throw new IllegalArgumentException(
                String.format("Some strange state is detected in the lints.%n%s", context),
                exception
            );

        }
        return messages;
    }

}
