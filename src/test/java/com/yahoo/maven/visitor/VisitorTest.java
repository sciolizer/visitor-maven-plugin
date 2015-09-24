// Copyright 2016 Yahoo Inc.
// Licensed under the terms of the Apache 2.0 license. Please see LICENSE file in the project root for terms.
package com.yahoo.maven.visitor;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.util.Modules;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestName;

import javax.inject.Inject;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class VisitorTest {

    @Rule public TestName name = new TestName();

    @Inject @ScratchSpace protected Path scratchSpace;

    protected Injector injector;

    @Before
    public void setUp() throws Exception {
        injector = Guice.createInjector(Modules.override(Modules.combine(new TestModule(), new AbstractModule() {
            @Override
            protected void configure() {
                bind(Path.class).annotatedWith(ScratchSpace.class).toInstance(Paths.get("target", "visitor", name.getMethodName()));
            }
        })).with(getOverrides()));
        injector.injectMembers(this);
        Files.createDirectories(scratchSpace);
        FileUtils.cleanDirectory(scratchSpace.toFile());
    }

    protected Module getOverrides() {
        return Modules.EMPTY_MODULE;
    }

}
