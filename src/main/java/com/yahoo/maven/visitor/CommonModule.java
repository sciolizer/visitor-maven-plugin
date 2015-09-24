// Copyright 2016 Yahoo Inc.
// Licensed under the terms of the Apache 2.0 license. Please see LICENSE file in the project root for terms.
package com.yahoo.maven.visitor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.*;
import com.google.inject.matcher.Matchers;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.app.event.implement.IncludeRelativePath;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.apache.velocity.tools.generic.DisplayTool;

import javax.inject.Singleton;
import java.io.Writer;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

public class CommonModule extends AbstractModule {

    @Override
    protected void configure() {
        VelocityEngine velocityEngine = new VelocityEngine();
        velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
        velocityEngine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
        velocityEngine.setProperty(RuntimeConstants.EVENTHANDLER_INCLUDE, IncludeRelativePath.class.getName());
        velocityEngine.setProperty(RuntimeConstants.VM_LIBRARY, "visitor-maven-plugin/templates/VM_global_library.vm");
        velocityEngine.init();
        bind(VelocityEngine.class).toInstance(velocityEngine);
        bind(ObjectMapper.class).toInstance(new ObjectMapper());
        bindListener(Matchers.any(), new TypeListener() {
            @Override
            public <I> void hear(TypeLiteral<I> type, TypeEncounter<I> encounter) {
                Map<Field,String> fields = new LinkedHashMap<>();
                Class<? super I> clazz = type.getRawType();
                while (clazz != null) {
                    for (Field field : clazz.getDeclaredFields()) {
                        if (field.isAnnotationPresent(InjectTemplate.class)) {
                            if (!field.getType().isAssignableFrom(Template.class) && !field.getType().isAssignableFrom(TemplateMerger.class)) {
                                throw new RuntimeException("Field annotated with @InjectTemplate should be of type Template or TemplateMerger: " + field);
                            }
                            field.setAccessible(true);
                            fields.put(field, field.getAnnotation(InjectTemplate.class).value());
                        }
                    }
                    clazz = clazz.getSuperclass();
                }
                if (!fields.isEmpty()) {
                    TemplateInjector<I> templateInjector = new TemplateInjector<>(encounter.getProvider(VelocityEngine.class), fields);
                    encounter.register(templateInjector);
                }
            }
        });
    }

    protected static class TemplateInjector<T> implements MembersInjector<T> {
        protected Provider<VelocityEngine> velocityEngineProvider;
        protected Map<Field,String> fields = new LinkedHashMap<>();

        public TemplateInjector(Provider<VelocityEngine> velocityEngineProvider, Map<Field, String> fields) {
            this.velocityEngineProvider = velocityEngineProvider;
            this.fields = fields;
        }

        @Override
        public void injectMembers(T instance) {
            // todo: cache the templates
            for (Map.Entry<Field, String> entry : fields.entrySet()) {
                try {
                    Template template = velocityEngineProvider.get().getTemplate("visitor-maven-plugin/templates/" + entry.getValue() + ".vm", "UTF-8");

                    Field field = entry.getKey();
                    if (Template.class.isAssignableFrom(field.getType())) {
                        field.set(instance, template);
                    } else if (TemplateMerger.class.isAssignableFrom(field.getType())) {
                        field.set(instance, new TemplateMerger() {
                            @Override
                            public void merge(Writer writer, Object context) {
                                VelocityContext c = new VelocityContext();
                                c.put("display", new DisplayTool());
                                c.put("c", context);
                                template.merge(c, writer);
                            }
                        });
                    }
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    @Provides
    @Singleton
    public VisitorGeneratorFactory provideVisitorGeneratorFactory(Injector i) {
        return new VisitorGeneratorFactory() {
            @Override
            public VisitorGenerator newVisitorGenerator(Visitor[] visitors, Path outputDirectory) {
                VisitorGenerator visitorGenerator = new VisitorGenerator() {
                    @Override
                    protected Visitor[] getVisitors() {
                        return visitors;
                    }

                    @Override
                    protected Path getOutputDirectory() {
                        return outputDirectory;
                    }
                };
                i.injectMembers(visitorGenerator);
                return visitorGenerator;
            }
        };
    }

}
