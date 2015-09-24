// Copyright 2016 Yahoo Inc.
// Licensed under the terms of the Apache 2.0 license. Please see LICENSE file in the project root for terms.
package com.yahoo.maven.visitor;

public class TypeParameter implements TypeParameterContext {

    private String name;
    private String constraint;

    public TypeParameter() {
    }

    public TypeParameter(String name, String constraint) {
        this.name = name;
        this.constraint = constraint;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getConstraint() {
        return constraint;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setConstraint(String constraint) {
        this.constraint = constraint;
    }

}
