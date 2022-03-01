package lms.iuonly.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class DeptProvisioningUserBooleanOverride extends DeptProvisioningUser {
    private Boolean allowSisEnrollments;
    private Boolean overrideRestrictions;
}
