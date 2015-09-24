// Copyright 2016 Yahoo Inc.
// Licensed under the terms of the Apache 2.0 license. Please see LICENSE file in the project root for terms.
package com.yahoo.maven.visitor;

import org.apache.commons.io.FilenameUtils;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.tools.*;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;

@Singleton
public class SuccessfulCompilationAsserter {

    @Inject @ScratchSpace protected Path scratchSpace;

    public void assertNoCompilationErrors() throws IOException {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        try (StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, StandardCharsets.UTF_8)) {
            List<File> javaFiles = new ArrayList<>();
            try (Stream<Path> stream = Files.walk(scratchSpace)) {
                Iterator<Path> iterator = stream.iterator();
                while (iterator.hasNext()) {
                    Path path = iterator.next();
                    if (Files.isRegularFile(path) && "java".equals(FilenameUtils.getExtension(path.toString()))) {
                        javaFiles.add(path.toFile());
                    }
                }
            }
            Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjectsFromFiles(javaFiles);
            compiler.getTask(null, fileManager, diagnostics, Arrays.asList("-classpath", System.getProperty("java.class.path")), null, compilationUnits).call();
        }
        int errors = 0;
        for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
            if (Diagnostic.Kind.ERROR.equals(diagnostic.getKind())) {
                System.err.println(diagnostic);
                errors++;
            }
        }
        assertEquals(0, errors);
    }

}
