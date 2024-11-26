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

import io.github.secretx33.resourceresolver.PathMatchingResourcePatternResolver;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import org.cactoos.io.InputOf;
import org.cactoos.io.ResourceOf;
import org.cactoos.iterable.IterableEnvelope;
import org.cactoos.iterable.Mapped;
import org.cactoos.list.ListOf;

/**
 * All lints defined by XSLs.
 *
 * @since 0.1.0
 */
public final class XslLints extends IterableEnvelope<Lint> {

    /**
     * XSL extension pattern.
     */
    private static final Pattern XSL_PATTERN = Pattern.compile(
        ".xsl", Pattern.LITERAL
    );

    /**
     * Ctor.
     */
    public XslLints() {
        super(XslLints.all());
    }

    /**
     * All lints.
     *
     * @return List of all lints
     */
    private static Iterable<Lint> all() {
        try {
            return new Mapped<>(
                res -> {
                    final List<String> dirs = new ListOf<>(
                        res.getFile().toString().split("/")
                    );
                    final int last = dirs.size() - 1;
                    return new LintByXsl(
                        new InputOf(res.getInputStream()),
                        new InputOf(
                            new ResourceOf(
                                String.format(
                                    "org/eolang/motives/%s/%s",
                                    dirs.get(last - 1),
                                    XSL_PATTERN.matcher(
                                        dirs.get(last)
                                    ).replaceAll(".md")
                                )
                            ).stream()
                        )
                    );
                },
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
