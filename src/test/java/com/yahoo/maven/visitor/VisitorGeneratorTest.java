// Copyright 2016 Yahoo Inc.
// Licensed under the terms of the Apache 2.0 license. Please see LICENSE file in the project root for terms.
package com.yahoo.maven.visitor;

import org.junit.Test;

import javax.inject.Inject;

public class VisitorGeneratorTest extends VisitorTest {

    @Inject
    SuccessfulCompilationAsserter successfulCompilationAsserter;
    @Inject
    VisitorGeneratorFactory visitorGeneratorFactory;

    @Test
    public void testGenerate() throws Exception {
        VisitorGenerator generator = visitorGeneratorFactory.newVisitorGenerator(new Visitor[]{
                        new Visitor(
                                "Either",
                                new TypeParameter[]{new TypeParameter("L", null), new TypeParameter("R", null)},
                                new Disjunction[]{
                                        new Disjunction("left", new Parameter[]{new Parameter("L", "left")}),
                                        new Disjunction("right", new Parameter[]{new Parameter("R", "right")})}),
                        new Visitor(
                                "Maybe",
                                new TypeParameter[]{new TypeParameter("A", null)},
                                new Disjunction[]{
                                        new Disjunction("nothing", null),
                                        new Disjunction("just", new Parameter[]{new Parameter("A", "value")})}),
                        new Visitor(
                                "test.Expression",
                                null,
                                new Disjunction[]{
                                        new Disjunction("variable", new Parameter[]{new Parameter("String", "name")}),
                                        new Disjunction("application", new Parameter[]{new Parameter("test.Expression", "applier"), new Parameter("test.Expression", "applicand")}),
                                        new Disjunction("lambda", new Parameter[]{new Parameter("String", "identifier"), new Parameter("test.Expression", "body")})}),
                        new Visitor(
                                "test.ExtendableExpression",
                                new TypeParameter[] { new TypeParameter("Other", null) },
                                new Disjunction[]{
                                        new Disjunction("variable", new Parameter[]{new Parameter("String", "name")}),
                                        new Disjunction("application", new Parameter[]{new Parameter("test.ExtendableExpression<Other>", "applier"), new Parameter("test.ExtendableExpression<Other>", "applicand")}),
                                        new Disjunction("lambda", new Parameter[]{new Parameter("String", "identifier"), new Parameter("test.ExtendableExpression<Other>", "body")})}),
                        new Visitor(
                                "SignedNumber",
                                new TypeParameter[]{new TypeParameter("N", "Number")},
                                new Disjunction[]{
                                        new Disjunction("positive", new Parameter[]{new Parameter("N", "number")}),
                                        new Disjunction("zero", new Parameter[0]),
                                        new Disjunction("negative", new Parameter[]{new Parameter("N", "number")})}),
                        new Visitor("Unit", null, new Disjunction[]{new Disjunction("unit", null)}),
                        new Visitor("ListWrapper", null, new Disjunction[]{new Disjunction("list", new Parameter[]{new Parameter("java.util.List<String>", "strings")})}),
                        new Visitor("PrimitiveAsMethod", null, new Disjunction[]{new Disjunction("num", new Parameter[]{new Parameter("int", "num")})}),
                        new Visitor("Primitives", null, new Disjunction[]{new Disjunction("primitives", new Parameter[]{
                                new Parameter("boolean", "aBoolean"),
                                new Parameter("char", "aChar"),
                                new Parameter("byte", "aByte"),
                                new Parameter("short", "aShort"),
                                new Parameter("int", "anInt"),
                                new Parameter("long", "aLong"),
                                new Parameter("float", "aFloat"),
                                new Parameter("double", "aDouble")})})},
                scratchSpace);
        generator.generate();
        successfulCompilationAsserter.assertNoCompilationErrors();
    }

}
