package com.weehong.bootstrap.database.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "spring.datasource")
public class DatabaseProperty {

    @NestedConfigurationProperty
    private final HikariProperty hikari = new HikariProperty();

    private String url;
    private String username;
    private String password;
    private String driverClassName;

}
