package edu.iu.uits.lms.iuonly.services;

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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class FeatureAccessServiceImpl {

    @Autowired
    private FeatureAccessRepository featureAccessRepository = null;

    public boolean isFeatureEnabledForAccount(String featureId, String accountId, List<String> parentAccountIds) {
        if (featureId == null || accountId == null) {
            throw new IllegalArgumentException("featureId and accountId may not be null. featureId: " + featureId + " accountId: " + accountId);
        }

        boolean enabled = false;

        // Is feature restricted?  If there are no recs for this feature, it is off by default.
        List<FeatureAccess> featureAccessRecs = featureAccessRepository.findByFeatureId(featureId);
        if (featureAccessRecs == null || featureAccessRecs.isEmpty()) {
            enabled = false;

        } else {
            List<String> relatedAccountIds = new ArrayList<>();
            relatedAccountIds.add(accountId);

            // If the feature is enabled for any of the parent accounts, this account will have access to the feature
            if (parentAccountIds != null && !parentAccountIds.isEmpty()) {
                relatedAccountIds.addAll(parentAccountIds);
            }

            for (FeatureAccess accessRec : featureAccessRecs) {
                if (relatedAccountIds.contains(accessRec.getAccountId())) {
                    enabled = true;
                    break;
                }
            }
        }

        return enabled;
    }
}
