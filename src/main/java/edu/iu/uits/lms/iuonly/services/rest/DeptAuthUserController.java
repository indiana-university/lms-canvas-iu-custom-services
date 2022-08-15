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

import edu.iu.uits.lms.iuonly.model.DeptProvisioningUser;
import edu.iu.uits.lms.iuonly.model.DeptProvisioningUserBooleanOverride;
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

import static edu.iu.uits.lms.iuonly.IuCustomConstants.READ;
import static edu.iu.uits.lms.iuonly.IuCustomConstants.WRITE;

@RestController
@RequestMapping("/rest/iu/dept_auth_user")
@Slf4j
public class DeptAuthUserController {

   @Autowired
   private DeptProvisioningUserRepository deptProvisioningUserRepository;

   @GetMapping("/{id}")
   @PreAuthorize("#oauth2.hasScope('" + READ + "')")
   public DeptProvisioningUser getFromId(@PathVariable Long id) {
      return deptProvisioningUserRepository.findById(id).orElse(null);
   }

   @GetMapping("/username/{username}")
   @PreAuthorize("#oauth2.hasScope('" + READ + "')")
   public DeptProvisioningUser getByUsername(@PathVariable String username) {
      return deptProvisioningUserRepository.findByUsername(username);
   }

   @GetMapping("/canvasId/{canvasId}")
   @PreAuthorize("#oauth2.hasScope('" + READ + "')")
   public DeptProvisioningUser getByCanvasUserId(@PathVariable String canvasId) {
      return deptProvisioningUserRepository.findByCanvasUserId(canvasId);
   }

   @GetMapping("/all")
   @PreAuthorize("#oauth2.hasScope('" + READ + "')")
   public List<DeptProvisioningUser> getAll() {
      return (List<DeptProvisioningUser>) deptProvisioningUserRepository.findAll();
   }

   @PutMapping("/{id}")
   @PreAuthorize("#oauth2.hasScope('" + WRITE + "')")
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
   @PreAuthorize("#oauth2.hasScope('" + WRITE + "')")
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
   @PreAuthorize("#oauth2.hasScope('" + WRITE + "')")
   public String delete(@PathVariable Long id) {
      deptProvisioningUserRepository.deleteById(id);
      return "Delete success.";
   }
}
