package edu.iu.uits.lms.iuonly.services.business;

import edu.iu.uits.lms.email.model.EmailDetails;
import edu.iu.uits.lms.email.model.Priority;
import edu.iu.uits.lms.email.service.EmailService;
import edu.iu.uits.lms.email.service.LmsEmailTooBigException;
import edu.iu.uits.lms.iuonly.config.DerdackConfig;
import edu.iu.uits.lms.iuonly.model.errorcontact.ErrorContactEvent;
import edu.iu.uits.lms.iuonly.model.errorcontact.ErrorContactJobProfile;
import edu.iu.uits.lms.iuonly.model.errorcontact.ErrorContactResponse;
import edu.iu.uits.lms.iuonly.repository.ErrorContactEventRepository;
import edu.iu.uits.lms.iuonly.repository.ErrorContactJobProfileRepository;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.mail.MessagingException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Profile("derdack")
@Service
@Slf4j
public class ErrorContactBusinessService {
    @Autowired
    @Qualifier("DerdackRestTemplate")
    private RestTemplate restTemplate;

    @Autowired
    private DerdackConfig derdackConfig;

    @Autowired
    private ErrorContactEventRepository errorContactEventRepository;

    @Autowired
    private ErrorContactJobProfileRepository errorContactJobProfileRepository;

    @Autowired
    private EmailService emailService;


    public ErrorContactResponse postEvent(@NonNull String jobCode, @NonNull String message) {
        return postEvent(jobCode, message, false);
    }

    public ErrorContactResponse postEvent(@NonNull String jobCode, @NonNull String message, boolean alwaysPage) {
        ErrorContactJobProfile errorContactJobProfile = errorContactJobProfileRepository.findByJobCode(jobCode);

        if (errorContactJobProfile == null) {
            log.error("No job profile found for jobCode = {}", jobCode);

            ErrorContactResponse resultErrorContactResponse = new ErrorContactResponse();
            resultErrorContactResponse.setExternalId("-1");
            resultErrorContactResponse.setStatus("jobCode not found");

            return resultErrorContactResponse;
        }

        log.info("Found job profile = {}", errorContactJobProfile);

        log.info("Always page = {}", alwaysPage);

        StringBuilder emailBody = new StringBuilder();

        Integer duplicateThresholdMinutes = errorContactJobProfile.getDuplicateMinutesThreshold();
        Integer duplicateMaxCount = errorContactJobProfile.getDuplicateMaxCount();

        boolean doPage = alwaysPage;

        // We need to start at 1 because this current error isn't in this count
        int jobcodeEventsWithinThreshold = 1;

        StringBuilder emailFirstPartMessage = new StringBuilder();
        emailFirstPartMessage.append("\nThe job ");
        emailFirstPartMessage.append(jobCode);
        emailFirstPartMessage.append(" has failed");

        if (duplicateThresholdMinutes != null && duplicateMaxCount != null) {

            if (! alwaysPage) {
                jobcodeEventsWithinThreshold = jobcodeEventsWithinThreshold +
                        errorContactEventRepository.numberOfJobCodesNoOlderThanMinutes(jobCode, duplicateThresholdMinutes);
                log.error("checked for past jobs and got job # = {}", jobcodeEventsWithinThreshold);
            }

            emailFirstPartMessage.append(" ");
            emailFirstPartMessage.append(jobcodeEventsWithinThreshold);
            emailFirstPartMessage.append(" times.");

            if (jobcodeEventsWithinThreshold >= duplicateMaxCount) {
                // it's paging time
                log.info("Paging for jobCode = {}", jobCode);

                doPage = true;
            }
        } else {
            emailFirstPartMessage.append(".");
        }

        ErrorContactEvent errorContactEvent = new ErrorContactEvent();
        errorContactEvent.setErrorContactJobProfile(errorContactJobProfile);
        errorContactEvent.setMessage(message);
        errorContactEvent.setAction(doPage ? "PAGED" : "EMAILED");

        ErrorContactResponse resultErrorContactResponse = null;

        String subject = emailService.getStandardHeader() + " LMS Microservices job " + jobCode;

        // Page and email
        if (doPage) {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

            Map<String, Object> formMap = new HashMap<>();

            formMap.put("team", derdackConfig.getTeam());
            formMap.put("subject", subject);
            formMap.put("body", subject + " - " + message);

            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(formMap, headers);

            String url = derdackConfig.getBaseUrl() + "/events";

            ResponseEntity<ErrorContactResponse> response = null;

            try {
                response = restTemplate.postForEntity(url, requestEntity, ErrorContactResponse.class);
            } catch (Exception e) {
                log.error("Error: ", e);
            }

            if (response != null && response.getBody() != null) {
                resultErrorContactResponse = response.getBody();

                log.info("Page completed for jobCode = {}", jobCode);

                emailBody.append(emailFirstPartMessage);
                emailBody.append(" The team has been paged with event id ");
                emailBody.append(resultErrorContactResponse.getExternalId());

                errorContactEvent.setPageEventId(resultErrorContactResponse.getExternalId());
            } else {
                log.error("Could not page for jobCode {}", jobCode);

                emailBody.append(emailFirstPartMessage);
                emailBody.append(" An attempt was made to page the team but a page could not be initiated!");
            }

        } else { // Email only (no page)
            resultErrorContactResponse = new ErrorContactResponse();
            resultErrorContactResponse.setExternalId("-1");
            resultErrorContactResponse.setStatus("Emailed ONLY");

            emailBody.append(" This email is to notify of the problem. No page has been sent");
        }

        errorContactEventRepository.save(errorContactEvent);

        emailBody.append("\n\nError = " + message);

        EmailDetails emailDetails = new EmailDetails();

        emailDetails.setPriority(Priority.HIGH);
        emailDetails.setSubject(subject);
        emailDetails.setBody(emailBody.toString());
        emailDetails.setRecipients(new String[]{derdackConfig.getRecipientEmail()});

        try {
            emailService.sendEmail(emailDetails, false);
        } catch (LmsEmailTooBigException | MessagingException e) {
            log.error("Error sending email");
        }

        return resultErrorContactResponse;
    }
}
