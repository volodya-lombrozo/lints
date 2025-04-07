/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package fixtures;

/**
 * Program size.
 * @since 0.0.45
 */
public enum ProgramSize {

    /**
     * Standard program.
     */
    S("S", 5, 50, "com/sun/jna/PointerType.class"),

    /**
     * Medium-sized program.
     */
    M("M", 90, 200, "com/sun/jna/Memory.class"),

    /**
     * Large-size program.
     */
    L("L", 400, 700, "com/sun/jna/Pointer.class"),

    /**
     * Extra-large program.
     */
    XL("XL", 800, 1200, "com/sun/jna/Structure.class"),

    /**
     * XXL program.
     */
    XXL(
        "XXL",
        1500,
        Integer.MAX_VALUE,
        "org/apache/hadoop/hdfs/server/namenode/FSNamesystem.class"
    );

    /**
     * Size marker.
     */
    private final String marker;

    /**
     * Min size, in executable lines.
     */
    private final int min;

    /**
     * Max size, in executable lines.
     */
    private final int max;

    /**
     * Java '.class' path.
     */
    private final String jclass;

    /**
     * Ctor.
     * @param txt Txt marker
     * @param start Minimum size in executable lines
     * @param end Maximum size in executable lines
     */
    ProgramSize(final String txt, final int start, final int end, final String java) {
        this.marker = txt;
        this.min = start;
        this.max = end;
        this.jclass = java;
    }

    /**
     * Marker size.
     * @return Marker
     */
    public String size() {
        return this.marker;
    }

    /**
     * Min allowed count of executable lines.
     * @return Lines count
     */
    public int minAllowed() {
        return this.min;
    }

    /**
     * Max allowed count of executable lines.
     * @return Lines count
     */
    public int maxAllowed() {
        return this.max;
    }

    public String java() {
        return this.jclass;
    }
}
