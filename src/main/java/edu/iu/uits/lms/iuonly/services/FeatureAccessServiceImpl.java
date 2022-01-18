package edu.iu.uits.lms.iuonly.services;

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
