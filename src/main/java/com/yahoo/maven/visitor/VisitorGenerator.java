// Copyright 2016 Yahoo Inc.
// Licensed under the terms of the Apache 2.0 license. Please see LICENSE file in the project root for terms.
package com.yahoo.maven.visitor;

import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class VisitorGenerator {

    @Inject Capitalizer capitalizer;

    @InjectTemplate("visited") protected TemplateMerger visitedTemplate;
    @InjectTemplate("visitor") protected TemplateMerger visitorTemplate;
    @InjectTemplate("defaultingVisitor") protected TemplateMerger defaultingVisitorTemplate;
    @InjectTemplate("identityVisitor") protected TemplateMerger identityVisitorTemplate;
    @InjectTemplate("defaultingIdentityVisitor") protected TemplateMerger defaultingIdentityVisitorTemplate;

    protected abstract Visitor[] getVisitors();
    protected abstract Path getOutputDirectory();

    public void generate() throws IOException {
        for (Visitor visitor : getVisitors()) {
            String name = visitor.getVisitedName();
            int packageTail = name.lastIndexOf(".");
            String aPackage = packageTail == -1 ? null : name.substring(0, packageTail);
            String disjunctName = packageTail == -1 ? name : name.substring(packageTail + 1, name.length());
            String className = capitalizer.capitalize(disjunctName);
            String dir = aPackage == null ? "" : StringUtils.replace(aPackage, ".", FileSystems.getDefault().getSeparator());
            List<TypeParameterContext> typeParameterContexts = new ArrayList<>();
            Set<String> typeParameterNames = new LinkedHashSet<>();
            if (visitor.getTypeParameters() != null) {
                typeParameterContexts.addAll(Arrays.asList(visitor.getTypeParameters()));
                for (TypeParameter typeParameter : visitor.getTypeParameters()) {
                    typeParameterNames.add(typeParameter.getName());
                }
            }
            List<DisjunctContext> disjuncts = new ArrayList<>();
            for (Disjunction disjunction : visitor.getDisjunctions()) {
                List<ParameterContext> parameterContexts = new ArrayList<>();
                if (disjunction.parameters != null) {
                    for (Parameter parameter : disjunction.parameters) {
                        parameterContexts.add(new ParameterContext() {
                            @Override
                            public String getType() {
                                return parameter.type;
                            }

                            @Override
                            public String getName() {
                                return parameter.name;
                            }

                            @Override
                            public String getHashExpression() {
                                switch (getType()) {
                                    case "boolean":
                                        return "(" + getName() + " ? 1 : 0)";
                                    case "char":
                                    case "byte":
                                    case "short":
                                        return "(int) " + getName();
                                    case "int":
                                        return getName();
                                    case "long":
                                        return "(int) (" + getName() + " ^ (" + getName() + " >>> 32))";
                                    case "float":
                                        return "(" + getName() + " != +0.0f ? Float.floatToIntBits(" + getName() + ") : 0)";
                                    case "double":
                                        return "(int) (Double.doubleToLongBits(" + getName() + ") ^ (Double.doubleToLongBits(" + getName() + ") >>> 32))";
                                    default:
                                        return "(" + getName() + " != null ? " + getName() + ".hashCode() : 0)";
                                }
                            }
                        });
                    }
                }
                disjuncts.add(new DisjunctContext() {
                    @Override
                    public String getMethodName() {
                        return disjunction.name;
                    }

                    @Override
                    public List<ParameterContext> getParameters() {
                        return parameterContexts.isEmpty() ? null : parameterContexts;
                    }

                    @Override
                    public boolean hasAsMethod() {
                        return parameterContexts.size() == 1 && !primitives.contains(parameterContexts.get(0).getType());
                    }
                });
            }
            class AbstractBaseContext implements BaseContext {
                @Override
                public String getPackage() {
                    return aPackage;
                }

                @Override
                public String getVisitedClassName() {
                    return className;
                }

                @Override
                public List<TypeParameterContext> getTypeParameters() {
                    return typeParameterContexts.isEmpty() ? null : typeParameterContexts;
                }

                @Override
                public List<DisjunctContext> getDisjuncts() {
                    return disjuncts.isEmpty() ? null : disjuncts;
                }
            }
            Path outputFolder = getOutputDirectory().resolve(dir);
            File outputFolderAsFile = outputFolder.toFile();
            if (!outputFolderAsFile.mkdirs() && !outputFolderAsFile.exists()) {
                throw new IOException("Unable to make directory: " + outputFolderAsFile.getAbsolutePath());
            }
            try (Writer visitedWriter = Files.newBufferedWriter(outputFolder.resolve(className + ".java"))) {
                class ThisVisitedContext extends AbstractBaseContext implements VisitedContext {}
                visitedTemplate.merge(visitedWriter, new ThisVisitedContext());
            }
            try (Writer visitorWriter = Files.newBufferedWriter(outputFolder.resolve(className + "Visitor.java"))) {
                class ThisVisitorContext extends AbstractBaseContext implements VisitorContext {}
                visitorTemplate.merge(visitorWriter, new ThisVisitorContext());
            }
            try (Writer visitorWriter = Files.newBufferedWriter(outputFolder.resolve("Defaulting" + className + "Visitor.java"))) {
                class ThisDefaultingVisitorContext extends AbstractBaseContext implements DefaultingVisitorContext {}
                defaultingVisitorTemplate.merge(visitorWriter, new ThisDefaultingVisitorContext());
            }
            try (Writer visitorWriter = Files.newBufferedWriter(outputFolder.resolve("Identity" + className + "Visitor.java"))) {
                class ThisIdentityVisitorContext extends AbstractBaseContext implements IdentityVisitorContext {}
                identityVisitorTemplate.merge(visitorWriter, new ThisIdentityVisitorContext());
            }
            try (Writer visitorWriter = Files.newBufferedWriter(outputFolder.resolve("DefaultingIdentity" + className + "Visitor.java"))) {
                class ThisIdentityVisitorContext extends AbstractBaseContext implements IdentityVisitorContext, DefaultingVisitorContext {}
                defaultingIdentityVisitorTemplate.merge(visitorWriter, new ThisIdentityVisitorContext());
            }
        }
    }

    private static final Set<String> primitives = new LinkedHashSet<>(Arrays.asList("char", "byte", "short", "int", "long", "float", "double"));

}
