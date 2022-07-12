package edu.iu.uits.lms.iuonly.services;

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
