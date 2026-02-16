package com.nova.nova_server.global.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.victools.jsonschema.generator.*;
import com.github.victools.jsonschema.module.jackson.JacksonModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JsonSchemaConfig {

    @Bean
    public SchemaGenerator schemaGenerator(ObjectMapper objectMapper) {
        SchemaGeneratorConfig config = new SchemaGeneratorConfigBuilder(
                        objectMapper,
                        SchemaVersion.DRAFT_2020_12,
                        OptionPreset.PLAIN_JSON
                )
                .with(Option.FORBIDDEN_ADDITIONAL_PROPERTIES_BY_DEFAULT)
                .with(new JacksonModule())
                .build();

        return new SchemaGenerator(config);
    }

}
