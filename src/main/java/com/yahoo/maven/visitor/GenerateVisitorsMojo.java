// Copyright 2016 Yahoo Inc.
// Licensed under the terms of the Apache 2.0 license. Please see LICENSE file in the project root for terms.
package com.yahoo.maven.visitor;

import com.google.inject.Guice;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.IOException;

@Mojo(name = "generate-visitors", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class GenerateVisitorsMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project.build.directory}/generated-sources/visitors", property = "outputDirectory")
    private File outputDirectory;

//    @Parameter(defaultValue = "${project.build.sourceEncoding}", property = "encoding")
//    private String encoding;

    @Parameter(property = "skip", defaultValue = "false")
    private boolean skip;

    @Parameter(property = "visitors", required = true)
    private Visitor[] visitors;

    @Component
    private MavenProject mavenProject;

    // on MojoExecutionException vs MojoFailureException:
    // https://books.sonatype.com/mvnref-book/reference/writing-plugins-sect-custom-plugin.html#writing-plugins-sect-failure

    @Override
    public void execute() throws
            MojoExecutionException /* fatal error, always stops the build */,
            MojoFailureException /* other build phases (including other modules in a multi-module maven project) might still run */ {
        if (skip) return;
        VisitorGeneratorFactory factory = Guice.createInjector(new CommonModule()).getInstance(VisitorGeneratorFactory.class);
        VisitorGenerator generator = factory.newVisitorGenerator(visitors, outputDirectory.toPath());
        try {
            generator.generate();
        } catch (IOException e) {
            throw new MojoExecutionException("Error generating visitor classes", e);
        }
        mavenProject.addCompileSourceRoot(outputDirectory.getAbsolutePath());
    }

}
