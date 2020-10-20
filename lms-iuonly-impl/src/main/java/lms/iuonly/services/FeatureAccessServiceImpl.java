package lms.iuonly.services;

import canvas.client.generated.api.AccountsApi;
import io.swagger.annotations.Api;
import lms.iuonly.model.FeatureAccess;
import lms.iuonly.repository.FeatureAccessRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/featureaccess")
@Slf4j
@Api(tags = "featureAccess")
public class FeatureAccessServiceImpl extends BaseService {
    @Autowired
    private FeatureAccessRepository featureAccessRepository = null;

    @Autowired
    private AccountsApi accountsApi = null;

    @GetMapping("/{accountid}/{featureid}")
    @PreAuthorize("#oauth2.hasScope('" + READ_SCOPE + "')")
    public boolean isFeatureEnabledForAccount(@PathVariable("featureid") String featureId, @PathVariable("accountid") String accountId) {
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
            accountsApi.getParentAccounts(accountId).forEach(parentAccount -> relatedAccountIds.add(parentAccount.getId()));

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
