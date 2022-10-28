package edu.iu.uits.lms.iuonly.services;

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

   public void audit(String courseId, HierarchyResource templateForCourse, String activityType, String userLoginId) {
      Course course = courseService.getCourse(courseId);
      audit(course, templateForCourse, activityType, userLoginId);
   }

   public void audit(Course course, HierarchyResource templateForCourse, String activityType, String userLoginId) {
      AuditInfo ai = AuditInfo.builder()
            .canvasCourseId(course.getId())
            .canvasTermId(course.getTerm().getSisTermId())
            .canvasCourseCode(course.getCourseCode())
            .canvasTermName(course.getTerm().getName())
            .templateId(templateForCourse.getId().toString())
            .templateName(templateForCourse.getDisplayName())
            .templateFileName(templateForCourse.getStoredFile().getDisplayName())
            .templateSponsor(templateForCourse.getSponsor())
            .templateNode(templateForCourse.getNode())
            .activityType(activityType)
            .activityUser(userLoginId)
            .build();
      audit(ai);
   }
}
