package org.eolang.lints;

import com.jcabi.xml.XML;
import java.io.IOException;
import java.util.Objects;
import org.cactoos.Scalar;
import org.cactoos.scalar.IoChecked;
import org.cactoos.scalar.Sticky;
import org.cactoos.scalar.Synced;

/**
 * Lints to use.
 * @since 0.1
 */
final class Lints {

    /**
     * Default lints.
     */
    private static final Scalar<Iterable<Lint<XML>>> LINTS = new Sticky<>(
        () -> new ProgramLints().value()
    );

    /**
     * All lints.
     */
    private final Scalar<Iterable<Lint<XML>>> all;

    /**
     * Default ctor.
     */
    Lints() {
        this(Lints.LINTS);
    }

    /**
     * Ctor.
     * @param all All lints.
     */
    Lints(final Scalar<Iterable<Lint<XML>>> all) {
        this.all = new Synced<>(all);
    }

    /**
     * Iterate over lints.
     * Pay attention that this method can throw an exception.
     * This is why we don't inherit from {@link Iterable}.
     * @return Lints
     * @throws IOException If fails
     */
    Iterable<Lint<XML>> iterator() throws IOException {
        final Iterable<Lint<XML>> res = new IoChecked<>(this.all).value();
        if (Objects.isNull(res)) {
            throw new IllegalStateException(
                String.format("Lints are null before iteration in the %s", this)
            );
        }
        return res;
    }
}
