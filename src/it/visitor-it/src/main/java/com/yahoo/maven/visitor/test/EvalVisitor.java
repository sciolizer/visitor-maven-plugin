// Copyright 2016 Yahoo Inc.
// Licensed under the terms of the Apache 2.0 license. Please see LICENSE file in the project root for terms.
package com.yahoo.maven.visitor.test;

public class EvalVisitor implements ExpressionVisitor<Void, RuntimeException, String> {

    @Override
    public String literalString(String string) throws RuntimeException {
        return string;
    }

    @Override
    public String concatenation(Expression<? extends Void> left, Expression<? extends Void> right) throws RuntimeException {
        return left.accept(this) + right.accept(this);
    }

    @Override
    public String other(Void aVoid) throws RuntimeException {
        throw new UnsupportedOperationException();
    }

}
