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

import edu.iu.uits.lms.canvas.model.ContentMigration;
import edu.iu.uits.lms.canvas.services.ContentMigrationService;
import edu.iu.uits.lms.iuonly.helpers.ContentMigrationHelper;
import edu.iu.uits.lms.iuonly.model.coursetemplating.ContentMigrationStatus;
import edu.iu.uits.lms.iuonly.model.coursetemplating.TemplatedCourse;
import edu.iu.uits.lms.iuonly.repository.TemplatedCourseRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CourseTemplatingService {

   @Autowired
   private TemplatedCourseRepository templatedCourseRepository;

   @Autowired
   private ContentMigrationService contentMigrationService;

   public List<TemplatedCourse> findBySisCourseId(String sisCourseId) {
      return templatedCourseRepository.findBySisCourseId(sisCourseId);
   }

   private TemplatedCourse getTemplatedCourse(String courseId) {
      return templatedCourseRepository.findById(courseId).orElse(null);
   }

   public TemplatedCourse saveTemplatedCourse(TemplatedCourse templatedCourse) {
      return templatedCourseRepository.save(templatedCourse);
   }

   /**
    * Trigger a content migration, which will setup the course from the template
    * @param courseId Canvas Course id
    * @param termId Term Id used to determine if we should do the migration at all
    * @param accountId Account Id used to determine if the feature has been enabled
    * @param sisCourseId Sis Course Id
    * @param templateUrl Url that will be used to fetch the template
    * @param forceApply Should the template be applied, ignoring other conditions
    */
   public void checkAndDoImsCcImport(String courseId, String termId, String accountId, String sisCourseId, String templateUrl, boolean forceApply) {
      TemplatedCourse templatedCourse = getTemplatedCourse(courseId);
      if (templatedCourse != null) {
         // Make sure the status is current before continuing
         updateMigrationStatusForCourse(templatedCourse);
      }

      // Only need to try and run a migration (applying template) if it has not ever been done for the course,
      // or if the previous attempt was an error
      // Or if the forceApply flag is set
      if (templatedCourse == null || ContentMigrationHelper.STATUS.ERROR.name().equals(templatedCourse.getStatus()) || forceApply) {
         log.info("Applying template to course " + courseId + " (" + sisCourseId + ")");
         ContentMigration cm = contentMigrationService.importCCIntoCourse(courseId, null, templateUrl);
         ContentMigrationStatus cms = new ContentMigrationStatus();
         cms.setContentMigrationId(cm.getId());
         cms.setStatus(ContentMigrationHelper.translateStatus(cm.getWorkflowState()).name());

         if (templatedCourse == null) {
            templatedCourse = new TemplatedCourse(courseId, sisCourseId, termId, ContentMigrationHelper.STATUS.PENDING.name());
         } else {
            templatedCourse.setStatus(ContentMigrationHelper.STATUS.PENDING.name());
         }
         templatedCourse.addContentMigrations(cms);
         saveTemplatedCourse(templatedCourse);
      } else {
         log.info("Not applying template to course " + courseId + " (" + sisCourseId + ") because a template has previously been applied.");
      }
   }

   public void updateMigrationStatusForCourses(List<TemplatedCourse> templatedCourses) {
      templatedCourses.forEach(this::updateMigrationStatusForCourse);
   }

   public void updateMigrationStatusForCourse(TemplatedCourse templatedCourse) {
      // Get all migration statuses for the canvas course
      List<ContentMigration> migrationStatuses = contentMigrationService.getMigrationStatuses(templatedCourse.getCourseId(), null);

      // Turn into a map so each individual one can be accessed
      Map<String, ContentMigration> statusMap = migrationStatuses.stream().collect(Collectors.toMap(ContentMigration::getId, status -> status, (a, b) -> b));

      List<ContentMigrationStatus> contentMigrationStatuses = templatedCourse.getContentMigrations();

      // if there are no migration statuses, don't bother doing any of this stuff since it will likely throw an error
      if (contentMigrationStatuses.size() > 0) {
         // Only care about the PENDING ones
         List<ContentMigrationStatus> filteredStatuses = contentMigrationStatuses.stream()
               .filter(cms -> cms.getStatus().equals(ContentMigrationHelper.STATUS.PENDING.name()))
               .collect(Collectors.toList());

         boolean saveTemplatedCourse = false;
         for (ContentMigrationStatus status : filteredStatuses) {
            ContentMigration canvasContentMigration = statusMap.get(status.getContentMigrationId());
            if (canvasContentMigration != null) {
               // Get our internal status value from the canvas value
               ContentMigrationHelper.STATUS translatedCanvasStatus = ContentMigrationHelper.translateStatus(canvasContentMigration.getWorkflowState());
               status.setStatus(translatedCanvasStatus.name());
               saveTemplatedCourse = true;
            }
         }

         // if the id from the canvasContentMigration doesn't map, don't bother slowing things down with saves
         // this is likely only a scenario on test environments, but slows things down quite a bit!
         if (saveTemplatedCourse) {
            // Set the status on the templatedCourse to match the last one in the list (most recent attempt)
            templatedCourse.setStatus(contentMigrationStatuses.get(contentMigrationStatuses.size() - 1).getStatus());
            saveTemplatedCourse(templatedCourse);
         }
      }
   }
}
