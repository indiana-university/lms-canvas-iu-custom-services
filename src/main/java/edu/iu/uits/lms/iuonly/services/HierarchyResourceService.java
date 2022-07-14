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

import edu.iu.uits.lms.canvas.helpers.CourseHelper;
import edu.iu.uits.lms.canvas.model.Account;
import edu.iu.uits.lms.canvas.model.Course;
import edu.iu.uits.lms.canvas.services.AccountService;
import edu.iu.uits.lms.canvas.services.CourseService;
import edu.iu.uits.lms.iuonly.model.HierarchyResource;
import edu.iu.uits.lms.iuonly.model.coursetemplating.CourseTemplatesWrapper;
import edu.iu.uits.lms.iuonly.repository.HierarchyResourceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class HierarchyResourceService {

   @Autowired
   private HierarchyResourceRepository hierarchyResourceRepository;

   @Autowired
   private AccountService accountService;

   @Autowired
   private CourseService courseService;

   public HierarchyResource getTemplate(Long templateId) throws HierarchyResourceException {
      HierarchyResource hierarchyResource = hierarchyResourceRepository.findById(templateId).orElse(null);
      if (hierarchyResource == null) {
         throw new HierarchyResourceException("Could not find template with id " + templateId);
      }
      return hierarchyResource;
   }

   public CourseTemplatesWrapper getAvailableTemplatesForSisCourse(String sisCourseId) throws HierarchyResourceException {
      Course course = courseService.getCourse("sis_course_id:" + sisCourseId);
      return getAvailableTemplatesForCourse(course);
   }

   public CourseTemplatesWrapper getAvailableTemplatesForCanvasCourse(String canvasCourseId) throws HierarchyResourceException {
      Course course = courseService.getCourse(canvasCourseId);
      return getAvailableTemplatesForCourse(course);
   }

   private CourseTemplatesWrapper getAvailableTemplatesForCourse(Course course) throws HierarchyResourceException {
      String bodyText = "";
      CourseTemplatesWrapper courseTemplatesWrapper = new CourseTemplatesWrapper();
      List<HierarchyResource> hierarchyResources = new ArrayList<>();
      if (course != null) {
         Account account = accountService.getAccount(course.getAccountId());
         if (account != null) {
            // specific account doesn't exist in our table, let's see if there's a parent
            List<String> relatedAccountNames = new ArrayList<>();
            accountService.getParentAccounts(account.getId()).forEach(parentAccount -> relatedAccountNames.add(parentAccount.getName()));
            Collections.reverse(relatedAccountNames);

            for (String accountName : relatedAccountNames) {
               List<HierarchyResource> parentHierarchyResources = hierarchyResourceRepository.findByNode(accountName);
               if (parentHierarchyResources != null) {
                  hierarchyResources.addAll(parentHierarchyResources);
               }
            }

            List<HierarchyResource> hierarchyResourcesForNode = hierarchyResourceRepository.findByNode(account.getName());
            if (hierarchyResourcesForNode != null) {
               hierarchyResources.addAll(hierarchyResourcesForNode);
            }

            if (!hierarchyResources.isEmpty()) {
               courseTemplatesWrapper.setTemplates(hierarchyResources);
               courseTemplatesWrapper.setCourseId(course.getId());
               courseTemplatesWrapper.setCoursePublished(CourseHelper.isPublished(course));
               return courseTemplatesWrapper;
            }

            // if we're here, could not find a record in our table
            bodyText = "No node found for " + course.getId() + " (" + course.getSisCourseId() + ")";
         } else {
            bodyText = "Could not find account!";
         }
      } else {
         bodyText = "Course does not exist!";
      }

      // if we made it here, it did not find something along the way
      throw new HierarchyResourceException(bodyText);
   }

}
