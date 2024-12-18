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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.cactoos.iterable.Sticky;
import org.eolang.lints.units.LtUnitTestMissing;

/**
 * A collection of XMIR programs to analyze.
 *
 * @see <a href="https://news.eolang.org/2022-11-25-xmir-guide.html">XMIR</a>
 * @since 0.1.0
 */
public final class Programs {

    /**
     * Lints to use.
     */
    private static final Iterable<Lint<Map<String, XML>>> LINTS = new Sticky<>(
        new LtUnitTestMissing()
    );

    /**
     * The package of XMIR files.
     */
    private final Map<String, XML> pkg;

    /**
     * Ctor.
     * @param dirs The directory
     * @throws IOException If fails
     */
    public Programs(final Path... dirs) throws IOException {
        this(Arrays.asList(dirs));
    }

    /**
     * Ctor.
     * @param dirs The directory
     * @throws IOException If fails
     */
    public Programs(final Collection<Path> dirs) throws IOException {
        this(Programs.discover(dirs));
    }

    /**
     * Ctor.
     * @param map The map with them
     */
    public Programs(final Map<String, XML> map) {
        this.pkg = Collections.unmodifiableMap(map);
    }

    /**
     * Find defects possible defects in the XMIR file.
     * @return All defects found
     */
    public Collection<Defect> defects() throws IOException {
        final Collection<Defect> messages = new LinkedList<>();
        for (final Lint<Map<String, XML>> lint : Programs.LINTS) {
            messages.addAll(lint.defects(this.pkg));
        }
        return messages;
    }

    /**
     * Discover all XMIR files in the directory.
     * @param dirs The directories to search for XMIR files in (recursively)
     * @return Map of XMIR files
     * @throws IOException If fails
     */
    private static Map<String, XML> discover(final Iterable<Path> dirs) throws IOException {
        final Map<String, XML> map = new HashMap<>(0);
        for (final Path dir : dirs) {
            map.putAll(Programs.discover(dir));
        }
        return map;
    }

    /**
     * Discover all XMIR files in the directory.
     * @param dir The directories to search for XMIR files in (recursively)
     * @return Map of XMIR files
     * @throws IOException If fails
     */
    private static Map<String, XML> discover(final Path dir) throws IOException {
        try (Stream<Path> walk = Files.walk(dir)) {
            return walk
                .filter(Files::isRegularFile)
                .collect(
                    Collectors.toMap(
                        path -> path.getFileName().toString().replaceAll("\\.xmir$", ""),
                        path -> {
                            try {
                                return new XMLDocument(path);
                            } catch (final FileNotFoundException ex) {
                                throw new IllegalArgumentException(ex);
                            }
                        }
                    )
                );
        }
    }

}
