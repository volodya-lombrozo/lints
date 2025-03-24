/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import com.jcabi.xml.XML;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.cactoos.list.ListOf;
import org.eolang.parser.EoSyntax;
import org.yaml.snakeyaml.Yaml;

/**
 * Test story for WPA.
 *
 * @since 0.0.43
 */
final class WpaStory {

    /**
     * Yaml.
     */
    private final String yaml;

    /**
     * WPA lints.
     */
    final Map<String, Lint<Map<String, XML>>> wpa;

    /**
     * Ctor.
     *
     * @param yml  Yaml to load
     * @param lnts Lints
     */
    WpaStory(final String yml, final Map<String, Lint<Map<String, XML>>> lnts) {
        this.yaml = yml;
        this.wpa = lnts;
    }

    /**
     * Execute it.
     * @return Map of failures
     * @throws IOException if I/O fails
     */
    public Map<String, String> execute() throws IOException {
        final Map<String, Object> loaded = new Yaml().load(String.class.cast(yaml));
        final List<String> specials = new ListOf<>("lints", "defects");
        final Map<String, XML> programs = new HashMap<>(0);
        loaded.forEach(
            (key, val) -> {
                if (!specials.contains(key)) {
                    try {
                        programs.put(key, new EoSyntax(key, (String) val).parsed());
                    } catch (final IOException exception) {
                        throw new IllegalStateException(
                            "Failed to parse EO syntax", exception
                        );
                    }
                }
            }
        );
        Object lints = loaded.get("lints");
        if (lints == null) {
            lints = Arrays.asList();
        }
        Object expected = loaded.get("defects");
        if (expected == null) {
            expected = Arrays.asList();
        }
        final Collection<Defect> found = new LinkedList<>();
        final Map<String, String> failures = new HashMap<>(0);
        for (final String lint : (Iterable<String>) lints) {
            final Lint<Map<String, XML>> wpl = this.wpa.get(lint);
            found.addAll(wpl.defects(programs));
        }
        for (final String expression : (Iterable<String>) expected) {
            if (expression.startsWith("count()=")) {
                final int ecount = Integer.parseInt(expression.substring("count()=".length()));
                if (found.size() != ecount) {
                    failures.put(
                        expression,
                        String.format(
                            "Expected defects with size %d, actual size: %d", ecount, found.size()
                        )
                    );
                }
            }
            if (expression.startsWith("text()=")) {
                final List<String> texts = found.stream()
                    .map(Defect::text)
                    .collect(Collectors.toList());
                final String etext = expression.substring("text()=".length());
                if (!texts.contains(etext)) {
                    failures.put(
                        expression,
                        String.format(
                            "None of defects don't contain \"%s\" text inside, while it expected",
                            etext
                        )
                    );
                }
            }
        }
        return failures;
    }
}
