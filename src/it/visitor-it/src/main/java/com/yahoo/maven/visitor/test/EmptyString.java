// Copyright 2016 Yahoo Inc.
// Licensed under the terms of the Apache 2.0 license. Please see LICENSE file in the project root for terms.
package com.yahoo.maven.visitor.test;

public class EmptyString<Other> implements Expression<Other> {

    @Override
    public <E extends Exception, T> T accept(ExpressionVisitor<? super Other, E, T> visitor) throws E {
        return visitor.literalString("");
    }

}
