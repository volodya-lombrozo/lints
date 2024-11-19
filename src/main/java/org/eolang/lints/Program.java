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
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import org.cactoos.io.InputOf;
import org.cactoos.iterable.Mapped;

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
    private static final Iterable<Lint> LINTS = Program.all();

    /**
     * Absolute path of the XMIR file to analyze.
     */
    private final Path path;

    /**
     * Ctor.
     * @param file The absolute path of the XMIR file
     */
    public Program(final Path file) {
        this.path = file;
    }

    /**
     * Find defects possible defects in the XMIR file.
     * @return All defects found
     */
    public Collection<Defect> defects() throws IOException {
        final Collection<Defect> messages = new LinkedList<>();
        final XML xmir = new XMLDocument(this.path);
        for (final Lint lint : Program.LINTS) {
            messages.addAll(lint.defects(xmir));
        }
        return messages;
    }

    /**
     * All lints.
     * @return List of all lints
     */
    private static Iterable<Lint> all() {
        try {
            return new Mapped<>(
                res -> new LintByXsl(
                    new InputOf(res.getInputStream())
                ),
                Arrays.asList(
                    new PathMatchingResourcePatternResolver().getResources(
                        "classpath*:org/eolang/lints/**/*.xsl"
                    )
                )
            );
        } catch (final IOException ex) {
            throw new IllegalArgumentException(ex);
        }
    }

}
