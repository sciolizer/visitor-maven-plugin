// Copyright 2016 Yahoo Inc.
// Licensed under the terms of the Apache 2.0 license. Please see LICENSE file in the project root for terms.
package com.yahoo.maven.visitor;

public interface ParameterContext {

    String getType();

    String getCovariantType();

    String getWildcardType();

    String getName();

    String getHashExpression();

    String getParametricTypeBound();

}