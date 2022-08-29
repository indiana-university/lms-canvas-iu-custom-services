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

import edu.iu.uits.lms.iuonly.model.FeatureAccess;
import edu.iu.uits.lms.iuonly.repository.FeatureAccessRepository;
import edu.iu.uits.lms.iuonly.services.FeatureAccessServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static edu.iu.uits.lms.iuonly.IuCustomConstants.READ;
import static edu.iu.uits.lms.iuonly.IuCustomConstants.WRITE;

@RestController
@RequestMapping("/rest/iu/featureaccess")
@Tag(name = "FeatureAccessController", description = "Operations involving the FeatureAccess table")
@Slf4j
public class FeatureAccessController {
    @Autowired
    private FeatureAccessRepository featureAccessRepository = null;

    @Autowired
    private FeatureAccessServiceImpl featureAccessService = null;

    @GetMapping("/{id}")
    @PreAuthorize("#oauth2.hasScope('" + READ + "')")
    @Operation(summary = "Get a particular FeatureAccess object by id")
    public FeatureAccess getFeatureAccessById(@PathVariable("id") Long id) {
        return featureAccessRepository.findById(id).orElse(null);
    }

    @GetMapping("/featureid/{featureId}")
    @PreAuthorize("#oauth2.hasScope('" + READ + "')")
    @Operation(summary = "Get all FeatureAccess objects for a given featureId")
    public List<FeatureAccess> getAccessRecsForFeature(@PathVariable("featureId") String featureId) {
        return featureAccessRepository.findByFeatureId(featureId);
    }

    @GetMapping("/all")
    @PreAuthorize("#oauth2.hasScope('" + READ + "')")
    @Operation(summary = "Get all FeatureAccess objects")
    public List<FeatureAccess> getAllFeatureAccessRecs() {
        return (List<FeatureAccess>) featureAccessRepository.findAll();
    }

    @PutMapping("{id}")
    @PreAuthorize("#oauth2.hasScope('" + WRITE + "')")
    @Operation(summary = "Update a FeatureAccess object with the given id")
    public FeatureAccess updateFeatureAccess(@PathVariable Long id, @RequestBody FeatureAccess featureAccess) {
        FeatureAccess updatedFeatureAccess = featureAccessRepository.findById(id).orElse(null);

        if (updatedFeatureAccess != null) {
            if (featureAccess.getFeatureId() != null) {
                updatedFeatureAccess.setFeatureId(featureAccess.getFeatureId());
            }

            if (featureAccess.getAccountId() != null) {
                updatedFeatureAccess.setAccountId(featureAccess.getAccountId());
            }

            return featureAccessRepository.save(updatedFeatureAccess);
        } else {
            return null;
        }
    }

    @PostMapping
    @PreAuthorize("#oauth2.hasScope('" + WRITE + "')")
    @Operation(summary = "Create a new FeatureAccess object")
    public FeatureAccess createFeatureAccess(@RequestBody FeatureAccess featureAccess) {
        FeatureAccess newAccess = new FeatureAccess();
        newAccess.setFeatureId(featureAccess.getFeatureId());
        newAccess.setAccountId(featureAccess.getAccountId());

        return featureAccessRepository.save(newAccess);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("#oauth2.hasScope('" + WRITE + "')")
    @Operation(summary = "Delete a FeatureAccess object with the given id")
    public String deleteFeatureAccess(@PathVariable("id") Long id) {
        featureAccessRepository.deleteById(id);
        return "Delete success.";
    }

    @DeleteMapping("/featureId/{featureId}")
    @PreAuthorize("#oauth2.hasScope('" + WRITE + "')")
    @Operation(summary = "Delete all FeatureAccess objects with the given featureId")
    public String deleteAllAccessForFeature(@PathVariable String featureId) {
        List<FeatureAccess> allAccessForFeature = featureAccessRepository.findByFeatureId(featureId);
        if (allAccessForFeature != null) {
            for (FeatureAccess access : allAccessForFeature) {
                featureAccessRepository.delete(access);
            }

            return "Delete success.";
        }

        return "No records to delete";

    }

    @GetMapping("/{accountid}/{featureid}")
    @PreAuthorize("#oauth2.hasScope('" + READ + "')")
    @Operation(summary = "Check if a given featureId is enabled in the given account, optionally including additional parent account IDs to check")
    public boolean isFeatureEnabledForAccount(@PathVariable("featureid") String featureId, @PathVariable("accountid") String accountId,
                                              @RequestParam(value = "parentAccountIds", required = false) List<String> parentAccountIds) {
        return featureAccessService.isFeatureEnabledForAccount(featureId, accountId, parentAccountIds);
    }
}
