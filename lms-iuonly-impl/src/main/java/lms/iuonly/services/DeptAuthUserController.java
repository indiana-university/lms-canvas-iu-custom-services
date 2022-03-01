package lms.iuonly.services;

import lms.iuonly.model.DeptProvisioningUser;
import lms.iuonly.model.DeptProvisioningUserBooleanOverride;
import lms.iuonly.repository.DeptProvisioningUserRepository;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static lms.iuonly.services.BaseService.READ_SCOPE;
import static lms.iuonly.services.BaseService.WRITE_SCOPE;

@RestController
@RequestMapping("/dept_auth_user")
@Slf4j
public class DeptAuthUserController {

   @Autowired
   private DeptProvisioningUserRepository deptProvisioningUserRepository;

   @GetMapping("/{id}")
   @PreAuthorize("#oauth2.hasScope('" + READ_SCOPE + "')")
   public DeptProvisioningUser getFromId(@PathVariable Long id) {
      return deptProvisioningUserRepository.findById(id).orElse(null);
   }

   @GetMapping("/username/{username}")
   @PreAuthorize("#oauth2.hasScope('" + READ_SCOPE + "')")
   public DeptProvisioningUser getByUsername(@PathVariable String username) {
      return deptProvisioningUserRepository.findByUsername(username);
   }

   @GetMapping("/canvasId/{canvasId}")
   @PreAuthorize("#oauth2.hasScope('" + READ_SCOPE + "')")
   public DeptProvisioningUser getByCanvasUserId(@PathVariable String canvasId) {
      return deptProvisioningUserRepository.findByCanvasUserId(canvasId);
   }

   @GetMapping("/all")
   @PreAuthorize("#oauth2.hasScope('" + READ_SCOPE + "')")
   public List<DeptProvisioningUser> getAll() {
      return (List<DeptProvisioningUser>) deptProvisioningUserRepository.findAll();
   }

   @PutMapping("/{id}")
   @PreAuthorize("#oauth2.hasScope('" + WRITE_SCOPE + "')")
   public DeptProvisioningUser update(@PathVariable Long id, @RequestBody DeptProvisioningUserBooleanOverride user) {
      DeptProvisioningUser updatingUser = deptProvisioningUserRepository.findById(id).orElse(null);

      if (user.getGroupCode() != null) {
         updatingUser.setGroupCode(user.getGroupCode());
      }
      if (user.getUsername() != null) {
         updatingUser.setUsername(user.getUsername());
      }
      if (user.getCanvasUserId() != null) {
         updatingUser.setCanvasUserId(user.getCanvasUserId());
      }
      if (user.getDisplayName() != null) {
         updatingUser.setDisplayName(user.getDisplayName());
      }
      if (user.getAllowSisEnrollments() != null) {
         updatingUser.setAllowSisEnrollments(user.getAllowSisEnrollments());
      }
      if (user.getAuthorizedAccounts() != null) {
         updatingUser.setAuthorizedAccounts(user.getAuthorizedAccounts());
      }
      if (user.getOverrideRestrictions() != null) {
         updatingUser.setOverrideRestrictions(user.getOverrideRestrictions());
      }
      return deptProvisioningUserRepository.save(updatingUser);
   }

   @PostMapping("/")
   @PreAuthorize("#oauth2.hasScope('" + WRITE_SCOPE + "')")
   public DeptProvisioningUser create(@RequestBody DeptProvisioningUser user) {
      DeptProvisioningUser newUser = new DeptProvisioningUser();
      newUser.setGroupCode(user.getGroupCode());
      newUser.setUsername(user.getUsername());
      newUser.setDisplayName(user.getDisplayName());
      newUser.setCanvasUserId(user.getCanvasUserId());
      newUser.setAllowSisEnrollments(user.isAllowSisEnrollments());
      newUser.setAuthorizedAccounts(user.getAuthorizedAccounts());
      newUser.setOverrideRestrictions(user.isOverrideRestrictions());
      return deptProvisioningUserRepository.save(newUser);
   }

   @DeleteMapping("/{id}")
   @PreAuthorize("#oauth2.hasScope('" + WRITE_SCOPE + "')")
   public String delete(@PathVariable Long id) {
      deptProvisioningUserRepository.deleteById(id);
      return "Delete success.";
   }
}
