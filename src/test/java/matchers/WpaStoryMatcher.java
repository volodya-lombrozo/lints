package matchers;

import java.util.Map;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

/**
 * Hamcrest matcher to check outcome of {@link org.eolang.lints.WpaStory}.
 *
 * @since 0.0.43
 */
public final class WpaStoryMatcher extends BaseMatcher<Map<String, String>> {

    @Override
    public boolean matches(final Object input) {
        final Map<String, String> failures = (Map<String, String>) input;
        return failures.isEmpty();
    }

    @Override
    public void describeTo(final Description description) {
        description.appendText("an empty failures map");
    }

    @Override
    public void describeMismatch(final Object input, final Description description) {
        final Map<String, String> failures = (Map<String, String>) input;
        final StringBuilder message = new StringBuilder(0);
        failures.forEach(
            (failure, explanation) ->
                message.append(String.format("FAIL: %s, (%s)", failure, explanation))
        );
        description.appendText(message.toString());
    }
}
