package com.karmanno.config;

public interface PropertiesDictionary {

    Class<?> getConfigurationPropertiesClass();

    String getPropertyField();

    String getPropertyKey(String propertyKey);

}
