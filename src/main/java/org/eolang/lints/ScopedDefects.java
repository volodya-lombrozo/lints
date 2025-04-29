/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import org.cactoos.collection.CollectionEnvelope;
import org.cactoos.iterable.Mapped;
import org.cactoos.list.ListOf;

/**
 * Defects, marked with their scopes.
 *
 * @since 0.0.48
 */
final class ScopedDefects extends CollectionEnvelope<Defect> {
    /**
     * Ctor.
     *
     * @param origin Origin defects
     * @param marker Marker
     */
    ScopedDefects(final Iterable<Defect> origin, final String marker) {
        super(
            new ListOf<>(
                new Mapped<>(
                    defect -> new DfContext(
                        new Defect.Default(
                            defect.rule(),
                            defect.severity(),
                            defect.program(),
                            defect.line(),
                            String.format("%s (%s)", defect.text(), marker),
                            defect.experimental()
                        ),
                        defect.context()
                    ),
                    origin
                )
            )
        );
    }
}
