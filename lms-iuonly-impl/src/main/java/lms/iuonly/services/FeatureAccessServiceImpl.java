package lms.iuonly.services;

import io.swagger.annotations.Api;
import lms.iuonly.model.FeatureAccess;
import lms.iuonly.repository.FeatureAccessRepository;
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

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/featureaccess")
@Slf4j
@Api(tags = "featureAccess")
public class FeatureAccessServiceImpl extends BaseService {
    @Autowired
    private FeatureAccessRepository featureAccessRepository = null;

    @GetMapping("/{id}")
    @PreAuthorize("#oauth2.hasScope('" + READ_SCOPE + "')")
    public FeatureAccess getFeatureAccessById(@PathVariable("id") Long id) {
        return featureAccessRepository.findById(id).orElse(null);
    }

    @GetMapping("/featureid/{featureId}")
    @PreAuthorize("#oauth2.hasScope('" + READ_SCOPE + "')")
    public List<FeatureAccess> getAccessRecsForFeature(@PathVariable("featureId") String featureId) {
        return featureAccessRepository.findByFeatureId(featureId);
    }

    @GetMapping("/all")
    @PreAuthorize("#oauth2.hasScope('" + READ_SCOPE + "')")
    public List<FeatureAccess> getAllFeatureAccessRecs() {
        return (List<FeatureAccess>) featureAccessRepository.findAll();
    }

    @PutMapping("{id}")
    @PreAuthorize("#oauth2.hasScope('" + WRITE_SCOPE + "')")
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
    @PreAuthorize("#oauth2.hasScope('" + WRITE_SCOPE + "')")
    public FeatureAccess createFeatureAccess(@RequestBody FeatureAccess featureAccess) {
        FeatureAccess newAccess = new FeatureAccess();
        newAccess.setFeatureId(featureAccess.getFeatureId());
        newAccess.setAccountId(featureAccess.getAccountId());

        return featureAccessRepository.save(newAccess);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("#oauth2.hasScope('" + WRITE_SCOPE + "')")
    public String deleteFeatureAccess(@PathVariable("id") Long id) {
        featureAccessRepository.deleteById(id);
        return "Delete success.";
    }

    @DeleteMapping("/featureId/{featureId}")
    @PreAuthorize("#oauth2.hasScope('" + WRITE_SCOPE + "')")
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
    @PreAuthorize("#oauth2.hasScope('" + READ_SCOPE + "')")
    public boolean isFeatureEnabledForAccount(@PathVariable("featureid") String featureId, @PathVariable("accountid") String accountId,
                                              @RequestParam(value = "parentAccountIds", required = false) List<String> parentAccountIds) {
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
