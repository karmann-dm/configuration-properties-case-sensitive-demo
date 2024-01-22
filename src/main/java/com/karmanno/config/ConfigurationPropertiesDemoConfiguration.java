package com.karmanno.config;

import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class ConfigurationPropertiesDemoConfiguration {

    @Bean
    public HikariPostgresPropertiesDictionary hikariPostgresPropertiesDictionary() {
        return new HikariPostgresPropertiesDictionary();
    }

    @Bean
    public ConfigurationTransformingPostProcessor configurationTransformingPostProcessor(ConfigurableListableBeanFactory beanFactory,
                                                                                         List<PropertiesDictionary> dictionaries) {
        return new ConfigurationTransformingPostProcessor(beanFactory, dictionaries);
    }

}
