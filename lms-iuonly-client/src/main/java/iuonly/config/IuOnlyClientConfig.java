package iuonly.config;

import edu.iu.uits.lms.common.oauth.OAuthConfig;
import iuonly.client.generated.ApiClient;
import iuonly.client.generated.api.FeatureAccessApi;
import iuonly.client.generated.api.SudsApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.token.AccessTokenRequest;
import org.springframework.security.oauth2.client.token.DefaultAccessTokenRequest;
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordResourceDetails;
import org.springframework.security.oauth2.common.AuthenticationScheme;

import java.util.Arrays;

public class IuOnlyClientConfig {
    private ApiClient apiClient;

    @Value("${lms.service.iuonly.url.${app.env}}")
    private String baseServiceUrl;

    @Autowired
    private OAuthConfig oAuthConfig;

    @Bean
    public FeatureAccessApi featureAccessApi() {
        return new FeatureAccessApi(apiClient());
    }

    @Bean
    public SudsApi sudsApi() {
        return new SudsApi(apiClient());
    }


    private ApiClient apiClient() {
        if (apiClient == null) {
            apiClient = new ApiClient(iuOnlyClientRestTemplate());
            apiClient.setBasePath(baseServiceUrl);
        }
        return apiClient;
    }

    @Bean(name = "iuOnlyClientRestTemplate")
    public OAuth2RestTemplate iuOnlyClientRestTemplate() {
        ResourceOwnerPasswordResourceDetails resourceDetails = new ResourceOwnerPasswordResourceDetails();
        resourceDetails.setClientId(oAuthConfig.getClientId());
        resourceDetails.setClientSecret(oAuthConfig.getClientSecret());
        resourceDetails.setUsername(oAuthConfig.getClientId());
        resourceDetails.setPassword(oAuthConfig.getClientPassword());
        resourceDetails.setAccessTokenUri(oAuthConfig.getAccessTokenUri());
        resourceDetails.setClientAuthenticationScheme(AuthenticationScheme.form);
        resourceDetails.setScope(Arrays.asList("iusvcs:read", "iusvcs:write"));

        AccessTokenRequest atr = new DefaultAccessTokenRequest();
        DefaultOAuth2ClientContext clientContext = new DefaultOAuth2ClientContext(atr);

        OAuth2RestTemplate restTemplate = new OAuth2RestTemplate(resourceDetails, clientContext);
        return restTemplate;
    }
}
