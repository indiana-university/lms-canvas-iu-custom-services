package lms.iuonly.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class DenodoDBConfig {

    @Bean(name = "denododb")
    @ConfigurationProperties(prefix = "spring.datasource.denodo")
    // setting up the data source for the denodo database.
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }
}
