/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints;

import com.jcabi.xml.XML;
import java.io.IOException;
import java.util.Collection;

/**
 * A single checker for an {@code .xmir} file.
 *
 * @param <T> The type of entity to analyze
 * @since 0.0.1
 * @todo #368:90min Implement lint caching decorator.
 *  We should cache `Lint.defects()` to cache found defects when calling
 *  particular lint. Don't forget to add unit tests, and benchmark tests.
 */
public interface Lint<T> {

    /**
     * Name of the lint.
     * @return Lint name
     */
    String name();

    /**
     * Find and return defects.
     * @param entity The entity to analyze (could be {@link XML}
     *  or {@link java.nio.file.Path})
     * @return Defects
     */
    Collection<Defect> defects(T entity) throws IOException;

    /**
     * Returns motive for a lint, explaining why this lint exists.
     * @return Motive text about lint
     * @throws IOException if something went wrong
     */
    String motive() throws IOException;

}
