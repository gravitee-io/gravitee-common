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

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class FreeMarkerComponentTest {

    private static final Map<String, Object> DATA = Map.of("message", "It works!");

    private static final String TEMPLATE = "template.ftl";

    @Test
    void should_resolve_template_from_class_template_loader_base() {
        var freeMarkerComponent = FreeMarkerComponent
            .builder()
            .classLoader(getClass().getClassLoader())
            .classLoaderTemplateBase("freemarker/")
            .build();

        var result = freeMarkerComponent.generateFromTemplate(TEMPLATE, DATA);
        assertThat(result)
            .isEqualToIgnoringNewLines("""
      { "description" : "from freemarker package", "message" : "It works!" }
      """);
    }

    @Test
    void should_resolve_template_with_no_class_template_loader_base() {
        var freeMarkerComponent = FreeMarkerComponent.builder().classLoader(getClass().getClassLoader()).build();

        var template = "/freemarker/" + TEMPLATE;
        var result = freeMarkerComponent.generateFromTemplate(template, DATA);
        assertThat(result)
            .isEqualToIgnoringNewLines("""
      { "description" : "from freemarker package", "message" : "It works!" }
      """);
    }

    @Test
    void should_resolve_templates_from_file_system(@TempDir Path tempDir) throws IOException {
        var freeMarkerComponent = FreeMarkerComponent.builder().path(tempDir).build();

        var fsTemplate = tempDir.resolve(TEMPLATE);

        Files.write(fsTemplate, """
    { "description" : "from file system", "message" : "${message}" }
    """.getBytes());

        var result = freeMarkerComponent.generateFromTemplate(TEMPLATE, DATA);
        assertThat(result).isEqualToIgnoringNewLines("""
      { "description" : "from file system", "message" : "It works!" }
      """);
    }

    @Test
    void should_resolve_templates_from_file_system_and_class_path(@TempDir Path tempDir) throws IOException {
        var freeMarkerComponent = FreeMarkerComponent
            .builder()
            .path(tempDir)
            .classLoader(getClass().getClassLoader())
            .classLoaderTemplateBase("freemarker")
            .build();

        var fsTemplate = tempDir.resolve(TEMPLATE);

        Files.write(fsTemplate, """
    { "description" : "from file system", "message" : "${message}" }
    """.getBytes());

        var fsResult = freeMarkerComponent.generateFromTemplate(TEMPLATE, DATA);
        assertThat(fsResult).isEqualToIgnoringNewLines("""
      { "description" : "from file system", "message" : "It works!" }
      """);

        var template = "subpackage/" + TEMPLATE;
        var cpResult = freeMarkerComponent.generateFromTemplate(template, DATA);
        assertThat(cpResult)
            .isEqualToIgnoringNewLines("""
      { "description" : "from freemarker subpackage", "message" : "It works!" }
      """);
    }
}
