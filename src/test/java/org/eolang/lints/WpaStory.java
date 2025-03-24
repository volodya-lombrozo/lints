/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import com.jcabi.xml.XML;
import java.io.IOException;
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
     * Severity count pattern.
     */
    private static final Pattern SEVERED_COUNT = Pattern.compile("^count\\((\\w+)\\)=(\\d+)$");

    /**
     * Yaml.
     */
    private final String yaml;

    /**
     * WPA lints.
     */
    private final Map<String, Lint<Map<String, XML>>> wpa;

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
    @SuppressWarnings("unchecked")
    public Map<List<String>, Collection<Defect>> execute() throws IOException {
        final Map<String, Object> loaded = new Yaml().load(this.yaml);
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
            lints = List.of();
        }
        Object expected = loaded.get("asserts");
        if (expected == null) {
            expected = List.of();
        }
        final Collection<Defect> found = new LinkedList<>();
        final List<String> failures = new LinkedList<>();
        for (final String lint : (Iterable<String>) lints) {
            final Lint<Map<String, XML>> wpl = this.wpa.get(lint);
            found.addAll(wpl.defects(programs));
        }
        for (final String expression : (Iterable<String>) expected) {
            new Assertion.AssertsOnTrue(
                expression.startsWith("count()="),
                new WpaStory.Assertion.Count(expression, failures, found)
            ).exec();
            final Matcher matcher = WpaStory.SEVERED_COUNT.matcher(expression);
            new Assertion.AssertsOnTrue(
                matcher.matches(),
                new WpaStory.Assertion.SeveredCount(expression, failures, found, matcher)
            );
            new Assertion.AssertsOnTrue(
                expression.startsWith("hasText()="),
                new WpaStory.Assertion.HasText(expression, failures, found)
            ).exec();
            new Assertion.AssertsOnTrue(
                expression.startsWith("containsText()="),
                new WpaStory.Assertion.ContainsText(expression, failures, found)
            ).exec();
            new Assertion.AssertsOnTrue(
                expression.startsWith("lines()="),
                new WpaStory.Assertion.HasLines(expression, failures, found)
            ).exec();
        }
        final Map<List<String>, Collection<Defect>> outcome = new HashMap<>(0);
        if (!failures.isEmpty()) {
            outcome.put(failures, found);
        }
        return outcome;
    }

    /**
     * Assertion.
     * @since 0.0.43
     */
    interface Assertion {

        /**
         * Execute it.
         */
        void exec();

        /**
         * Starts with.
         * @since 0.0.43
         */
        final class AssertsOnTrue implements WpaStory.Assertion {

            /**
             * Condition.
             */
            private final boolean condition;

            /**
             * Origin.
             */
            private final WpaStory.Assertion origin;

            /**
             * Ctor.
             * @param bool Condition
             * @param assertion Assertion
             */
            AssertsOnTrue(final boolean bool, final WpaStory.Assertion assertion) {
                this.condition = bool;
                this.origin = assertion;
            }

            @Override
            public void exec() {
                if (this.condition) {
                    this.origin.exec();
                }
            }
        }

        /**
         * Count assertion.
         * @since 0.0.43
         */
        final class Count implements WpaStory.Assertion {

            /**
             * Expression.
             */
            private final String expression;

            /**
             * Failures.
             */
            private final List<String> failures;

            /**
             * Defects.
             */
            private final Collection<Defect> defects;

            /**
             * Ctor.
             * @param expr Expression
             * @param flrs Failures
             * @param found Defects
             */
            Count(final String expr, final List<String> flrs, final Collection<Defect> found) {
                this.expression = expr;
                this.failures = flrs;
                this.defects = found;
            }

            @Override
            public void exec() {
                if (
                    this.defects.size()
                        != Integer.parseInt(this.expression.substring("count()=".length()))
                ) {
                    this.failures.add(this.expression);
                }
            }
        }

        /**
         * Count with severity.
         * @since 0.0.43
         */
        final class SeveredCount implements Assertion {

            /**
             * Expression.
             */
            private final String expression;

            /**
             * Failures.
             */
            private final List<String> failures;

            /**
             * Defects.
             */
            private final Collection<Defect> defects;

            /**
             * Regexp.
             */
            private final Matcher regexp;

            /**
             * Ctor.
             * @param expr Expression
             * @param flrs Failures
             * @param found Defects
             * @param matcher Matcher
             * @checkstyle ParameterNumberCheck (5 lines)
             */
            SeveredCount(
                final String expr, final List<String> flrs, final Collection<Defect> found,
                final Matcher matcher
            ) {
                this.expression = expr;
                this.failures = flrs;
                this.defects = found;
                this.regexp = matcher;
            }

            @Override
            public void exec() {
                final List<Defect> severed = this.defects.stream().filter(
                    defect -> defect.severity() == Severity.parsed(this.regexp.group(1))
                ).collect(Collectors.toList());
                if (severed.size() != Integer.parseInt(this.regexp.group(2))) {
                    this.failures.add(this.expression);
                }
            }
        }

        /**
         * Has text assertion.
         * @since 0.0.43
         */
        final class HasText implements WpaStory.Assertion {

            /**
             * Expression.
             */
            private final String expression;

            /**
             * Failures.
             */
            private final List<String> failures;

            /**
             * Defects.
             */
            private final Collection<Defect> defects;

            /**
             * Ctor.
             * @param expr Expression
             * @param flrs Failures
             * @param found Defects
             */
            HasText(final String expr, final List<String> flrs, final Collection<Defect> found) {
                this.expression = expr;
                this.failures = flrs;
                this.defects = found;
            }

            @Override
            public void exec() {
                final List<String> texts = this.defects.stream()
                    .map(Defect::text)
                    .collect(Collectors.toList());
                final String value = this.expression.substring("hasText()=".length());
                final String sanitized;
                if (value.startsWith("'") && value.endsWith("'")) {
                    sanitized = value.substring(1, value.length() - 1);
                } else {
                    sanitized = value;
                }
                if (!texts.contains(sanitized)) {
                    this.failures.add(this.expression);
                }
            }
        }

        /**
         * Contains text assertion.
         * @since 0.0.43
         */
        final class ContainsText implements WpaStory.Assertion {

            /**
             * Expression.
             */
            private final String expression;

            /**
             * Failures.
             */
            private final List<String> failures;

            /**
             * Defects.
             */
            private final Collection<Defect> defects;

            /**
             * Ctor.
             * @param expr Expression
             * @param flrs Failures
             * @param found Defects
             */
            ContainsText(
                final String expr, final List<String> flrs, final Collection<Defect> found
            ) {
                this.expression = expr;
                this.failures = flrs;
                this.defects = found;
            }

            @Override
            public void exec() {
                final List<String> texts = this.defects.stream()
                    .map(Defect::text)
                    .collect(Collectors.toList());
                for (final String txt : texts) {
                    final String value = this.expression.substring("containsText()=".length());
                    final String sanitized;
                    if (value.startsWith("'") && value.endsWith("'")) {
                        sanitized = value.substring(1, value.length() - 1);
                    } else {
                        sanitized = value;
                    }
                    if (txt.contains(sanitized)) {
                        break;
                    }
                    this.failures.add(this.expression);
                }
            }
        }

        /**
         * Lines assertion.
         * @since 0.0.43
         */
        final class HasLines implements WpaStory.Assertion {

            /**
             * Expression.
             */
            private final String expression;

            /**
             * Failures.
             */
            private final List<String> failures;

            /**
             * Defects.
             */
            private final Collection<Defect> defects;

            /**
             * Ctor.
             * @param expr Expression
             * @param flrs Failures
             * @param found Defects
             */
            HasLines(final String expr, final List<String> flrs, final Collection<Defect> found) {
                this.expression = expr;
                this.failures = flrs;
                this.defects = found;
            }

            @Override
            public void exec() {
                new ListOf<>(
                    this.expression.substring("lines()=".length())
                        .replace("[", "").replace("]", "").split(",")
                ).forEach(
                    ref -> {
                        final String[] parts = ref.trim().split(":");
                        final boolean matches = this.defects.stream().anyMatch(
                            defect -> defect.program().equals(parts[0])
                                && defect.line() == Integer.parseInt(parts[1])
                        );
                        if (!matches) {
                            this.failures.add(this.expression);
                        }
                    }
                );
            }
        }
    }
}
