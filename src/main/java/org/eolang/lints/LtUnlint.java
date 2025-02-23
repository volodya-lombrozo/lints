/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import com.github.lombrozo.xnav.Xnav;
import com.jcabi.xml.XML;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Lint that ignores linting if {@code +unlint} meta is present.
 *
 * @since 0.0.1
 */
final class LtUnlint implements Lint<XML> {

    /**
     * The original lint.
     */
    private final Lint<XML> origin;

    /**
     * Ctor.
     * @param lint The lint to decorate
     */
    LtUnlint(final Lint<XML> lint) {
        this.origin = lint;
    }

    @Override
    public String name() {
        return this.origin.name();
    }

    @Override
    public Collection<Defect> defects(final XML xmir) throws IOException {
        final boolean suppress = new Xnav(xmir.inner()).path(
            String.format(
                "/program/metas/meta[head='unlint' and tail='%s']",
                this.origin.name()
            )
        ).findAny().isPresent();
        final Collection<Defect> defects = new ArrayList<>(0);
        if (!suppress) {
            defects.addAll(this.origin.defects(xmir));
        }
        return defects;
    }

    @Override
    public String motive() throws IOException {
        return this.origin.motive();
    }

}
