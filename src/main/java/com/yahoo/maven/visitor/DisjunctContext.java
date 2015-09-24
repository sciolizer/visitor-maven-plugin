// Copyright 2016 Yahoo Inc.
// Licensed under the terms of the Apache 2.0 license. Please see LICENSE file in the project root for terms.
package com.yahoo.maven.visitor;

import java.util.List;

public interface DisjunctContext {

    String getMethodName();

    List<ParameterContext> getParameters();

    boolean hasAsMethod();

}