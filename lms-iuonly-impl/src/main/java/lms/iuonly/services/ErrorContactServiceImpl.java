package lms.iuonly.services;

import io.swagger.annotations.Api;
import lms.iuonly.model.errorcontact.ErrorContactEvent;
import lms.iuonly.model.errorcontact.ErrorContactJobProfile;
import lms.iuonly.model.errorcontact.ErrorContactPostForm;
import lms.iuonly.model.errorcontact.ErrorContactResponse;
import lms.iuonly.repository.ErrorContactEventRepository;
import lms.iuonly.repository.ErrorContactJobProfileRepository;
import lms.iuonly.services.business.ErrorContactService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/errorcontact")
@Slf4j
@Api(tags = "errorContact")
public class ErrorContactServiceImpl extends BaseService {
    @Autowired
    private ErrorContactEventRepository errorContactEventRepository;

    @Autowired
    private ErrorContactJobProfileRepository errorContactJobProfileRepository;

    @Autowired
    private ErrorContactService errorContactService;

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

        ErrorContactResponse resultErrorContactResponse = errorContactService.postEvent(errorContactPostForm.getJobCode(),
                errorContactPostForm.getMessage(), errorContactPostForm.isAlwaysPage());

        return resultErrorContactResponse;
    }

    @GetMapping("/events/all")
    @PreAuthorize("#oauth2.hasScope('" + READ_SCOPE + "')")
    public List<ErrorContactEvent> getAllEvents() {
        return IterableUtils.toList(errorContactEventRepository.findAll());
    }

    @GetMapping("/jobprofiles/all")
    @PreAuthorize("#oauth2.hasScope('" + READ_SCOPE + "')")
    public List<ErrorContactJobProfile> getAllJobProfiles() {
        return IterableUtils.toList(errorContactJobProfileRepository.findAll());
    }

    @PostMapping("/jobprofiles")
    @PreAuthorize("#oauth2.hasScope('" + WRITE_SCOPE + "')")
    public ErrorContactJobProfile postJobProfile(@RequestBody ErrorContactJobProfile errorContactJobProfile) {
        if (errorContactJobProfile == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing job information");
        }

        return errorContactJobProfileRepository.save(errorContactJobProfile);
    }

    @DeleteMapping("/jobprofiles/{jobcode}")
    @PreAuthorize("#oauth2.hasScope('" + WRITE_SCOPE + "')")
    public boolean deleteJobProfile(@PathVariable("jobcode") String jobcode) {
        if (jobcode == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing jobcode");
        }

        ErrorContactJobProfile errorContactJobProfile = errorContactJobProfileRepository.findByJobCode(jobcode);

        if (errorContactJobProfile == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Job not found");
        }

        errorContactJobProfileRepository.delete(errorContactJobProfile);

        return true;
    }
}