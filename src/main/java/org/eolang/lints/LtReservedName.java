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
        // put into resources
        // parse them into .xmir
        // find each name -> remove `org.eolang.<package>.` part
        return new ListOf<>();
    }

    @Override
    public String motive() throws IOException {
        throw new UnsupportedOperationException("#motive()");
    }
}
