package edu.iu.uits.lms.iuonly.config;

import edu.iu.uits.lms.common.oauth.CustomJwtAuthenticationConverter;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

public class IuCustomRestConfiguration {
    @Profile("iucustomrest")
    @Configuration
    @Order(SecurityProperties.BASIC_AUTH_ORDER - 4996)
    public static class IuRestWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.requestMatchers().antMatchers("/rest/iu/**")
                    .and()
                    .authorizeRequests()
                    .antMatchers("/rest/iu/**")
                    .access("hasAuthority('SCOPE_iusvcs:read') or hasAuthority('SCOPE_iusvcs:write')")
                    .and()
                    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and()
                    .oauth2ResourceServer()
                    .jwt().jwtAuthenticationConverter(new CustomJwtAuthenticationConverter());
        }
    }

    @Profile("iucustomrest & swagger")
    @Configuration
    @Order(SecurityProperties.BASIC_AUTH_ORDER - 4995)
    public static class IuApiWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.requestMatchers().antMatchers("/api/iu/**")
                  .and()
                  .authorizeRequests()
                  .antMatchers("/api/iu/**").permitAll();
        }
    }
}
