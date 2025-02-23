/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package fixtures;

import com.jcabi.xml.XML;
import java.io.File;
import org.cactoos.Scalar;
import org.cactoos.io.ResourceOf;
import org.eolang.parser.EoSyntax;

/**
 * Parsed EO syntax to XMIR.
 *
 * @since 0.0.31
 */
public final class ParsedEo implements Scalar<XML> {

    /**
     * Path to the EO program, in classpath (resources).
     */
    private final String path;

    /**
     * Ctor.
     * @param pth Path to EO program
     */
    public ParsedEo(final String pth) {
        this.path = pth;
    }

    @Override
    public XML value() throws Exception {
        return new EoSyntax(
            new File(this.path).getName(),
            new ResourceOf(this.path)
        ).parsed();
    }
}
