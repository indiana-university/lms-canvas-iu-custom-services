package edu.iu.uits.lms.iuonly.services;

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
