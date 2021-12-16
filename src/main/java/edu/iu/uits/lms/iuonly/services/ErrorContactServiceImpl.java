package edu.iu.uits.lms.iuonly.services;

import edu.iu.uits.lms.iuonly.model.errorcontact.ErrorContactPostForm;
import edu.iu.uits.lms.iuonly.model.errorcontact.ErrorContactResponse;
import edu.iu.uits.lms.iuonly.services.business.ErrorContactBusinessService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Profile("derdack")
@Service
@Slf4j
public class ErrorContactServiceImpl {
    @Autowired
    private ErrorContactBusinessService errorContactBusinessService;

    public ErrorContactResponse postEvent(ErrorContactPostForm errorContactPostForm) {

        if (errorContactPostForm == null ||
                errorContactPostForm.getJobCode() == null || errorContactPostForm.getJobCode().trim().length() == 0 ||
                errorContactPostForm.getMessage() == null || errorContactPostForm.getMessage().trim().length() == 0) {
            log.error("Missing job code and/or message jobCode = {}, message = {}",
                    errorContactPostForm.getJobCode(), errorContactPostForm.getMessage());

            ErrorContactResponse resultErrorContactResponse = new ErrorContactResponse();
            resultErrorContactResponse.setExternalId("-1");
            resultErrorContactResponse.setStatus("Missing job code and/or message");

            return resultErrorContactResponse;
        }

        ErrorContactResponse resultErrorContactResponse = errorContactBusinessService.postEvent(errorContactPostForm.getJobCode(),
                errorContactPostForm.getMessage(), errorContactPostForm.isAlwaysPage());

        return resultErrorContactResponse;
    }
}