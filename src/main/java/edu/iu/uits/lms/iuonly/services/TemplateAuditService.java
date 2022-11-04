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

import edu.iu.uits.lms.canvas.model.Course;
import edu.iu.uits.lms.canvas.services.CourseService;
import edu.iu.uits.lms.iuonly.model.HierarchyResource;
import edu.iu.uits.lms.iuonly.model.coursetemplating.AuditInfo;
import edu.iu.uits.lms.iuonly.repository.AuditInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TemplateAuditService {

   @Autowired
   private AuditInfoRepository auditInfoRepository;

   @Autowired
   private CourseService courseService;

   public void audit(AuditInfo auditInfo) {
      auditInfoRepository.save(auditInfo);
   }

   public void audit(String courseId, String sourceCanvasCourseId, String activityType, String userLoginId) {
      Course course = courseService.getCourse(courseId);
      audit(course, null, sourceCanvasCourseId, activityType, userLoginId);
   }

   public void audit(String courseId, HierarchyResource templateForCourse, String activityType, String userLoginId) {
      Course course = courseService.getCourse(courseId);
      audit(course, templateForCourse, null, activityType, userLoginId);
   }

   public void audit(Course course, HierarchyResource templateForCourse, String activityType, String userLoginId) {
      audit(course, templateForCourse, null, activityType, userLoginId);
   }

   public void audit(Course course, HierarchyResource templateForCourse, String sourceCanvasCourseId, String activityType, String userLoginId) {
      AuditInfo.AuditInfoBuilder aib = AuditInfo.builder()
            .canvasCourseId(course.getId())
            .canvasTermId(course.getTerm().getSisTermId())
            .canvasCourseCode(course.getCourseCode())
            .canvasTermName(course.getTerm().getName())
            .sourceCanvasCourseId(sourceCanvasCourseId)
            .activityType(activityType)
            .activityUser(userLoginId);

      if (templateForCourse != null) {
         aib.templateId(templateForCourse.getId().toString())
               .templateName(templateForCourse.getDisplayName())
               .templateFileName(templateForCourse.getStoredFile().getDisplayName())
               .templateSponsor(templateForCourse.getSponsor())
               .templateNode(templateForCourse.getNode());
      }

      audit(aib.build());
   }
}
