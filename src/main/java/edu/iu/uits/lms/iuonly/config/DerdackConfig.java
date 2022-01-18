package edu.iu.uits.lms.iuonly.config;

import edu.iu.uits.lms.iuonly.security.DerdackKeyAuthorizationInterceptor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.client.RestTemplate;

@Profile("derdack")
@Configuration
@ConfigurationProperties(prefix = "derdack")
@Getter
@Setter
public class DerdackConfig {
    private String baseUrl;
    private String apiKey;
    private String team;
    private String recipientEmail;

    @Bean(name = "DerdackRestTemplate")
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();

        restTemplate.getInterceptors().add(new DerdackKeyAuthorizationInterceptor(apiKey));

        return restTemplate;
    }
}
