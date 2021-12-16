package edu.iu.uits.lms.iuonly.services;

import edu.iu.uits.lms.iuonly.model.coursetemplating.TemplatedCourse;
import edu.iu.uits.lms.iuonly.repository.TemplatedCourseRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class CourseTemplatingServiceImpl {

   @Autowired
   private TemplatedCourseRepository templatedCourseRepository;

   public List<TemplatedCourse> findBySisCourseId(String sisCourseId) {
      return templatedCourseRepository.findBySisCourseId(sisCourseId);
   }

   public TemplatedCourse getTemplatedCourse(String courseId) {
      return templatedCourseRepository.findById(courseId).orElse(null);
   }

   public TemplatedCourse saveTemplatedCourse(TemplatedCourse templatedCourse) {
      return templatedCourseRepository.save(templatedCourse);
   }
}
