/*
 * Copyright (C) 2007-2010 Júlio Vilmar Gesser.
 * Copyright (C) 2011, 2013-2024 The JavaParser Team.
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

package com.github.javaparser.ast.validator;

import static com.github.javaparser.ParseStart.CLASS_BODY;
import static com.github.javaparser.ParseStart.STATEMENT;
import static com.github.javaparser.ParserConfiguration.LanguageLevel.JAVA_10;
import static com.github.javaparser.Providers.provider;
import static com.github.javaparser.utils.TestUtils.assertNoProblems;
import static com.github.javaparser.utils.TestUtils.assertProblems;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.stmt.Statement;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class Java10ValidatorTest {
    public static final JavaParser javaParser = new JavaParser(new ParserConfiguration().setLanguageLevel(JAVA_10));

    @Test
    void varAllowedInLocalVariableDeclaration() {
        ParseResult<Statement> result = javaParser.parse(STATEMENT, provider("var a = 5;"));
        assertNoProblems(result);
    }

    @Test
    void varAllowedInForEach() {
        ParseResult<Statement> result = javaParser.parse(STATEMENT, provider("for(var a : as){}"));
        assertNoProblems(result);
    }

    @Test
    void varAllowedInOldFor() {
        ParseResult<Statement> result = javaParser.parse(STATEMENT, provider("for(var a = 5;a<9;a++){}"));
        assertNoProblems(result);
    }

    @Test
    void varAllowedInTryWithResources() {
        ParseResult<Statement> result =
                javaParser.parse(STATEMENT, provider("try(var f = new FileReader(\"\")){ }catch (Exception e){ }"));
        assertNoProblems(result);
    }

    @Test
    void varNotAllowedInCast() {
        ParseResult<Statement> result = javaParser.parse(STATEMENT, provider("int a = (var)20;"));
        assertNoProblems(result);
    }

    @Test
    void varNotAllowedInField() {
        ParseResult<BodyDeclaration<?>> result = javaParser.parse(CLASS_BODY, provider("var a = 20;"));
        assertProblems(result, "(line 1,col 1) \"var\" is not allowed here.");
    }

    @Test
    void varNotAllowedInTypeArguments() {
        ParseResult<Statement> result = javaParser.parse(STATEMENT, provider("new X<var>();"));
        assertProblems(result, "(line 1,col 7) \"var\" is not allowed here.");
    }

    @Test
    void varNotAllowedInLambdaParameters() {
        ParseResult<Statement> result = javaParser.parse(STATEMENT, provider("x((var x) -> null);"));
        assertProblems(result, "(line 1,col 4) \"var\" is not allowed here.");
    }

    @Test
    void emptyInitializerNotAllowed() {
        ParseResult<Statement> result = javaParser.parse(STATEMENT, provider("var a;"));
        assertProblems(result, "(line 1,col 1) \"var\" needs an initializer.");
    }

    @Test
    void multipleVariablesNotAllowed() {
        ParseResult<Statement> result = javaParser.parse(STATEMENT, provider("var a=1, b=2;"));
        assertProblems(result, "(line 1,col 1) \"var\" only takes a single variable.");
    }

    @Test
    void nullVariablesNotAllowed() {
        ParseResult<Statement> result = javaParser.parse(STATEMENT, provider("var a=null;"));
        assertProblems(result, "(line 1,col 1) \"var\" cannot infer type from just null.");
    }

    @Test
    void extraBracketPairsNotAllowed() {
        ParseResult<Statement> result = javaParser.parse(STATEMENT, provider("var d[] = new int[4];"));
        assertProblems(result, "(line 1,col 5) \"var\" cannot have extra array brackets.");
    }

    @Test
    void arrayDimensionBracketsNotAllowed() {
        ParseResult<Statement> result = javaParser.parse(STATEMENT, provider("var a={ 6 };"));
        assertProblems(result, "(line 1,col 1) \"var\" cannot infer array types.");
    }

    // This is pretty hard to impossible to implement correctly with just the AST.
    @Disabled
    @Test
    void selfReferenceNotAllowed() {
        ParseResult<Statement> result = javaParser.parse(STATEMENT, provider("var a=a;"));
        assertProblems(result, "");
    }

    // Can be implemented once https://github.com/javaparser/javaparser/issues/1434 is implemented.
    @Disabled
    @Test
    void polyExpressionAsInitializerNotAllowed() {
        ParseResult<Statement> result = javaParser.parse(STATEMENT, provider("var a=new ArrayList<>();"));
        assertProblems(result, "");
    }
}
