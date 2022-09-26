package edu.iu.uits.lms.iuonly.rest;

import edu.iu.uits.lms.common.test.CommonTestUtils;
import edu.iu.uits.lms.iuonly.IuCustomConstants;
import edu.iu.uits.lms.iuonly.config.IuCustomRestConfiguration;
import edu.iu.uits.lms.iuonly.services.rest.BatchEmailRestController;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.NestedTestConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collection;

import static edu.iu.uits.lms.iuonly.IuCustomConstants.IUCUSTOMREST_PROFILE;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.context.NestedTestConfiguration.EnclosingConfiguration.INHERIT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@NestedTestConfiguration(INHERIT)
@WebMvcTest(controllers = {BatchEmailRestController.class})
@Import(IuCustomRestConfiguration.class)
public class RestControllerAuthzTest {

   @MockBean
   private JwtDecoder jwtDecoder;

   @Nested
   @ActiveProfiles({IUCUSTOMREST_PROFILE})
   class Enabled {

      @Autowired
      private MockMvc mvc;

      @Test
      public void appNoAuthnLaunch() throws Exception {
         //This is a secured endpoint and should not allow access without authn
         SecurityContextHolder.getContext().setAuthentication(null);
         mvc.perform(get("/rest/iu/batchemail/all")
                     .header(HttpHeaders.USER_AGENT, CommonTestUtils.defaultUseragent())
                     .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isUnauthorized());
      }

      @Test
      public void restAuthnLaunch() throws Exception {
         Jwt jwt = CommonTestUtils.createJwtToken("asdf");

         Collection<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList(IuCustomConstants.READ_SCOPE, "ROLE_LMS_REST_ADMINS");
         JwtAuthenticationToken token = new JwtAuthenticationToken(jwt, authorities);

         //This is a secured endpoint and should not allow access without authn
         mvc.perform(get("/rest/iu/batchemail/all")
                     .header(HttpHeaders.USER_AGENT, CommonTestUtils.defaultUseragent())
                     .contentType(MediaType.APPLICATION_JSON)
                     .with(authentication(token)))
               .andExpect(status().isOk());
      }

      @Test
      public void restAuthnLaunchWithWrongScope() throws Exception {
         Jwt jwt = CommonTestUtils.createJwtToken("asdf");

         Collection<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList("SCOPE_read", "ROLE_NONE_YA");
         JwtAuthenticationToken token = new JwtAuthenticationToken(jwt, authorities);

         //This is a secured endpoint and should not allow access without authn
         mvc.perform(get("/rest/iu/batchemail/all")
                     .header(HttpHeaders.USER_AGENT, CommonTestUtils.defaultUseragent())
                     .contentType(MediaType.APPLICATION_JSON)
                     .with(authentication(token)))
               .andExpect(status().isForbidden());
      }
   }

   @Nested
   class Disabled {

      @Autowired
      private MockMvc mvc;

      @Test
      public void appNoAuthnLaunch() throws Exception {
         //This is a secured endpoint and should not allow access without authn
         SecurityContextHolder.getContext().setAuthentication(null);
         mvc.perform(get("/rest/iu/batchemail/all")
                     .header(HttpHeaders.USER_AGENT, CommonTestUtils.defaultUseragent())
                     .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isUnauthorized());
      }

      @Test
      public void restAuthnLaunch() throws Exception {
         Jwt jwt = CommonTestUtils.createJwtToken("asdf");

         Collection<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList(IuCustomConstants.READ_SCOPE, "ROLE_LMS_REST_ADMINS");
         JwtAuthenticationToken token = new JwtAuthenticationToken(jwt, authorities);

         // This is a secured endpoint and should not allow access without authn.
         // But, since we have authn, we get through security to find that the endpoint isn't available
         mvc.perform(get("/rest/iu/batchemail/all")
                     .header(HttpHeaders.USER_AGENT, CommonTestUtils.defaultUseragent())
                     .contentType(MediaType.APPLICATION_JSON)
                     .with(authentication(token)))
               .andExpect(status().isNotFound());
      }

      @Test
      public void restAuthnLaunchWithWrongScope() throws Exception {
         Jwt jwt = CommonTestUtils.createJwtToken("asdf");

         Collection<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList("SCOPE_read", "ROLE_NONE_YA");
         JwtAuthenticationToken token = new JwtAuthenticationToken(jwt, authorities);

         //This is a secured endpoint and should not allow access without authn
         mvc.perform(get("/rest/iu/batchemail/all")
                     .header(HttpHeaders.USER_AGENT, CommonTestUtils.defaultUseragent())
                     .contentType(MediaType.APPLICATION_JSON)
                     .with(authentication(token)))
               .andExpect(status().isForbidden());
      }
   }
}
