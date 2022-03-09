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

import edu.iu.uits.lms.iuonly.model.coursetemplating.TemplatedCourse;

import java.util.List;

public interface CourseTemplatingService {

   /**
    * Trigger a content migration, which will setup the course from the template
    * @param courseId Canvas Course id
    * @param termId Term Id used to determine if we should do the migration at all
    * @param accountId Account Id used to determine if the feature has been enabled
    * @param sisCourseId Sis Course Id
    * @param templateUrl Url that will be used to fetch the template
    * @param forceApply Should the template be applied, ignoring other conditions
    */
   void checkAndDoImsCcImport(String courseId, String termId, String accountId, String sisCourseId, String templateUrl, boolean forceApply);

   /**
    * Update the migration status for a list of courses
    * @param templatedCourses List of courses
    */
   void updateMigrationStatusForCourses(List<TemplatedCourse> templatedCourses);

   /**
    * Update the migration status for a given course
    * @param templatedCourse course
    */
   void updateMigrationStatusForCourse(TemplatedCourse templatedCourse);
}
