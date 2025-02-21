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
        final Map<Xnav, Map<String, List<Integer>>> whole = new HashMap<>(0);
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
                whole.put(program, local);
            }
        );
        final Map<String, List<Integer>> merged = new HashMap<>(0);
        for (final Map<String, List<Integer>> localized : whole.values()) {
            for (final Map.Entry<String, List<Integer>> entry : localized.entrySet()) {
                merged.computeIfAbsent(entry.getKey(), k -> new ArrayList<>())
                    .addAll(entry.getValue());
            }
        }
        final Map<String, List<Xnav>> inverted = new HashMap<>(0);
        for (final Map.Entry<Xnav, Map<String, List<Integer>>> entry : whole.entrySet()) {
            final Xnav program = entry.getKey();
            for (final Map.Entry<String, List<Integer>> local : entry.getValue().entrySet()) {
                final String base = local.getKey();
                inverted.computeIfAbsent(base, k -> new ArrayList<>()).add(program);
            }
        }
        merged.forEach(
            (base, counts) -> {
                if (counts.stream().distinct().count() != 1L) {
                    final List<Xnav> programs = inverted.get(base);
                    programs.forEach(
                        program -> {
                            final List<Integer> lines = program.path(String.format("//o[@base='%s']", base))
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
                                            String.format("Object '%s' has arguments inconsistency", base)
                                        )
                                    )
                            );
                        }
                    );
                }
            }
        );

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
