/*
 * Copyright © 2015 The Gravitee team (http://gravitee.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.gravitee.common.templating;

import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.FileTemplateLoader;
import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.core.TemplateClassResolver;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Antoine CORDIER (antoine.cordier at graviteesource.com)
 * @author GraviteeSource Team
 *
 * Utility component that compiles freemarker templates from various source to various outputs.
 *
 * If looking for templates in the classPath, it is the responsibility of the caller to provide a classLoader.
 */
@Slf4j
public class FreeMarkerComponent {

    private final Configuration freeMarkerConfiguration;

    /**
     * Creates a new FreeMarkerComponent.
     * @param path path to a template directory on the File System
     * @param classLoader class loader to load templates from the class path
     * @param classLoaderTemplateBase base path to load templates from the class path
     *
     * If path is defined (not null), templates will be looked for on the file system first, then in the class path if
     * a class loader has been provided.
     *
     * If classLoaderTemplateBase is not defined (null), templates will be looked for at the root of the class path.
     *
     * Regarding template path resolution comments in the javadoc of {@link ClassTemplateLoader} state that
     *
     * """
     * Because a ClassLoader isn't bound to any Java package,
     * it doesn't matter if the basePackagePath starts with / or not,
     * it will be always relative to the root of the package hierarchy.
     * """
     *
     */
    @Builder
    public FreeMarkerComponent(Path path, ClassLoader classLoader, String classLoaderTemplateBase) {
        this.freeMarkerConfiguration = freemarkerConfiguration(path, classLoader, classLoaderTemplateBase);
    }

    /**
     * Compiles the free marker template and writes the result to the writer.
     * @param templatePath name of the FreeMarker template
     * @param data data of the template
     * @param writer writer to write the result to
     */
    public void generateFromTemplate(final String templatePath, final Map<String, Object> data, Writer writer) {
        try {
            log.debug("Generating from template {}", templatePath);
            final Template template = freeMarkerConfiguration.getTemplate(templatePath);
            template.process(data, writer);
        } catch (final IOException | TemplateException exception) {
            throw new IllegalArgumentException("Impossible to generate from template " + templatePath, exception);
        }
    }

    /**
     * Compiles the free marker template and returns the result as a string.
     * @param templatePath name of the FreeMarker template
     * @param data data of the template
     * @return compiled template as a string
     */
    public String generateFromTemplate(final String templatePath, final Map<String, Object> data) {
        try (final StringWriter output = new StringWriter()) {
            generateFromTemplate(templatePath, data, output);
            return output.getBuffer().toString();
        } catch (final IOException exception) {
            throw new IllegalArgumentException("Impossible to generate from template " + templatePath, exception);
        }
    }

    private static Configuration freemarkerConfiguration(Path path, ClassLoader classLoader, String classLoaderTemplateBase) {
        var configuration = new Configuration(Configuration.VERSION_2_3_23);
        configuration.setDefaultEncoding(StandardCharsets.UTF_8.name());
        configuration.setDateFormat("iso_utc");
        configuration.setLocale(Locale.ENGLISH);
        configuration.setNumberFormat("computer");
        configuration.setNewBuiltinClassResolver(TemplateClassResolver.SAFER_RESOLVER);
        configuration.setTemplateLoader(templateLoader(path, classLoader, classLoaderTemplateBase));
        return configuration;
    }

    private static TemplateLoader templateLoader(Path path, ClassLoader classLoader, String classLoaderTemplateBase) {
        try {
            var templateLoaders = new ArrayList<TemplateLoader>();
            if (path != null) {
                log.debug("Adding file template loader for path {}", path);
                templateLoaders.add(new FileTemplateLoader(path.toFile()));
            }
            if (classLoader != null) {
                log.debug("Adding classloader {} to freemarker template loaders", classLoader);
                if (classLoaderTemplateBase == null) {
                    log.debug("Classpath templates will be looked for in the root package");
                    templateLoaders.add(new ClassTemplateLoader(classLoader, "/"));
                } else {
                    log.debug("Classpath templates will be looked for in {}", classLoaderTemplateBase);
                    templateLoaders.add(new ClassTemplateLoader(classLoader, classLoaderTemplateBase));
                }
            }
            return new MultiTemplateLoader(templateLoaders.toArray(new TemplateLoader[] {}));
        } catch (IOException e) {
            throw new IllegalArgumentException("Unable to initialize freemarker template loader", e);
        }
    }
}
