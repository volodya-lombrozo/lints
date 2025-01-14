/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016-2024 Objectionary.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.eolang.lints.critical;

import com.jcabi.xml.XML;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.eolang.lints.Defect;
import org.eolang.lints.Lint;
import org.eolang.lints.Severity;

/**
 * All `@atom` values in the entire scope must be unique.
 *
 * @since 0.0.31
 */
public final class LtAtomIsNotUnique implements Lint<Map<String, XML>> {

    @Override
    public String name() {
        return "atom-is-not-unique";
    }

    @Override
    public Collection<Defect> defects(final Map<String, XML> pkg) throws IOException {
        final Collection<Defect> defects = new LinkedList<>();
        final Map<XML, List<String>> index = pkg.values().stream().collect(
            Collectors.toMap(
                Function.identity(),
                xmir -> xmir.xpath("//o[@atom]/@atom")
            )
        );
        final Set<String> checked = new HashSet<>(0);
        index.forEach(
            (xmir, atoms) -> {
                final Map<String, Long> local = atoms.stream()
                    .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                    .entrySet()
                    .stream()
                    .filter(entry -> entry.getValue() > 1L)
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
                local.forEach(
                    (aname, count) -> IntStream.range(0, Math.toIntExact(count))
                        .forEach(
                            value -> defects.add(
                                new Defect.Default(
                                    this.name(),
                                    Severity.ERROR,
                                    xmir.xpath("/program/@name").stream()
                                        .findFirst().orElse("unknown"),
                                    Integer.parseInt(
                                        xmir.xpath(
                                            String.format("//o[@atom='%s']/@line", aname)
                                        ).get(value)
                                    ),
                                    String.format(
                                        "Atom '%s' is duplicated",
                                        aname
                                    )
                                )
                            )
                        )
                );
                index.forEach(
                    new BiConsumer<XML, List<String>>() {
                        @Override
                        public void accept(final XML next, final List<String> natoms) {
                            if (!Objects.equals(next, xmir)) {
                                final String pair;
                                if (xmir.hashCode() < next.hashCode()) {
                                    pair = String.format("%d:%d", xmir.hashCode(), next.hashCode());
                                } else {
                                    pair = String.format("%d:%d", next.hashCode(), xmir.hashCode());
                                }
                                if (!checked.contains(pair)) {
                                    checked.add(pair);
                                    natoms.stream()
                                        .filter(atoms::contains)
                                        .forEach(
                                            new Consumer<>() {
                                                @Override
                                                public void accept(final String aname) {
                                                    final String nname = next.xpath("/program/@name").stream()
                                                        .findFirst().orElse("unknown");
                                                    final String original = xmir.xpath("/program/@name").stream()
                                                        .findFirst().orElse("unknown");
                                                    defects.add(
                                                        new Defect.Default(
                                                            LtAtomIsNotUnique.this.name(),
                                                            Severity.ERROR,
                                                            nname,
                                                            0,
                                                            String.format(
                                                                "Atom '%s' is duplicated, original was found in '%s'",
                                                                aname, original
                                                            )
                                                        )
                                                    );
                                                    defects.add(
                                                        new Defect.Default(
                                                            LtAtomIsNotUnique.this.name(),
                                                            Severity.ERROR,
                                                            original,
                                                            0,
                                                            String.format(
                                                                "Atom '%s' is duplicated, original was found in '%s'",
                                                                aname, nname
                                                            )
                                                        )
                                                    );
                                                }
                                            }
                                        );
                                }
                            }
                        }
                    }
                );
            }
        );
        return defects;
    }

    @Override
    public String motive() throws Exception {
        throw new UnsupportedOperationException("#motive()");
    }
}
