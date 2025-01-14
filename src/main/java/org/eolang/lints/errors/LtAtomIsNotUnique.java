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
package org.eolang.lints.errors;

import com.jcabi.xml.XML;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.cactoos.io.ResourceOf;
import org.cactoos.text.TextOf;
import org.cactoos.text.UncheckedText;
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
        final Collection<String> checked = new HashSet<>(0);
        index.forEach(
            (xmir, atoms) -> {
                atoms.stream()
                    .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                    .entrySet()
                    .stream()
                    .filter(entry -> entry.getValue() > 1L)
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
                    .forEach(
                        (aname, count) ->
                            IntStream.range(0, Math.toIntExact(count)).forEach(
                                pos -> defects.add(this.singleDefect(xmir, aname, pos))
                            )
                    );
                index.forEach(
                    (next, natoms) -> {
                        if (!Objects.equals(next, xmir)) {
                            final String pair = LtAtomIsNotUnique.pairHash(xmir, next);
                            if (!checked.contains(pair)) {
                                checked.add(pair);
                                natoms.stream()
                                    .filter(atoms::contains)
                                    .forEach(
                                        aname -> {
                                            defects.add(this.sharedDefect(next, xmir, aname));
                                            defects.add(this.sharedDefect(xmir, next, aname));
                                        }
                                    );
                            }
                        }
                    }
                );
            }
        );
        return defects;
    }

    @Override
    public String motive() {
        return new UncheckedText(
            new TextOf(
                new ResourceOf(
                    String.format(
                        "org/eolang/motives/errors/%s.md", this.name()
                    )
                )
            )
        ).asString();
    }

    private Defect singleDefect(final XML xmir, final String atom, final int pos) {
        return new Defect.Default(
            this.name(),
            Severity.ERROR,
            xmir.xpath("/program/@name").stream()
                .findFirst().orElse("unknown"),
            Integer.parseInt(
                xmir.xpath(
                    String.format("//o[@atom='%s']/@line", atom)
                ).get(pos)
            ),
            String.format("Atom '%s' is duplicated", atom)
        );
    }

    private Defect sharedDefect(final XML xmir, final XML original, final String atom) {
        return new Defect.Default(
            this.name(),
            Severity.ERROR,
            xmir.xpath("/program/@name").stream().findFirst().orElse("unknown"),
            Integer.parseInt(
                xmir.xpath(
                    String.format("//o[@atom='%s']/@line", atom)
                ).get(0)
            ),
            String.format(
                "Atom '%s' is duplicated, original was found in '%s'",
                atom, original.xpath("/program/@name").stream().findFirst().orElse("unknown")
            )
        );
    }

    private static String pairHash(final XML first, final XML second) {
        final String pair;
        if (first.hashCode() < second.hashCode()) {
            pair = String.format("%d:%d", first.hashCode(), second.hashCode());
        } else {
            pair = String.format("%d:%d", second.hashCode(), first.hashCode());
        }
        return pair;
    }
}
