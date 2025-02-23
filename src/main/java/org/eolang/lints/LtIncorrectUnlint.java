/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import com.jcabi.xml.XML;
import java.io.IOException;
import java.util.Collection;
import org.cactoos.io.ResourceOf;
import org.cactoos.iterable.Filtered;
import org.cactoos.iterable.Mapped;
import org.cactoos.list.ListOf;
import org.cactoos.set.SetOf;
import org.cactoos.text.TextOf;
import org.cactoos.text.UncheckedText;

/**
 * Lint that all unlint metas point to existing lint.
 *
 * @since 0.0.38
 */
final class LtIncorrectUnlint implements Lint<XML> {

    /**
     * All possible names.
     */
    private final Collection<String> names;

    /**
     * Ctor.
     * @param lints All possible lint names
     */
    LtIncorrectUnlint(final Iterable<String> lints) {
        this.names = new SetOf<>(lints);
    }

    @Override
    public String name() {
        return "incorrect-unlint";
    }

    @Override
    public Collection<Defect> defects(final XML entity) throws IOException {
        return new ListOf<>(
            new Mapped<Defect>(
                xml -> new Defect.Default(
                    this.name(),
                    Severity.ERROR,
                    entity.xpath("/program/@name").stream().findFirst().orElse("unknown"),
                    Integer.parseInt(xml.xpath("@line").get(0)),
                    "Uselessly \"unlint\", because a lint with that name does not exist"
                ),
                new Filtered<>(
                    xml -> !this.names.contains(xml.xpath("tail/text()").get(0)),
                    entity.nodes("/program/metas/meta[head='unlint']")
                )
            )
        );
    }

    @Override
    public String motive() throws IOException {
        return new UncheckedText(
            new TextOf(
                new ResourceOf(
                    "org/eolang/motives/errors/lt-incorrect-unlint.md"
                )
            )
        ).asString();
    }
}
