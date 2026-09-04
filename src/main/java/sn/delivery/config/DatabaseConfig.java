package sn.delivery.config;

import javax.sql.DataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.beans.factory.annotation.Value;

@Configuration
public class DatabaseConfig {

    @Value("${spring.datasource.url:jdbc:h2:mem:deliverydb}")
    private String dbUrl;

    @Value("${spring.datasource.driver-class-name:org.h2.Driver}")
    private String dbDriver;

    @Value("${spring.datasource.username:sa}")
    private String dbUsername;

    @Value("${spring.datasource.password:}")
    private String dbPassword;

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(dbDriver);
        dataSource.setUrl(dbUrl);
        dataSource.setUsername(dbUsername);
        dataSource.setPassword(dbPassword);
        return dataSource;
    }
}
