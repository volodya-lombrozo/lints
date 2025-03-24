/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package matchers;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import org.eolang.lints.Defect;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

/**
 * Hamcrest matcher to check outcome of {@link org.eolang.lints.WpaStory}.
 *
 * @since 0.0.43
 */
public final class WpaStoryMatcher extends BaseMatcher<Map<List<String>, Collection<Defect>>> {

    @Override
    public boolean matches(final Object input) {
        final Map<List<String>, Collection<Defect>> outcome =
            (Map<List<String>, Collection<Defect>>) input;
        return outcome.isEmpty() || outcome.keySet().iterator().next().isEmpty();
    }

    @Override
    public void describeTo(final Description description) {
        description.appendText("an empty failures map");
    }

    @Override
    public void describeMismatch(final Object input, final Description description) {
        final Map<List<String>, Collection<Defect>> outcome =
            (Map<List<String>, Collection<Defect>>) input;
        final StringBuilder message = new StringBuilder(0);
        final List<String> failures = outcome.keySet().iterator().next();
        if (!failures.isEmpty()) {
            message.append(String.format("found %d failure", failures.size()));
            if (failures.size() > 1) {
                message.append('s');
            }
            message.append(':');
            failures.forEach(
                f -> message.append('\n').append(String.format("FAIL: %s", f))
            );
            message.append("\n\n").append("Found defects:\n");
            outcome.get(failures).forEach(message::append);
        }
        description.appendText(message.toString());
    }
}
