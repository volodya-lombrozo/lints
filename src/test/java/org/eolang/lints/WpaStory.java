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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
     * @return Map of failures with defects context
     * @throws IOException if I/O fails
     */
    public Map<List<String>, Collection<Defect>> execute() throws IOException {
        final Map<String, Object> loaded = new Yaml().load(String.class.cast(yaml));
        final Map<String, XML> programs = new HashMap<>(0);
        loaded.forEach(
            (key, val) -> {
                if (key.endsWith(".eo")) {
                    try {
                        final String name = key.substring(0, key.length() - 3);
                        programs.put(name, new EoSyntax(name, (String) val).parsed());
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
        Object expected = loaded.get("asserts");
        if (expected == null) {
            expected = Arrays.asList();
        }
        final Collection<Defect> found = new LinkedList<>();
        final List<String> failures = new LinkedList<>();
        for (final String lint : (Iterable<String>) lints) {
            final Lint<Map<String, XML>> wpl = this.wpa.get(lint);
            found.addAll(wpl.defects(programs));
        }
        for (final String expression : (Iterable<String>) expected) {
            if (expression.startsWith("count()=")) {
                if (found.size() != Integer.parseInt(expression.substring("count()=".length()))) {
                    failures.add(expression);
                }
            }
            final Pattern pattern = Pattern.compile("^count\\((\\w+)\\)=(\\d+)$");
            Matcher matcher = pattern.matcher(expression);
            if (matcher.matches()) {
                final List<Defect> severed = found.stream().filter(
                    defect -> defect.severity() == Severity.parsed(matcher.group(1))
                ).collect(Collectors.toList());
                if (severed.size() != Integer.parseInt(matcher.group(2))) {
                    failures.add(expression);
                }
            }
            if (expression.startsWith("hasText()=")) {
                final List<String> texts = found.stream()
                    .map(Defect::text)
                    .collect(Collectors.toList());
                final String value = expression.substring("hasText()=".length());
                final String sanitized;
                if (value.startsWith("'") && value.endsWith("'")) {
                    sanitized = value.substring(1, value.length() - 1);
                } else {
                    sanitized = value;
                }
                if (!texts.contains(sanitized)) {
                    failures.add(expression);
                }
            }
            if (expression.startsWith("containsText()=")) {
                final List<String> texts = found.stream()
                    .map(Defect::text)
                    .collect(Collectors.toList());
                for (final String txt : texts) {
                    final String value = expression.substring("containsText()=".length());
                    final String sanitized;
                    if (value.startsWith("'") && value.endsWith("'")) {
                        sanitized = value.substring(1, value.length() - 1);
                    } else {
                        sanitized = value;
                    }
                    if (txt.contains(sanitized)) {
                        break;
                    }
                    failures.add(expression);
                }
            }
            if (expression.startsWith("lines()=")) {
                new ListOf<>(
                    expression.substring("lines()=".length())
                        .replace("[", "").replace("]", "").split(",")
                ).forEach(
                    ref -> {
                        final String[] parts = ref.trim().split(":");
                        final boolean matches = found.stream().anyMatch(
                            defect -> defect.program().equals(parts[0])
                                && defect.line() == Integer.parseInt(parts[1])
                        );
                        if (!matches) {
                            failures.add(expression);
                        }
                    }
                );
            }
        }
        final Map<List<String>, Collection<Defect>> outcome = new HashMap<>(0);
        if (!failures.isEmpty()) {
            outcome.put(failures, found);
        }
        return outcome;
    }
}
