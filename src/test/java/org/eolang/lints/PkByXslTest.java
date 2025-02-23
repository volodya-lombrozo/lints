/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import io.github.secretx33.resourceresolver.PathMatchingResourcePatternResolver;
import io.github.secretx33.resourceresolver.Resource;
import java.io.IOException;
import java.util.Arrays;
import org.cactoos.io.InputOf;
import org.cactoos.proc.ForEach;
import org.cactoos.text.TextOf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test for {@link PkByXsl}.
 *
 * @since 0.0.1
 */
final class PkByXslTest {

    @Test
    void passesOnSimpleXmir() throws IOException {
        final XML xmir = new XMLDocument("<program name='no-exceptions'/>");
        for (final Lint<XML> lint : new PkByXsl()) {
            MatcherAssert.assertThat(
                "passes with no exceptions",
                lint.defects(xmir),
                Matchers.notNullValue()
            );
        }
    }

    @Test
    void checksIdsOfEveryXslStylesheet() throws Exception {
        new ForEach<Resource>(
            res -> {
                final XML sheet = new XMLDocument(
                    new TextOf(new InputOf(res.getInputStream())).asString()
                );
                MatcherAssert.assertThat(
                    "the @id is equal to the basename of the file",
                    sheet.xpath("/xsl:stylesheet/@id").get(0),
                    Matchers.equalTo(res.getFilename().replaceAll(".xsl$", ""))
                );
            }
        ).exec(
            Arrays.asList(
                new PathMatchingResourcePatternResolver().getResources(
                    "classpath*:org/eolang/lints/**/*.xsl"
                )
            )
        );
    }

    @Test
    void checksAllMotives() throws Exception {
        for (final Lint<XML> lint : new PkByXsl()) {
            MatcherAssert.assertThat(
                "Lint's motive is empty, but should not be",
                lint.motive().isEmpty(),
                Matchers.equalTo(false)
            );
        }
    }

}
