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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
        final Optional<LtSyntaxVersion.Version> actual = xml.path("/object").findFirst()
            .flatMap(root -> root.attribute("version").text())
            .flatMap(LtSyntaxVersion.Version::parsed);
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

    /**
     * Build a defect when the {@code +syntax} meta requires a newer version than {@code actual}.
     * @param meta The {@code +syntax} meta element
     * @param actual The actual parser version
     * @return Defect, if the declared version is newer than the actual one
     */
    private static Optional<Defect> outdated(
        final Xnav meta, final LtSyntaxVersion.Version actual
    ) {
        final String declared = meta.element("tail").text().orElse("");
        return LtSyntaxVersion.Version.parsed(declared)
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

    /**
     * A strict {@code major.minor.patch} version.
     * @since 0.2.11
     */
    private static final class Version {

        /**
         * Matches the numeric {@code major.minor.patch} core of a version string.
         */
        private static final Pattern CORE = Pattern.compile("^(\\d+)\\.(\\d+)\\.(\\d+)");

        /**
         * Major version.
         */
        private final int major;

        /**
         * Minor version.
         */
        private final int minor;

        /**
         * Patch version.
         */
        private final int patch;

        /**
         * Ctor.
         * @param mjr Major version
         * @param mnr Minor version
         * @param pch Patch version
         */
        private Version(final int mjr, final int mnr, final int pch) {
            this.major = mjr;
            this.minor = mnr;
            this.patch = pch;
        }

        @Override
        public String toString() {
            return String.format("%d.%d.%d", this.major, this.minor, this.patch);
        }

        /**
         * Parse a version string, strictly.
         * @param text Version text, e.g. {@code "0.59.0"} or {@code "0.59.0-SNAPSHOT"}
         * @return Parsed version, or empty if it doesn't match
         */
        private static Optional<LtSyntaxVersion.Version> parsed(final String text) {
            final Matcher matcher = LtSyntaxVersion.Version.CORE.matcher(text.trim());
            final Optional<LtSyntaxVersion.Version> result;
            if (matcher.find()) {
                result = Optional.of(
                    new LtSyntaxVersion.Version(
                        Integer.parseInt(matcher.group(1)),
                        Integer.parseInt(matcher.group(2)),
                        Integer.parseInt(matcher.group(3))
                    )
                );
            } else {
                result = Optional.empty();
            }
            return result;
        }

        /**
         * Is this version strictly newer than {@code other}?
         * @param other Version to compare against
         * @return True if this version is newer
         */
        private boolean newerThan(final LtSyntaxVersion.Version other) {
            final boolean newer;
            if (this.major == other.major) {
                if (this.minor == other.minor) {
                    newer = this.patch > other.patch;
                } else {
                    newer = this.minor > other.minor;
                }
            } else {
                newer = this.major > other.major;
            }
            return newer;
        }
    }
}
