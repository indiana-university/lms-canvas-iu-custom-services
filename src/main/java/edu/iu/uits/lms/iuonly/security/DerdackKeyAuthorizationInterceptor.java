package edu.iu.uits.lms.iuonly.security;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.support.HttpRequestWrapper;
import org.springframework.util.Assert;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;

public class DerdackKeyAuthorizationInterceptor implements ClientHttpRequestInterceptor {

    private final String apiKey;

    private static final String APIKEY_PARAMETER_NAME = "apiKey";

    /**
     * Create a new interceptor which adds the apiKey query parameter
     * @param apiKey to use
     */
    public DerdackKeyAuthorizationInterceptor(String apiKey) {
        Assert.hasLength(apiKey, "Api key must not be empty");
        this.apiKey = apiKey;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body,
                                        ClientHttpRequestExecution execution) throws IOException {
        URI uri = UriComponentsBuilder.fromHttpRequest(request)
                .queryParam(APIKEY_PARAMETER_NAME, apiKey)
                .build().toUri();

        HttpRequest modifiedRequest = new HttpRequestWrapper(request) {

            @Override
            public URI getURI() {
                return uri;
            }
        };

        return execution.execute(modifiedRequest, body);
    }
}
