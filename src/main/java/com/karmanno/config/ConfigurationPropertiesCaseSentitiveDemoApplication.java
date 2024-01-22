package com.karmanno.config;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ConfigurationPropertiesCaseSentitiveDemoApplication implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(ConfigurationPropertiesCaseSentitiveDemoApplication.class);

    @Autowired
    private HikariDataSource dataSource;

    public static void main(String[] args) {
        SpringApplication.run(ConfigurationPropertiesCaseSentitiveDemoApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("Socket timeout {}", dataSource.getConnection().getNetworkTimeout());
    }

}
