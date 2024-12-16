package org.eolang.lints;

import com.jcabi.xml.XML;
import java.io.IOException;
import java.util.Objects;
import org.cactoos.Scalar;
import org.cactoos.scalar.IoChecked;
import org.cactoos.scalar.Sticky;
import org.cactoos.scalar.Synced;

final class Lints {

    /**
     * Lints to use.
     */
    private static final Scalar<Iterable<Lint<XML>>> LINTS = new Sticky<>(
        () -> new ProgramLints().value()
    );


    private final Scalar<Iterable<Lint<XML>>> all;

    Lints() {
        this(Lints.LINTS);
    }

    Lints(final Scalar<Iterable<Lint<XML>>> all) {
        this.all = new Synced<>(all);
    }

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
