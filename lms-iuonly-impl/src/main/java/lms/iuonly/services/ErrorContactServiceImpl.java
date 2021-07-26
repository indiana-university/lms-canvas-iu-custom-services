package lms.iuonly.services;

import io.swagger.annotations.Api;
import lms.iuonly.model.errorcontact.ErrorContactPostForm;
import lms.iuonly.model.errorcontact.ErrorContactResponse;
import lms.iuonly.services.business.ErrorContactBusinessService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/errorcontact")
@Slf4j
@Api(tags = "errorContact")
public class ErrorContactServiceImpl extends BaseService {
    @Autowired
    private ErrorContactBusinessService errorContactBusinessService;

    @PostMapping
    @PreAuthorize("#oauth2.hasScope('" + WRITE_SCOPE + "')")
    public ErrorContactResponse postEvent(@RequestBody ErrorContactPostForm errorContactPostForm) {

        if (errorContactPostForm == null ||
                errorContactPostForm.getJobCode() == null || errorContactPostForm.getJobCode().trim().length() == 0 ||
                errorContactPostForm.getMessage() == null || errorContactPostForm.getMessage().trim().length() == 0) {
            log.error("Missing job code and/or message jobCode = {}, message = {}",
                    errorContactPostForm.getJobCode(), errorContactPostForm.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing job code and/or message");
        }

        ErrorContactResponse resultErrorContactResponse = errorContactBusinessService.postEvent(errorContactPostForm.getJobCode(),
                errorContactPostForm.getMessage(), errorContactPostForm.isAlwaysPage());

        return resultErrorContactResponse;
    }
}