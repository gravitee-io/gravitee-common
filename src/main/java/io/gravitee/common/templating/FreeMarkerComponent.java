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
import java.util.Locale;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Antoine CORDIER (antoine.cordier at graviteesource.com)
 * @author GraviteeSource Team
 *
 * Utility component that compiles freemarker templates from various source to various outputs.
 *
 * This can be used in plugins as it handles template loading from the parent classloader.
 *
 * To be resolved from the class path, templates *must* reside in the `freemarker` resources package.
 */
public class FreeMarkerComponent {

    private static final Logger LOG = LoggerFactory.getLogger(FreeMarkerComponent.class);

    private static final String TEMPLATES_BASE = "/freemarker";

    private final Configuration freeMarkerConfiguration;

    /**
     * Create a FreeMarker component with default configuration, meaning that templates are loaded from
     * a freemarker resources package located in the classpath, using the current classloader and its parent.
     * The parent classloader takes precedence over the current classloader.
     * {@see #freemarker.cache.ClassTemplateLoader(Class, String)}
     */
    public FreeMarkerComponent() {
        this("");
    }

    /**
     * Convenience constructor to create a FreeMarker component which will load templates from a subpackage
     * inside the freemarker resources package.
     * {@link FreeMarkerComponent#FreeMarkerComponent()}
     */
    public FreeMarkerComponent(String subPackagePath) {
        var basePackagePath = sanitizeBasePackage(subPackagePath);
        this.freeMarkerConfiguration = freemarkerConfiguration(basePackagePath);
    }

    /**
     * Creates a FreeMarker component which will look for templates from a given path on the file system first,
     * and fallback to the classpath loader.
     */
    public FreeMarkerComponent(Path path) {
        this(path, "");
    }

    /**
     * Creates a FreeMarker component which will look for templates from a given path on the file system first,
     * and fallback to the freemarker resources subpackage.
     * {@link FreeMarkerComponent#FreeMarkerComponent(String)}
     * {@link FreeMarkerComponent#FreeMarkerComponent(Path)}
     */
    public FreeMarkerComponent(Path path, String subPackagePath) {
        var basePackagePath = sanitizeBasePackage(subPackagePath);
        this.freeMarkerConfiguration = freemarkerConfiguration(path, basePackagePath);
    }

    /**
     * Compiles the free marker template and writes the result to the writer.
     * @param templateName name of the FreeMarker template
     * @param data data of the template
     * @param writer writer to write the result to
     */
    public void generateFromTemplate(final String templateName, final Map<String, Object> data, Writer writer) {
        try {
            final Template template = freeMarkerConfiguration.getTemplate(templateName);
            template.process(data, writer);
        } catch (final IOException | TemplateException exception) {
            LOG.error("Impossible to generate from template " + templateName, exception);
            throw new IllegalArgumentException();
        }
    }

    /**
     * Compiles the free marker template and returns the result as a string.
     * @param templateName name of the FreeMarker template
     * @param data data of the template
     * @return compiled template as a string
     */
    public String generateFromTemplate(final String templateName, final Map<String, Object> data) {
        try (final StringWriter output = new StringWriter()) {
            generateFromTemplate(templateName, data, output);
            return output.getBuffer().toString();
        } catch (final IOException exception) {
            LOG.error("Impossible to generate from template {}", templateName, exception);
            throw new IllegalArgumentException();
        }
    }

    private static String sanitizeBasePackage(String subPackagePath) {
        return subPackagePath.startsWith("/") ? TEMPLATES_BASE + subPackagePath : TEMPLATES_BASE + "/" + subPackagePath;
    }

    private static Configuration freemarkerConfiguration(String templatesLocation) {
        var configuration = commonFreemarkerConfiguration();
        configuration.setTemplateLoader(classTemplateLoader(templatesLocation));
        return configuration;
    }

    private static Configuration freemarkerConfiguration(Path path, String basePackagePath) {
        var configuration = commonFreemarkerConfiguration();
        configuration.setTemplateLoader(fileTemplateLoader(path, basePackagePath));
        return configuration;
    }

    private static Configuration commonFreemarkerConfiguration() {
        var configuration = new Configuration(Configuration.VERSION_2_3_23);
        configuration.setDefaultEncoding(StandardCharsets.UTF_8.name());
        configuration.setDateFormat("iso_utc");
        configuration.setLocale(Locale.ENGLISH);
        configuration.setNumberFormat("computer");
        configuration.setNewBuiltinClassResolver(TemplateClassResolver.SAFER_RESOLVER);
        return configuration;
    }

    private static TemplateLoader fileTemplateLoader(Path path, String templateLocation) {
        try {
            var fileTemplateLoader = new FileTemplateLoader(path.toFile());
            var classTemplateLoader = classTemplateLoader(templateLocation);
            return new MultiTemplateLoader(new TemplateLoader[] { fileTemplateLoader, classTemplateLoader });
        } catch (IOException e) {
            throw new IllegalArgumentException("Unable to initialize template loader", e);
        }
    }

    private static TemplateLoader classTemplateLoader(String templatesLocation) {
        var classLoader = FreeMarkerComponent.class.getClassLoader();
        var templateLoader = new ClassTemplateLoader(classLoader, templatesLocation);
        if (classLoader.getParent() != null) {
            var parentTemplateLoader = new ClassTemplateLoader(classLoader.getParent(), templatesLocation);
            return new MultiTemplateLoader(new TemplateLoader[] { parentTemplateLoader, templateLoader });
        }
        return templateLoader;
    }
}
