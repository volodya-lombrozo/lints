/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import com.github.lombrozo.xnav.Filter;
import com.github.lombrozo.xnav.Xnav;
import com.jcabi.xml.XML;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.cactoos.list.ListOf;

/**
 * Lint to check incorrect number of attributes passed to the object in scope.
 * @since 0.0.43
 */
final class LtIncorrectNumberOfAttrs implements Lint<Map<String, XML>> {

    @Override
    public Collection<Defect> defects(final Map<String, XML> pkg) throws IOException {
        final Collection<Defect> defects = new LinkedList<>();
        // step 1: build map of definitions
        final Map<String, Integer> definitions = LtIncorrectNumberOfAttrs.objectDefinitions(pkg);
        System.out.println(definitions);
        return defects;
    }

    @Override
    public String name() {
        return "incorrect-number-of-attributes";
    }

    @Override
    public String motive() throws IOException {
        throw new UnsupportedOperationException("#motive()");
    }

    /**
     * Build object definitions.
     * @param pkg Package to scan
     * @return Map of object name and attributes count
     */
    private static Map<String, Integer> objectDefinitions(final Map<String, XML> pkg) {
        final Map<String, Integer> definitions = new HashMap<>(0);
        pkg.forEach(
            (program, xmir) -> new Xnav(xmir.inner()).element("program")
                .element("objects")
                .elements(Filter.withName("o")).forEach(
                    xob -> {
                        final List<Xnav> attrs = new ListOf<>();
                        xob.path("o[@base='∅']").forEach(attrs::add);
                        final String name = xob.attribute("name").text().orElse("unknown");
                        definitions.put(name, attrs.size());
                    }
                )
        );
        return definitions;
    }
}
