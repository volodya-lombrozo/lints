/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import com.jcabi.xml.XML;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.eolang.jucs.ClasspathSource;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;

/**
 * Tests for {@link WpaLints}.
 *
 * @since 0.0.43
 */
final class WpaLintsTest {

    @Test
    @SuppressWarnings("JTCOP.RuleAssertionMessage")
    void staysPackagePrivate() {
        ArchRuleDefinition.classes()
            .that().haveSimpleName("WpaLints")
            .should().bePackagePrivate()
            .check(new ClassFileImporter()
                .withImportOption(new ImportOption.DoNotIncludeTests())
                .importPackages("org.eolang.lints")
            );
    }

    @SuppressWarnings("JTCOP.RuleNotContainsTestWord")
    @ParameterizedTest
    @ClasspathSource(value = "org/eolang/lints/packs/wpa/", glob = "**.yaml")
    void testsAllLintsByEo(final String yaml) throws IOException {
        final Map<String, Lint<Map<String, XML>>> wpa = new HashMap<>(0);
        new WpaLints().forEach(wpl -> wpa.put(wpl.name(), wpl));
        MatcherAssert.assertThat(
            "Doesn't tell the story as it's expected",
            new WpaStory(yaml, wpa).execute(),
            Matchers.anEmptyMap()
        );
    }
}
