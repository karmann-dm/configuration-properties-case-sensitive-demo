package com.karmanno.config;

import com.zaxxer.hikari.HikariDataSource;
import org.postgresql.PGProperty;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class HikariPostgresPropertiesDictionary implements PropertiesDictionary {

    private static final Set<String> AVAILABLE_KEYS = Arrays.stream(PGProperty.values())
            .map(PGProperty::getName)
            .collect(Collectors.toSet());

    @Override
    public Class<?> getConfigurationPropertiesClass() {
        return HikariDataSource.class;
    }

    @Override
    public String getPropertyField() {
        return "dataSourceProperties";
    }

    @Override
    public String getPropertyKey(String rawKey) {
        return AVAILABLE_KEYS.stream()
                .filter(k -> k.equalsIgnoreCase(rawKey))
                .findFirst()
                .orElse(null);
    }

}
