package edu.iu.uits.lms.iuonly.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableJpaRepositories(
        entityManagerFactoryRef = "postgresdbEntityMgrFactory",
        transactionManagerRef = "postgresdbTransactionMgr",
        basePackages = {
                "edu.iu.uits.lms.iuonly.repository"
        })

@EnableTransactionManagement
public class PostgresDBConfig {

    @Bean(name = "postgresdb")
    @ConfigurationProperties(prefix = "spring.datasource")
    @Primary
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "postgresdbEntityMgrFactory")
    @Primary
    public LocalContainerEntityManagerFactoryBean postgresdbEntityMgrFactory(
            final EntityManagerFactoryBuilder builder,
            @Qualifier("postgresdb") final DataSource dataSource) {
        // dynamically setting up the hibernate properties for each of the datasource.
        final Map<String, String> properties = new HashMap<>();
        return builder
                .dataSource(dataSource)
                .properties(properties)
                .packages("edu.iu.uits.lms.iuonly.model")
                .build();
    }

    @Bean(name = "postgresdbTransactionMgr")
    @Primary
    public PlatformTransactionManager postgresdbTransactionMgr(
            @Qualifier("postgresdbEntityMgrFactory") final EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}
