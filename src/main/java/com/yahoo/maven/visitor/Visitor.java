// Copyright 2016 Yahoo Inc.
// Licensed under the terms of the Apache 2.0 license. Please see LICENSE file in the project root for terms.
package com.yahoo.maven.visitor;

public class Visitor {

    private String visitedName;
    private TypeParameter[] typeParameters;
    private Disjunction[] disjunctions;

    public Visitor() {
    }

    public Visitor(String visitedName, TypeParameter[] typeParameters, Disjunction[] disjunctions) {
        this.visitedName = visitedName;
        this.typeParameters = typeParameters;
        this.disjunctions = disjunctions;
    }

    public String getVisitedName() {
        return visitedName;
    }

    public TypeParameter[] getTypeParameters() {
        return typeParameters;
    }

    public Disjunction[] getDisjunctions() {
        return disjunctions;
    }
}
