package lms.iuonly.services;

import io.swagger.annotations.Api;
import lms.iuonly.model.coursetemplating.TemplatedCourse;
import lms.iuonly.repository.TemplatedCourseRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/courseTemplating")
@Slf4j
@Api(tags = "courseTemplating")
public class CourseTemplatingServiceImpl extends BaseService {

   @Autowired
   private TemplatedCourseRepository templatedCourseRepository;

   @GetMapping("/templates/{sisCourseId}")
   @PreAuthorize("#oauth2.hasScope('" + READ_SCOPE + "')")
   public List<TemplatedCourse> findBySisCourseId(@PathVariable("sisCourseId") String sisCourseId) {
      return templatedCourseRepository.findBySisCourseId(sisCourseId);
   }

   @GetMapping("/template/{courseId}")
   @PreAuthorize("#oauth2.hasScope('" + WRITE_SCOPE + "')")
   public TemplatedCourse getTemplatedCourse(@PathVariable("courseId") String courseId) {
      return templatedCourseRepository.findById(courseId).orElse(null);
   }

   @GetMapping("/save")
   @PreAuthorize("#oauth2.hasScope('" + WRITE_SCOPE + "')")
   public TemplatedCourse saveTemplatedCourse(@RequestBody TemplatedCourse templatedCourse) {
      return templatedCourseRepository.save(templatedCourse);
   }

   public void deleteTemplatedCourse(TemplatedCourse templatedCourse) {
      templatedCourseRepository.delete(templatedCourse);
   }
}
