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
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.cactoos.list.ListOf;

/**
 * Lint to check incorrect number of attributes passed to the object in scope.
 * @since 0.0.43
 */
final class LtIncorrectNumberOfAttrs implements Lint<Map<String, XML>> {

    @Override
    public Collection<Defect> defects(final Map<String, XML> pkg) throws IOException {
        final Collection<Defect> defects = new LinkedList<>();
        final Map<String, Integer> definitions = LtIncorrectNumberOfAttrs.objectDefinitions(pkg);
        // step 2: scan usages
        pkg.forEach(
            new BiConsumer<String, XML>() {
                @Override
                public void accept(final String program, final XML xmir) {
                    new Xnav(xmir.inner()).path("//o[@base and not(@base='∅')]").forEach(
                        new Consumer<Xnav>() {
                            @Override
                            public void accept(final Xnav xnav) {
                                final int provided = (int) xnav.elements(Filter.withName("o")).count();
                                final String object = xnav.attribute("base").text().orElse("unknown");
                                System.out.println(object);
                                final Integer expected = definitions.get(object);
                                System.out.println(provided);
                                System.out.println(expected);
                                if (expected != null && provided != expected) {
                                    defects.add(
                                        new Defect.Default(
                                            LtIncorrectNumberOfAttrs.this.name(),
                                            Severity.ERROR,
                                            program,
                                            Integer.parseInt(xnav.attribute("line").text().orElse("0")),
                                            String.format(
                                                "The object %s expects %d arguments, while %d provided",
                                                object, expected, provided
                                            )
                                        )
                                    );
                                }
                            }
                        }
                    );
                }
            }
        );
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
            (program, xmir) -> {
                final Xnav xml = new Xnav(xmir.inner());
                xml.element("program")
                    .element("objects")
                    .elements(Filter.withName("o")).forEach(
                        xob -> {
                            final List<Xnav> attrs = new ListOf<>();
                            xob.path("o[@base='∅']").forEach(attrs::add);
                            final String name = xob.attribute("name").text().orElse("unknown");
                            definitions.put(
                                LtIncorrectNumberOfAttrs.fqnPackaged(name, xml), attrs.size()
                            );
                        }
                    );
            }
        );
        return definitions;
    }

    private static String fqnPackaged(final String oname, final Xnav xml) {
        final String pack;
        final List<Xnav> packages = xml.element("program")
            .element("metas")
            .elements(Filter.withName("meta"))
            .filter(
                meta -> "package".equals(meta.element("head").text().get())
            )
            .collect(Collectors.toList());
        if (packages.isEmpty()) {
            pack = "org.eolang";
        } else {
            pack = packages.get(0).element("tail").text().get();
        }
        return String.format("Q.%s.%s", pack, oname);
    }
}
