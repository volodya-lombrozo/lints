/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import com.github.lombrozo.xnav.Xnav;
import com.jcabi.xml.XML;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.cactoos.list.ListOf;
import org.eolang.parser.EoSyntax;

/**
 * Lint for reserved names.
 * @since 0.0.43
 */
final class LtReservedName implements Lint<XML> {

    /**
     * Reserved names.
     */
    private final List<String> reserved;

    /**
     * Ctor.
     */
    LtReservedName() {
        this(LtReservedName.reservedInHome());
    }

    /**
     * Ctor.
     *
     * @param names Reserved names
     */
    LtReservedName(final List<String> names) {
        this.reserved = names;
    }

    @Override
    public String name() {
        return "reserved-name";
    }

    @Override
    public Collection<Defect> defects(final XML xmir) throws IOException {
        final Collection<Defect> defects = new LinkedList<>();
        final Xnav program = new Xnav(xmir.inner());
        // move to the xpath @name = ...values
        program.path("//o[@name]")
            .forEach(
                object -> {
                    final String oname = object.attribute("name").text().get();
                    System.out.println(oname);
                    if (this.reserved.contains(oname)) {
                        defects.add(
                            new Defect.Default(
                                this.name(),
                                Severity.WARNING,
                                program.element("program").attribute("name")
                                    .text().orElse("unknown"),
                                Integer.parseInt(object.attribute("line").text().orElse("0")),
                                String.format(
                                    "Object name \"%s\" is already reserved by object in the org.eolang package",
                                    oname
                                )
                            )
                        );
                    }
                }
            );
        return defects;
    }

    private static List<String> reservedInHome() {
        // fetch objects from home with tag: https://github.com/objectionary/home/tree/master/objects/org/eolang
        final List<String> sources = new ListOf<>(
            String.join(
                "\n",
                "# Foo.",
                "[] > foo",
                "# Bar.",
                "[] > bar"
            )
        );
        final List<String> names = new ListOf<>();
        sources.stream().map(
            src -> {
                try {
                    return new EoSyntax("reserved", src).parsed();
                } catch (final IOException exception) {
                    throw new IllegalStateException("Failed to parse EO sources", exception);
                }
            }
        ).forEach(
            xmir -> new Xnav(xmir.inner()).path("/program/objects/o/@name")
                .map(oname -> oname.text().get())
                .forEach(names::add)
        );
        return names;
    }

    @Override
    public String motive() throws IOException {
        throw new UnsupportedOperationException("#motive()");
    }
}
