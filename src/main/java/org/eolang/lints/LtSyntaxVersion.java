/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import com.github.lombrozo.xnav.Xnav;
import com.jcabi.xml.XML;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Lint that checks the {@code +syntax} meta against the actual EO parser version.
 *
 * <p>The {@code +syntax} meta declares the minimum EO version the code requires,
 * e.g. {@code +syntax 0.59.0}. Comparison is strict: only the numeric
 * {@code major.minor.patch} core of both versions is compared; pre-release
 * suffixes and non-SemVer values are ignored (not flagged).</p>
 *
 * @since 0.2.11
 */
final class LtSyntaxVersion implements Lint {

    @Override
    public String name() {
        return "syntax-version";
    }

    @Override
    public Collection<Defect> defects(final XML xmir) throws IOException {
        final Xnav xml = new Xnav(xmir.inner());
        final Optional<Version> actual = xml.path("/object").findFirst()
            .flatMap(root -> root.attribute("version").text())
            .flatMap(Version::parsed);
        final Collection<Defect> defects;
        if (actual.isPresent()) {
            defects = xml.path("/object/metas/meta[head='syntax']")
                .map(meta -> LtSyntaxVersion.outdated(meta, actual.get()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
        } else {
            defects = Collections.emptyList();
        }
        return defects;
    }

    @Override
    public String motive() throws IOException {
        return new MotiveFrom("metas", this.name()).asString();
    }

    @Override
    public Fix fix() {
        return new FxEmpty();
    }

    private static Optional<Defect> outdated(
        final Xnav meta, final Version actual
    ) {
        final String declared = meta.element("tail").text().orElse("");
        return Version.parsed(declared)
            .filter(version -> version.newerThan(actual)).map(
                version -> new Defect.Default(
                    "syntax-version",
                    Severity.ERROR,
                    new LineOf(meta).value(),
                    String.format(
                        "The +syntax meta requires EO %s or newer, but the parser is EO %s",
                        declared, actual
                    )
                )
            );
    }
}
