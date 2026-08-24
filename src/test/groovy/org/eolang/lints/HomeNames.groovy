/*
 * SPDX-FileCopyrightText: Copyright (c) 2016-2026 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
package org.eolang.lints

import com.yegor256.tojos.MnCsv
import com.yegor256.tojos.TjCached
import com.yegor256.tojos.TjDefault
import com.yegor256.tojos.TjSynchronized
import com.yegor256.tojos.Tojos
import java.nio.file.Path
import java.nio.file.Paths

/**
 * Home object names placed into the CSV file.
 * @since 0.0.49
 */
final class HomeNames {

  /**
   * Reserved store.
   */
  private final Tojos placed

  /**
   * Home objects location.
   */
  private final String location

  /**
   * Ctor.
   * @param home Home objects location
   */
  HomeNames(final Path home) {
    this(home.toString())
  }

  /**
   * Ctor.
   * @param hloc Home objects location
   */
  HomeNames(final String hloc) {
    this("target/classes/reserved.csv", hloc)
  }

  /**
   * Ctor.
   * @param csv CSV file path
   * @param hloc Home objects location
   */
  HomeNames(final String csv, final String hloc) {
    this(
      new TjCached(
        new TjSynchronized(
          new TjDefault(
            new MnCsv(Paths.get(csv))
          )
        )
      ),
      hloc
    )
  }

  /**
   * Ctor.
   * @param tjs Reserved store
   * @param hloc Home location
   */
  HomeNames(final Tojos tjs, final String hloc) {
    this.placed = tjs
    this.location = hloc
  }

  /**
   * Place them into CSV file.
   */
  void placeCsv() {
    new HomeObjects(this.location).read().stream()
      .flatMap(map -> map.entrySet().stream())
      .forEach(
        entry -> this.placed.add(entry.key).set("path", entry.value)
      )
  }
}
