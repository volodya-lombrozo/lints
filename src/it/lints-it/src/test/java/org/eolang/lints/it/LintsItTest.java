/*
 * The MIT License (MIT)
 *
 * SPDX-FileCopyrightText: Copyright (c) 2016-2025 Objectionary.com
 * SPDX-License-Identifier: MIT
 */
import com.jcabi.xml.XMLDocument;
import org.junit.jupiter.api.Test;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.eolang.lints.Program;
import java.io.IOException;

final class LintsItTest {

    @Test
    void lintsProgram() throws IOException {
        MatcherAssert.assertThat(
            "passes with no exceptions",
            new Program(new XMLDocument("<program name='it'/>")).defects(),
            Matchers.notNullValue()
        );
    }
}
