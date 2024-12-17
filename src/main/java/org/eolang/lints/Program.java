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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;

/**
 * A single XMIR program to analyze.
 *
 * @see <a href="https://news.eolang.org/2022-11-25-xmir-guide.html">XMIR</a>
 * @since 0.1.0
 */
public final class Program {

    /**
     * The XMIR program to analyze.
     */
    private final XML xmir;

    /**
     * The lints to use.
     */
    private final Lint<XML> lints;

    /**
     * Ctor.
     * @param file The absolute path of the XMIR file
     * @throws FileNotFoundException If file isn't found
     */
    public Program(final Path file) throws FileNotFoundException {
        this(new XMLDocument(file));
    }

    /**
     * Ctor.
     * @param xml The XMIR
     */
    public Program(final XML xml) {
        this(xml, new LintProgram());
    }

    /**
     * Ctor.
     * @param xmir The XMIR
     * @param lints The lints
     */
    Program(final XML xmir, final Lint<XML> lints) {
        this.xmir = xmir;
        this.lints = lints;
    }

    /**
     * Find defects possible defects in the XM§IR file.
     * @return All defects found
     */
    public Collection<Defect> defects() throws IOException {
        return this.lints.defects(this.xmir);
    }
}
