package edu.iu.uits.lms.iuonly.business;

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

import edu.iu.uits.lms.email.service.EmailService;
import edu.iu.uits.lms.iuonly.config.DerdackConfig;
import edu.iu.uits.lms.iuonly.model.errorcontact.ErrorContactJobProfile;
import edu.iu.uits.lms.iuonly.model.errorcontact.ErrorContactResponse;
import edu.iu.uits.lms.iuonly.repository.ErrorContactEventRepository;
import edu.iu.uits.lms.iuonly.repository.ErrorContactJobProfileRepository;
import edu.iu.uits.lms.iuonly.services.business.ErrorContactBusinessService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.client.RestTemplate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;

@ContextConfiguration(classes={ErrorContactBusinessService.class})
@ActiveProfiles("derdack")
@SpringBootTest
@Slf4j
public class ErrorContactBusinessServiceTest {
    @Autowired
    private ErrorContactBusinessService errorContactBusinessService;

    @Qualifier("DerdackRestTemplate")
    @MockBean
    private RestTemplate restTemplate;

    @MockBean
    private ErrorContactEventRepository errorContactEventRepository;

    @MockBean
    private ErrorContactJobProfileRepository errorContactJobProfileRepository;

    @MockBean
    private EmailService emailService;

    @MockBean
    private DerdackConfig derdackConfig;

    @Test
    public void testErrorContactService_job_not_found() throws Exception {
        ErrorContactResponse errorContactResponse = errorContactBusinessService.postEvent("JOB1", "This is the test");

        Assertions.assertNotNull(errorContactResponse);
        Assertions.assertEquals("-1", errorContactResponse.getExternalId());
        Assertions.assertEquals("jobCode not found", errorContactResponse.getStatus());
    }

    @Test
    public void testErrorContactService_job_not_have_limits() throws Exception {
        ErrorContactJobProfile errorContactJobProfile = new ErrorContactJobProfile();

        Mockito.when(errorContactJobProfileRepository.findByJobCode("JOB1")).thenReturn(errorContactJobProfile);
        ErrorContactResponse errorContactResponse = errorContactBusinessService.postEvent("JOB1", "This is the test");

        Assertions.assertEquals("-1", errorContactResponse.getExternalId());
        Assertions.assertEquals("Emailed ONLY", errorContactResponse.getStatus());
    }

    @Test
    public void testErrorContactService_job_not_have_limits_but_force_page() throws Exception {
        ErrorContactJobProfile errorContactJobProfile = new ErrorContactJobProfile();

        Mockito.when(errorContactJobProfileRepository.findByJobCode("JOB1")).thenReturn(errorContactJobProfile);

        ErrorContactResponse errorContactResponseFromDerdack = new ErrorContactResponse();
        errorContactResponseFromDerdack.setStatus("TEST PAGED");

        ResponseEntity<Object> responseEntity = new ResponseEntity<Object>((Object) errorContactResponseFromDerdack, null, HttpStatus.OK);

        Mockito.when(restTemplate.postForEntity(anyString(), any(), any())).thenReturn(responseEntity);

        ErrorContactResponse errorContactResponse = errorContactBusinessService.postEvent("JOB1", "This is the test", true);

        Assertions.assertEquals("TEST PAGED", errorContactResponse.getStatus());
    }

    @Test
    public void testErrorContactService_job_has_limits_but_not_reached() throws Exception {
        ErrorContactJobProfile errorContactJobProfile = new ErrorContactJobProfile();
        errorContactJobProfile.setDuplicateMinutesThreshold(1);
        errorContactJobProfile.setDuplicateMaxCount(3);

        Mockito.when(errorContactEventRepository.numberOfJobCodesNoOlderThanMinutes(anyString(), anyInt())).thenReturn(1);
        Mockito.when(errorContactJobProfileRepository.findByJobCode("JOB1")).thenReturn(errorContactJobProfile);
        ErrorContactResponse errorContactResponse = errorContactBusinessService.postEvent("JOB1", "This is the test");

        Assertions.assertEquals("-1", errorContactResponse.getExternalId());
        Assertions.assertEquals("Emailed ONLY", errorContactResponse.getStatus());
    }

    @Test
    public void testErrorContactService_job_has_limits_and_limit_is_reached() throws Exception {
        ErrorContactJobProfile errorContactJobProfile = new ErrorContactJobProfile();
        errorContactJobProfile.setDuplicateMinutesThreshold(1);
        errorContactJobProfile.setDuplicateMaxCount(2);

        Mockito.when(errorContactJobProfileRepository.findByJobCode("JOB1")).thenReturn(errorContactJobProfile);

        ErrorContactResponse errorContactResponseFromDerdack = new ErrorContactResponse();
        errorContactResponseFromDerdack.setStatus("TEST PAGED");

        ResponseEntity<Object> responseEntity = new ResponseEntity<Object>((Object) errorContactResponseFromDerdack, null, HttpStatus.OK);

        Mockito.when(restTemplate.postForEntity(anyString(), any(), any())).thenReturn(responseEntity);

        ErrorContactResponse errorContactResponse = errorContactBusinessService.postEvent("JOB1", "This is the test", true);

        Assertions.assertEquals("TEST PAGED", errorContactResponse.getStatus());
    }
}
