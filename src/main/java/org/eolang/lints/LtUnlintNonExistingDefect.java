/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import com.github.lombrozo.xnav.Xnav;
import com.jcabi.xml.XML;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.cactoos.io.ResourceOf;
import org.cactoos.list.ListOf;
import org.cactoos.text.IoCheckedText;
import org.cactoos.text.TextOf;

/**
 * Lint for checking `+unlint` meta to suppress non-existing defects in single program scope.
 *
 * @since 0.0.40
 */
final class LtUnlintNonExistingDefect implements Lint<XML> {

    /**
     * Lints.
     */
    private final Iterable<Lint<XML>> lints;

    /**
     * Lints to exclude.
     */
    private final Collection<String> excluded;

    /**
     * Ctor.
     * @param lnts Lints
     */
    LtUnlintNonExistingDefect(final Iterable<Lint<XML>> lnts) {
        this(lnts, new ListOf<>());
    }

    /**
     * Ctor.
     *
     * @param lnts Lints
     * @param exld Lint names to exclude
     */
    LtUnlintNonExistingDefect(final Iterable<Lint<XML>> lnts, final Collection<String> exld) {
        this.lints = lnts;
        this.excluded = exld;
    }

    @Override
    public String name() {
        return "unlint-non-existing-defect";
    }

    @Override
    public Collection<Defect> defects(final XML xmir) throws IOException {
        final Collection<Defect> defects = new LinkedList<>();
        final Map<String, List<Integer>> present = this.existingDefects(xmir);
        final Xnav xml = new Xnav(xmir.inner());
        final Set<String> unlints = xml.path("/program/metas/meta[head='unlint']/tail")
            .map(xnav -> xnav.text().get())
            .collect(Collectors.toSet());
        unlints.stream()
            .filter(
                unlint -> {
                    final boolean matches;
                    final String[] split = unlint.split(":");
                    final String name = split[0];
                    if (split.length > 1) {
                        final Integer line = Integer.parseInt(split[1]);
                        final List<Integer> lines = present.get(name);
                        matches = (present.get(name) == null || !lines.contains(line)) && !this.excluded.contains(name);
                    } else {
                        matches = present.get(name) == null && !this.excluded.contains(name);
                    }
                    return matches;
                }
            )
            .forEach(
                unlint ->
                    xml.path(
                        String.format(
                            "program/metas/meta[head='unlint' and tail='%s']/@line", unlint
                        )
                        )
                        .map(xnav -> xnav.text().get())
                        .collect(Collectors.toList())
                        .forEach(
                            line ->
                                defects.add(
                                    new Defect.Default(
                                        this.name(),
                                        Severity.WARNING,
                                        xml.element("program")
                                            .attribute("name")
                                            .text()
                                            .orElse("unknown"),
                                        Integer.parseInt(line),
                                        String.format(
                                            "Unlinting rule '%s' doesn't make sense, since there are no defects with it",
                                            unlint
                                        )
                                    )
                                )
                        )
            );
        return defects;
    }

    @Override
    public String motive() throws IOException {
        return new IoCheckedText(
            new TextOf(
                new ResourceOf(
                    String.format(
                        "org/eolang/motives/misc/%s.md", this.name()
                    )
                )
            )
        ).asString();
    }

    private Map<String, List<Integer>> existingDefects(final XML xmir) {
        final Map<String, List<Integer>> existing = new HashMap<>(0);
        this.lints.forEach(
            lint -> {
                try {
//                    existing.addAll(
//                        lint.defects(xmir).stream()
//                            .map(Defect::rule)
//                            .collect(Collectors.toList())
//                    );
                    lint.defects(xmir).forEach(defect ->
                        existing.computeIfAbsent(defect.rule(), key -> new ListOf<>())
                            .add(defect.line())
                    );

                } catch (final IOException exception) {
                    throw new IllegalStateException(exception);
                }
            }
        );
        return existing;
    }
}
