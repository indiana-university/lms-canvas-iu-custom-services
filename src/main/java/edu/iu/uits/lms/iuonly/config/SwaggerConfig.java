package edu.iu.uits.lms.iuonly.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.OAuthFlow;
import io.swagger.v3.oas.annotations.security.OAuthFlows;
import io.swagger.v3.oas.annotations.security.OAuthScope;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("iucustomrest & swagger")
@Configuration("IuCustomSwaggerConfig")
@SecurityScheme(name = "security_auth_iu", type = SecuritySchemeType.OAUTH2,
      flows = @OAuthFlows(authorizationCode = @OAuthFlow(
            authorizationUrl = "${springdoc.oAuthFlow.authorizationUrl}",
            scopes = {@OAuthScope(name = "iusvcs:read"), @OAuthScope(name = "iusvcs:write")},
            tokenUrl = "${springdoc.oAuthFlow.tokenUrl}")))
public class SwaggerConfig {

   @Bean
   public GroupedOpenApi iuCustomOpenApi() {
      return GroupedOpenApi.builder()
            .group("iu-custom")
            .packagesToScan("edu.iu.uits.lms.iuonly")
            .pathsToMatch("/rest/iu/**")
            .build();
   }
}
