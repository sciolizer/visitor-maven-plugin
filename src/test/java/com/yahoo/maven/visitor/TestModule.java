// Copyright 2016 Yahoo Inc.
// Licensed under the terms of the Apache 2.0 license. Please see LICENSE file in the project root for terms.
package com.yahoo.maven.visitor;

import com.google.inject.AbstractModule;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugin.logging.SystemStreamLog;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class TestModule extends AbstractModule {
    @Override
    protected void configure() {
        install(new CommonModule());
        bind(Log.class).toInstance(new SystemStreamLog());
        bind(Charset.class).toInstance(StandardCharsets.UTF_8);
    }
}
