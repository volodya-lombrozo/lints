/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import com.jcabi.xml.XML;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;

/**
 * Lint to check incorrect number of attributes passed to the object in scope.
 * @since 0.0.43
 */
final class LtIncorrectNumberOfAttrs implements Lint<Map<String, XML>> {

    @Override
    public Collection<Defect> defects(final Map<String, XML> pkg) throws IOException {
        throw new UnsupportedOperationException("#defects()");
    }

    @Override
    public String name() {
        return "incorrect-number-of-attributes";
    }

    @Override
    public String motive() throws IOException {
        throw new UnsupportedOperationException("#motive()");
    }
}
