/*
 * Copyright (C) 2013-2024 The JavaParser Team.
 *
 * This file is part of JavaParser.
 *
 * JavaParser can be used either under the terms of
 * a) the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * b) the terms of the Apache License
 *
 * You should have received a copy of both licenses in LICENCE.LGPL and
 * LICENCE.APACHE. Please refer to those files for details.
 *
 * JavaParser is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 */

package com.github.javaparser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.javaparser.ast.expr.LambdaExpr;
import com.github.javaparser.ast.stmt.Statement;
import org.junit.jupiter.api.Test;

/**
 * Tests related to https://github.com/javaparser/javaparser/issues/2482.
 */
public class Issue2482Test {
    @Test
    public void commentBeforeLambda() {
        LambdaExpr le = StaticJavaParser.parseExpression(
                "// a comment before parent" + System.lineSeparator() + "()->{return 1;}");

        assertTrue(le.getComment().isPresent());
        assertTrue(le.getOrphanComments().isEmpty());
        assertEquals(0, le.getAllContainedComments().size());
    }

    @Test
    public void commentBeforeBlock() {
        Statement st = StaticJavaParser.parseBlock(
                "// a comment before parent" + System.lineSeparator() + "{ if (file != null) {} }");
        assertTrue(st.getComment().isPresent());
        assertTrue(st.getOrphanComments().isEmpty());
        assertEquals(0, st.getAllContainedComments().size());
    }

    @Test
    public void commentBeforeIfStatement() {
        Statement st = StaticJavaParser.parseStatement(
                "// a comment before parent" + System.lineSeparator() + "if (file != null) {}");
        assertTrue(st.getComment().isPresent());
        assertTrue(st.getOrphanComments().isEmpty());
        assertEquals(0, st.getAllContainedComments().size());
    }

    @Test
    public void commentBeforeAssignment() {
        Statement st = StaticJavaParser.parseStatement("// a comment" + System.lineSeparator() + "int x = 3;");

        assertTrue(st.getComment().isPresent());
        assertTrue(st.getOrphanComments().isEmpty());
        assertEquals(0, st.getAllContainedComments().size());
    }
}
