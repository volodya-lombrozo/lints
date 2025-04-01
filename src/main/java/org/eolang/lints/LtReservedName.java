/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import com.github.lombrozo.xnav.Xnav;
import com.jcabi.xml.XML;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.cactoos.io.InputOf;
import org.cactoos.io.ResourceOf;
import org.cactoos.io.UncheckedInput;
import org.cactoos.text.TextOf;
import org.cactoos.text.UncheckedText;
import org.eolang.parser.EoSyntax;

/**
 * Lint for reserved names.
 *
 * @since 0.0.43
 */
final class LtReservedName implements Lint<XML> {

    /**
     * Reserved names.
     */
    private final Map<String, String> reserved;

    /**
     * Ctor.
     */
    LtReservedName() {
        this(LtReservedName.reservedInHome());
    }

    /**
     * Ctor.
     *
     * @param names Reserved names
     */
    LtReservedName(final Map<String, String> names) {
        this.reserved = names;
    }

    @Override
    public String name() {
        return "reserved-name";
    }

    @Override
    public Collection<Defect> defects(final XML xmir) throws IOException {
        final Collection<Defect> defects = new LinkedList<>();
        final Xnav program = new Xnav(xmir.inner());
        program.path("//o[@name]")
            .forEach(
                object -> {
                    final String oname = object.attribute("name").text().get();
                    if (this.reserved.keySet().contains(oname)) {
                        defects.add(
                            new Defect.Default(
                                this.name(),
                                Severity.WARNING,
                                program.element("program").attribute("name")
                                    .text().orElse("unknown"),
                                Integer.parseInt(object.attribute("line").text().orElse("0")),
                                String.format(
                                    "Object name \"%s\" is already reserved by object in the \"%s\"",
                                    oname, this.reserved.get(oname)
                                )
                            )
                        );
                    }
                }
            );
        return defects;
    }

    @Override
    public String motive() throws IOException {
        return new UncheckedText(
            new TextOf(
                new ResourceOf(
                    String.format(
                        "org/eolang/motives/names/%s.md", this.name()
                    )
                )
            )
        ).asString();
    }

    private static Map<String, String> reservedInHome() {
        final String cloned = "cloned/home";
        return LtReservedName.home(cloned);
    }

    private static Map<String, String> home(final String location) {
        final Map<String, String> names = new HashMap<>(64);
        final List<Path> result;
        final URL resource = LtReservedName.class.getClassLoader().getResource(location);
        if ("jar".equals(resource.getProtocol())) {
            final String jar = resource.getFile().substring(5, resource.getFile().indexOf('!'));
            final URI uri = URI.create(String.format("jar:file:%s", jar));
            try (FileSystem fs = FileSystems.newFileSystem(uri, Collections.emptyMap())) {
                result = Files.walk(fs.getPath(location)).collect(Collectors.toList());
                result.stream().filter(
                    p -> {
                        final String file = p.toString();
                        return file.endsWith(".eo")
                            && file.contains(String.format("%s/objects", location));
                    }
                ).forEach(
                    eo -> {
                        final XML parsed;
                        try {
                            try (InputStream input = Files.newInputStream(eo)) {
                                parsed = new EoSyntax(
                                    "reserved", new TextOf(input).asString()
                                ).parsed();
                            }
                        } catch (final Exception exception) {
                            throw new IllegalStateException(
                                String.format("Failed to parse EO source in \"%s\"", eo),
                                exception
                            );
                        }
                        new Xnav(parsed.inner()).path("/program/objects/o/@name")
                            .map(oname -> oname.text().get())
                            .forEach(
                                oname ->
                                    names.put(
                                        oname,
                                        eo.toString().replace(String.format("%s/objects", location), "")
                                            .substring(1).replace("/", ".")
                                    )
                            );
                    }
                );
            } catch (final IOException exception) {
                throw new IllegalStateException(
                    "Failed to read home objects from JAR", exception
                );
            }
        } else {
            try {
                result = Files.walk(Paths.get(resource.getPath())).collect(Collectors.toList());
                result.stream().filter(
                    p -> {
                        final String file = p.toString();
                        return file.endsWith(".eo")
                            && file.contains(String.format("%s/objects", location));
                    }
                ).forEach(
                    eo -> {
                        final XML parsed;
                        try {
                            parsed = new EoSyntax(
                                "reserved", new UncheckedInput(new InputOf(eo.toFile()))
                            ).parsed();
                        } catch (final IOException exception) {
                            throw new IllegalStateException(
                                String.format("Failed to parse EO source in \"%s\"", eo),
                                exception
                            );
                        }
                        new Xnav(parsed.inner()).path("/program/objects/o/@name")
                            .map(oname -> oname.text().get())
                            .forEach(
                                oname ->
                                    names.put(
                                        oname,
                                        eo.toString().replace(String.format("%s/objects", location), "")
                                            .substring(1).replace("/", ".")
                                    )
                            );
                    }
                );
            } catch (final IOException exception) {
                throw new IllegalStateException("Failed to walk through files", exception);
            }
        }
        return names;
    }
}
