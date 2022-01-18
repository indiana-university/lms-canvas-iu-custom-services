package edu.iu.uits.lms.iuonly.services.rest;

import edu.iu.uits.lms.iuonly.model.DeptProvisioningUser;
import edu.iu.uits.lms.iuonly.repository.DeptProvisioningUserRepository;
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

@RestController
@RequestMapping("/rest/iu/dept_auth_user")
@Slf4j
public class DeptAuthUserController extends BaseService {

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
   public DeptProvisioningUser update(@PathVariable Long id, @RequestBody DeptProvisioningUser user) {
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
      return deptProvisioningUserRepository.save(newUser);
   }

   @DeleteMapping("/{id}")
   @PreAuthorize("#oauth2.hasScope('" + WRITE_SCOPE + "')")
   public String delete(@PathVariable Long id) {
      deptProvisioningUserRepository.deleteById(id);
      return "Delete success.";
   }
}
