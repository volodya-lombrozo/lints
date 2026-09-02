/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package fixtures;

import com.jcabi.xml.XML;
import com.yegor256.xsline.Xsline;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import org.cactoos.map.MapEntry;
import org.cactoos.map.MapOf;
import org.eolang.xax.Xtory;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Test for {@link XtDefects}.
 * @since 1.0
 */
final class XtDefectsTest {

    @Test
    void addsCountOnlyAssertionForIntegerDefects() {
        MatcherAssert.assertThat(
            "Integer 'defects' must produce a plain count assertion",
            new XtDefects(new XtDefectsTest.FakeXtory(Map.of("defects", 0))).asserts(),
            Matchers.hasItem("/defects[count(defect)=0]")
        );
    }

    @Test
    void addsCountAndSeverityAndLineAssertionsForSingleDefect() {
        final Collection<String> asserts = new XtDefects(
            new XtDefectsTest.FakeXtory(
                new MapOf<>(
                    new MapEntry<>(
                        "defects",
                        Collections.singletonList(
                            Map.of("severity", "warning", "line", 1)
                        )
                    )
                )
            )
        ).asserts();
        MatcherAssert.assertThat(
            "A single defect entry must assert count, severity, and line",
            asserts,
            Matchers.hasItems(
                "/defects[count(defect)=1]",
                "/defects/defect[@severity='warning' and @line='1']"
            )
        );
    }

    @Test
    void addsAssertionsForEachDefectInMultiDefectList() {
        final Collection<String> asserts = new XtDefects(
            new XtDefectsTest.FakeXtory(
                new MapOf<>(
                    new MapEntry<>(
                        "defects",
                        java.util.List.of(
                            Map.of("severity", "error", "line", 3),
                            Map.of("severity", "error", "line", 7)
                        )
                    )
                )
            )
        ).asserts();
        MatcherAssert.assertThat(
            "Every defect entry in the list must get its own severity/line assertion",
            asserts,
            Matchers.hasItems(
                "/defects[count(defect)=2]",
                "/defects/defect[@severity='error' and @line='3']",
                "/defects/defect[@severity='error' and @line='7']"
            )
        );
    }

    /**
     * A minimal fake {@link Xtory} exposing only a YAML map.
     * @since 1.0
     */
    private static final class FakeXtory implements Xtory {
        /**
         * The map.
         */
        private final Map<String, Object> yaml;

        /**
         * Ctor.
         * @param map The map
         */
        FakeXtory(final Map<String, Object> map) {
            this.yaml = map;
        }

        @Override
        public Map<String, Object> map() {
            return this.yaml;
        }

        @Override
        public XML before() {
            throw new UnsupportedOperationException("not needed");
        }

        @Override
        public XML after() {
            throw new UnsupportedOperationException("not needed");
        }

        @Override
        public Xsline xsline() {
            throw new UnsupportedOperationException("not needed");
        }

        @Override
        public Collection<String> asserts() {
            return Collections.emptyList();
        }
    }
}
