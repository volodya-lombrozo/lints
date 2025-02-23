/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package fixtures;

import com.jcabi.xml.XML;
import com.jcabi.xml.XMLDocument;
import com.yegor256.farea.Farea;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicReference;
import org.cactoos.Scalar;
import org.cactoos.bytes.BytesOf;
import org.cactoos.bytes.UncheckedBytes;
import org.cactoos.io.ResourceOf;
import org.xembly.Directives;
import org.xembly.Xembler;

/**
 * Large XMIR document.
 *
 * @since 0.0.31
 */
public final class LargeXmir implements Scalar<XML> {

    /**
     * Name of the program.
     */
    private final String name;

    /**
     * Constructor.
     */
    public LargeXmir() {
        this("unknown");
    }

    /**
     * Constructor.
     * @param nme Program name.
     */
    public LargeXmir(final String nme) {
        this.name = nme;
    }

    @Override
    public XML value() throws Exception {
        final Path home = Files.createTempDirectory("tmp");
        final String path = "com/sun/jna/Pointer.class";
        final AtomicReference<XML> ref = new AtomicReference<>();
        new Farea(home).together(
            f -> {
                f.clean();
                f.files()
                    .file(String.format("target/classes/%s", path))
                    .write(
                        new UncheckedBytes(
                            new BytesOf(
                                new ResourceOf(
                                    "com/sun/jna/Pointer.class"
                                )
                            )
                        ).asBytes()
                    );
                f.build()
                    .plugins()
                    .append("org.eolang", "jeo-maven-plugin", "0.7.2")
                    .execution("default")
                    .phase("process-classes")
                    .goals("disassemble");
                f.exec("process-classes");
                ref.set(
                    new XMLDocument(
                        f.files().file(
                            "target/generated-sources/jeo-xmir/com/sun/jna/Pointer.xmir"
                        ).path()
                    )
                );
            }
        );
        final XML xml = ref.get();
        new Xembler(
            new Directives().xpath("/program").attr("name", this.name)
        ).apply(xml.inner());
        return xml;
    }
}
