/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2025 Objectionary.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.eolang.lints;

import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;
import com.yegor256.Together;
import java.io.IOException;
import java.util.stream.Collectors;
import org.cactoos.list.ListOf;
import org.cactoos.scalar.LengthOf;
import org.cactoos.set.SetOf;
import org.eolang.parser.EoSyntax;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link PkMono}.
 *
 * @since 0.23
 */
final class PkMonoTest {

    @Tag("deep")
    @RepeatedTest(5)
    void createsLintsInParallel() {
        MatcherAssert.assertThat(
            "",
            new SetOf<>(
                new Together<>(
                    thread -> new LengthOf(new PkMono()).value()
                )
            ).size(),
            Matchers.equalTo(1)
        );
    }

    @Test
    void allowsUnlint() throws IOException {
        MatcherAssert.assertThat(
            "Defects found, though they were unlinted",
            new Program(
                new EoSyntax(
                    "+unlint ascii-only\n # привет\n# как дела?\n[] > foo\n"
                ).parsed(),
                new PkMono()
            ).defects().stream().filter(
                defect -> "ascii-only".equals(defect.rule())
            ).collect(Collectors.toList()),
            Matchers.emptyIterable()
        );
    }

    @Test
    void checksThatLintsCanBeUnlinted() {
        new ListOf<>(new PkMono()).stream()
            .filter(lint -> !lint.getClass().equals(LtIncorrectUnlint.class))
            .collect(Collectors.toList()).forEach(
                lint ->
                    MatcherAssert.assertThat(
                        String.format(
                            "Lint '%s' can not be unlinted, since its not wrapped by LtUnlint",
                            lint.name()
                        ),
                        lint.getClass().equals(LtUnlint.class),
                        new IsEqual<>(true)
                    )
            );
    }

    @Test
    void staysInsideThePackage() {
        ArchRuleDefinition.classes()
            .that().haveSimpleName("PkMono")
            .should().bePackagePrivate()
            .check(new ClassFileImporter()
                .withImportOption(new ImportOption.DoNotIncludeTests())
                .importPackages("org.eolang.lints")
            );
    }

}
