package org.eolang.lints;

import java.io.IOException;
import org.cactoos.map.MapEntry;
import org.cactoos.map.MapOf;
import org.eolang.parser.EoSyntax;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link LtInconsistentArgs}.
 *
 * @since 0.41.0
 */
final class LtInconsistentArgsTest {

    @Test
    void catchesArgumentsInconsistency() throws IOException {
        MatcherAssert.assertThat(
            "Defects are empty, but they should not",
            new LtInconsistentArgs().defects(
                new MapOf<>(
                    new MapEntry<>(
                        "foo",
                        new EoSyntax(
                            String.join(
                                "\n",
                                "# Foo",
                                "[] > foo",
                                "  bar 42 > x",
                                "  bar 1 2 3 > y"
                            )
                        ).parsed()
                    )
                )
            ),
            Matchers.hasSize(2)
        );
    }
}
