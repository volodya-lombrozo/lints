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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * All {@code .xmir} files in a directory (with sub-directories).
 *
 * @since 0.0.1
 */
public final class ObjectsInDir implements Objects {

    /**
     * The location of them.
     */
    private final Path dir;

    /**
     * Ctor.
     * @param path The directory with XMIR files
     */
    public ObjectsInDir(final Path path) {
        this.dir = path;
    }

    @Override
    public Collection<String> rels() throws IOException {
        try (Stream<Path> files = Files.walk(this.dir)) {
            return files
                .filter(file -> !file.toFile().isDirectory())
                .map(file -> this.dir.relativize(file).toString())
                .map(rel -> rel.replaceAll("\\.xmir$", ""))
                .collect(Collectors.toList());
        }
    }

    @Override
    public XML take(final String rel) throws IOException {
        return new XMLDocument(
            this.dir.resolve(String.format("%s.xmir", rel))
        );
    }
}
