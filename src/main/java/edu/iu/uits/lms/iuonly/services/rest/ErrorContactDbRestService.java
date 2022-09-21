package edu.iu.uits.lms.iuonly.services.rest;

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

import edu.iu.uits.lms.iuonly.model.errorcontact.ErrorContactEvent;
import edu.iu.uits.lms.iuonly.model.errorcontact.ErrorContactJobProfile;
import edu.iu.uits.lms.iuonly.repository.ErrorContactEventRepository;
import edu.iu.uits.lms.iuonly.repository.ErrorContactJobProfileRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static edu.iu.uits.lms.iuonly.IuCustomConstants.IUCUSTOMREST_PROFILE;

@Profile(IUCUSTOMREST_PROFILE)
@RestController
@RequestMapping("/rest/iu/errorcontact")
@Tag(name = "ErrorContactDbRestService", description = "Operations involving the ErrorContactEvent and ErrorContactJobProfile tables")
@Slf4j
public class ErrorContactDbRestService {
    @Autowired
    private ErrorContactEventRepository errorContactEventRepository;

    @Autowired
    private ErrorContactJobProfileRepository errorContactJobProfileRepository;

    @GetMapping("/events/all")
    @Operation(summary = "Get all ErrorContactEvents")
    public List<ErrorContactEvent> getAllEvents() {
        return IterableUtils.toList(errorContactEventRepository.findAll());
    }

    @GetMapping("/jobprofiles/all")
    @Operation(summary = "Get all ErrorContactJobProfiles")
    public List<ErrorContactJobProfile> getAllJobProfiles() {
        return IterableUtils.toList(errorContactJobProfileRepository.findAll());
    }

    @GetMapping("/jobprofiles/{jobcode}")
    @Operation(summary = "Get an ErrorContactJobProfile for a given jobcode")
    public ErrorContactJobProfile getJobProfileByJobCode(@PathVariable("jobcode") String jobcode) {
        if (jobcode == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing job information");
        }

        ErrorContactJobProfile errorContactJobProfile = errorContactJobProfileRepository.findByJobCode(jobcode);

        if (errorContactJobProfile == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Job Profile not found");
        }

        return errorContactJobProfile;
    }

    @PostMapping("/jobprofiles")
    @Operation(summary = "Create a new ErrorContactJobProfile")
    public ErrorContactJobProfile createJobProfile(@RequestBody ErrorContactJobProfile errorContactJobProfile) {
        if (errorContactJobProfile == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing job information");
        }

        return errorContactJobProfileRepository.save(errorContactJobProfile);
    }

    @PutMapping("/jobprofiles")
    @Operation(summary = "Update an existing ErrorContactJobProfile,  based on its jobcode")
    public ErrorContactJobProfile updateJobProfile(@RequestBody ErrorContactJobProfile updatedErrorContactJobProfile) {
        if (updatedErrorContactJobProfile == null || updatedErrorContactJobProfile.getJobCode() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing job information");
        }

        ErrorContactJobProfile errorContactJobProfile = errorContactJobProfileRepository.findByJobCode(updatedErrorContactJobProfile.getJobCode());

        if (errorContactJobProfile == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Job Profile not found");
        }

        errorContactJobProfile.setDescription(updatedErrorContactJobProfile.getDescription());
        errorContactJobProfile.setActive(updatedErrorContactJobProfile.getActive());
        errorContactJobProfile.setDuplicateMinutesThreshold(updatedErrorContactJobProfile.getDuplicateMinutesThreshold());
        errorContactJobProfile.setDuplicateMaxCount(updatedErrorContactJobProfile.getDuplicateMaxCount());

        return errorContactJobProfileRepository.save(errorContactJobProfile);
    }

    @DeleteMapping("/jobprofiles/{jobcode}")
    @Operation(summary = "Delete an ErrorContactJobProfile,  based on its jobcode")
    public boolean deleteJobProfile(@PathVariable("jobcode") String jobcode) {
        if (jobcode == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing jobcode");
        }

        ErrorContactJobProfile errorContactJobProfile = errorContactJobProfileRepository.findByJobCode(jobcode);

        if (errorContactJobProfile == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Job Profile not found");
        }

        errorContactJobProfileRepository.delete(errorContactJobProfile);

        return true;
    }

    @PutMapping("/jobprofiles/activatebyjobcode/{jobcode}")
    @Operation(summary = "Activate an existing ErrorContactJobProfile,  based on its jobcode")
    public ErrorContactJobProfile activateByJobCode(@PathVariable("jobcode") String jobcode) {
        if (jobcode == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing jobcode");
        }

        ErrorContactJobProfile errorContactJobProfile = errorContactJobProfileRepository.findByJobCode(jobcode);

        if (errorContactJobProfile == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "invalid jobcode");
        }

        errorContactJobProfile.setActive(true);

        return errorContactJobProfileRepository.save(errorContactJobProfile);
    }

    @PutMapping("/jobprofiles/deactivatebyjobcode/{jobcode}")
    @Operation(summary = "Deactivate an existing ErrorContactJobProfile,  based on its jobcode")
    public ErrorContactJobProfile deactivateByJobCode(@PathVariable("jobcode") String jobcode) {
        if (jobcode == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing jobcode");
        }

        ErrorContactJobProfile errorContactJobProfile = errorContactJobProfileRepository.findByJobCode(jobcode);

        if (errorContactJobProfile == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "invalid jobcode");
        }

        errorContactJobProfile.setActive(false);

        return errorContactJobProfileRepository.save(errorContactJobProfile);
    }

    @PutMapping("/jobprofiles/activate/all")
    @Operation(summary = "Activate all existing ErrorContactJobProfiles")
    public void activateAllJobs() {
        errorContactJobProfileRepository.activateAllJobProfiles();
    }

    @PutMapping("/jobprofiles/deactivate/all")
    @Operation(summary = "Deactivate all existing ErrorContactJobProfiles")
    public void deactivateAllJobs() {
        errorContactJobProfileRepository.deactivateAllJobProfiles();
    }
}
