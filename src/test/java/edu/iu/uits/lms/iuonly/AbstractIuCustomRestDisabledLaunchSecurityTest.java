package edu.iu.uits.lms.iuonly;

/*-
 * #%L
 * lms-canvas-iu-custom-services
 * %%
 * Copyright (C) 2015 - 2022 Indiana University
 * %%
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * 3. Neither the name of the Indiana University nor the names of its contributors
 *    may be used to endorse or promote products derived from this software without
 *    specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

import edu.iu.uits.lms.common.test.CommonTestUtils;
import edu.iu.uits.lms.iuonly.services.rest.BatchEmailRestController;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collection;

import static edu.iu.uits.lms.iuonly.IuCustomConstants.READ_SCOPE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = BatchEmailRestController.class, properties = {"oauth.tokenprovider.url: http://foo"})
public abstract class AbstractIuCustomRestDisabledLaunchSecurityTest {
   @Autowired
   private MockMvc mvc;

   @Test
   public void appNoAuthnLaunch() throws Exception {
      //This is a secured endpoint and should not allow access without authn
      SecurityContextHolder.getContext().setAuthentication(null);
      mvc.perform(get("/rest/iu/batchemail/all")
                  .header(HttpHeaders.USER_AGENT, CommonTestUtils.defaultUseragent())
                  .contentType(MediaType.APPLICATION_JSON))
            .andExpect(result -> assertThat("Response status", result.getResponse().getStatus(),
                  Matchers.anyOf(Matchers.is(HttpStatus.UNAUTHORIZED.value()),
                        Matchers.is(HttpStatus.FORBIDDEN.value()))));
      // The above matcher is using an anyOf here to handle 2 different configuration cases.
      // The first is the "normal" case where the REST endpoint configuration exists, but the user is not authorized.
      // The second case is for when the configuration is "missing" because the tool doesn't need it as it doesn't have its own REST endpoints to configure.
   }

   @Test
   public void restAuthnLaunch() throws Exception {
      Jwt jwt = CommonTestUtils.createJwtToken("asdf");

      Collection<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList(READ_SCOPE, "ROLE_LMS_REST_ADMINS");
      JwtAuthenticationToken token = new JwtAuthenticationToken(jwt, authorities);

      //This is a secured endpoint and should not allow access without authn
      mvc.perform(get("/rest/iu/batchemail/all")
                  .header(HttpHeaders.USER_AGENT, CommonTestUtils.defaultUseragent())
                  .contentType(MediaType.APPLICATION_JSON)
                  .with(authentication(token)))
            .andExpect(status().isForbidden());
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
