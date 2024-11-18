/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2024 Objectionary.com
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
package org.eolang.lints;

/**
 * A single defect found.
 *
 * @since 0.0.1
 */
public interface Defect {

    /**
     * Severity.
     * @return Severity
     */
    Defect.Severity severity();

    /**
     * Line where it was found.
     * @return Line number
     */
    int line();

    /**
     * Error message.
     * @return Text
     */
    String text();

    /**
     * Severity.
     *
     * @since 0.0.1
     */
    enum Severity {
        /**
         * Compilation can't continue, must stop.
         */
        CRITICAL,
        /**
         * It's a bug, must be fixed.
         */
        ERROR,
        /**
         * Can live with it, but better be fixed.
         */
        WARNING;

        /**
         * Parse it from the text.
         * @param text Text of it
         * @return Severity
         */
        public static Defect.Severity parsed(final String text) {
            final Defect.Severity severity;
            if ("criticial".equals(text)) {
                severity = Defect.Severity.CRITICAL;
            } else if ("error".equals(text)) {
                severity = Defect.Severity.ERROR;
            } else if ("warning".equals(text)) {
                severity = Defect.Severity.WARNING;
            } else {
                throw new IllegalArgumentException(
                    String.format("Can't parse '%s'", text)
                );
            }
            return severity;
        }
    }

    /**
     * Default.
     *
     * @since 0.0.1
     */
    final class Default implements Defect {
        /**
         * Severity.
         */
        private final Defect.Severity sev;

        /**
         * Line number with the defect.
         */
        private final int lineno;

        /**
         * The message of the problem.
         */
        private final String txt;

        /**
         * Ctor.
         * @param severity Severity
         * @param line Line number
         * @param text Description of the defect
         */
        Default(final Defect.Severity severity, final int line, final String text) {
            this.sev = severity;
            this.lineno = line;
            this.txt = text;
        }

        @Override
        public Defect.Severity severity() {
            return this.sev;
        }

        @Override
        public int line() {
            return this.lineno;
        }

        @Override
        public String text() {
            return this.txt;
        }
    }

}
