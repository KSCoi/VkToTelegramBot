package ru.tsoyk.tg.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;


import javax.sql.DataSource;

@PropertySource("classpath:database.properties")
@Configuration
public class DbConnectionConfig {
    private final String driverClassName = "com.mysql.cj.jdbc.Driver";
    @Value("${jdbc.password}")
    private String password;
    @Value("${jdbc.username}")
    private String username;
    @Value("${jdbc.url}")
    private String URL;
    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource(URL,username,password);
        dataSource.setDriverClassName(driverClassName);
        return dataSource;
    }

    @Bean
    public JdbcTemplate jdbcTemplate() {
        return new JdbcTemplate(dataSource());
    }
}
