package edu.iu.uits.lms.iuonly.business;

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