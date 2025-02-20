package org.eolang.lints;

import com.github.lombrozo.xnav.Xnav;
import com.jcabi.xml.XML;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Lint for checking arguments inconsistency provided to the objects.
 *
 * @since 0.0.41
 */
public final class LtInconsistentArgs implements Lint<Map<String, XML>> {

    @Override
    public String name() {
        return "inconsistent-args";
    }

    @Override
    public Collection<Defect> defects(final Map<String, XML> pkg) throws IOException {
        final List<Defect> defects = new LinkedList<>();
//        final Map<String, List<Integer>> whole = new HashMap<>();
        pkg.values().forEach(
            xmir -> {
                final Map<String, List<Integer>> local = new HashMap<>(0);
                final Xnav program = new Xnav(xmir.inner());
                program.path("//o[@base]").forEach(
                    base -> {
                        final int args = base.node().getChildNodes().getLength();
                        local.computeIfAbsent(
                            base.attribute("base").text().get(),
                            k -> new ArrayList<>()
                        ).add(args);
                    }
                );
                local.forEach(
                    (base, counts) -> {
                        if (counts.stream().distinct().count() != 1L) {
                            final List<Integer> lines = program.path(
                                    String.format(
                                        "//o[@base='%s']",
                                        base
                                    )
                                )
                                .map(o -> Integer.parseInt(o.attribute("line").text().orElse("0")))
                                .collect(Collectors.toList());
                            lines.forEach(
                                line ->
                                    defects.add(
                                        new Defect.Default(
                                            this.name(),
                                            Severity.WARNING,
                                            program.element("program").attribute("name")
                                                .text().orElse("unknown"),
                                            line,
                                            String.format(
                                                "Object '%s' has arguments inconsistency: different number of arguments were passed in %s",
                                                base,
                                                LtInconsistentArgs.oppositeRefs(
                                                    program, base, lines, line
                                                )
                                            )
                                        )
                                    )
                            );
                        }
                    }
                );
            }
        );
//        whole.forEach(
//            new BiConsumer<String, List<Integer>>() {
//                @Override
//                public void accept(final String base, final List<Integer> counts) {
//                    if (counts.stream().distinct().count() != 1L) {
//                        defects.add(
//                            new Defect.Default(
//                                LtInconsistentArgs.this.name(),
//                                Severity.WARNING,
//                                "",
//                                0,
//                                String.format(
//                                    "Object '%s' has arguments inconsistency",
//                                    base
//                                )
//                            )
//                        );
//                    }
//                }
//            }
//        );
        return defects;
    }

    @Override
    public String motive() throws IOException {
        throw new UnsupportedOperationException("#motive()");
    }

    private static String oppositeRefs(
        final Xnav program,
        final String base,
        final List<Integer> lines,
        final Integer problematic
    ) {
        final String source = program.element("program").attribute("name").text()
            .orElse("unknown");
        final StringBuilder out = new StringBuilder(0);
        lines.forEach(
            line -> {
                if (!line.equals(problematic)) {
                    out.append(
                        String.format(
                            "%s:%s:%d",
                            source,
                            base,
                            line
                        )
                    );
                }
            }
        );
        return out.toString();
    }
}
