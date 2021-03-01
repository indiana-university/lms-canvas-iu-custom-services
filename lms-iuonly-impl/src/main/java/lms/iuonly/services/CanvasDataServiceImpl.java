package lms.iuonly.services;

import io.swagger.annotations.Api;
import lms.iuonly.exceptions.CanvasDataServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by chmaurer on 11/10/15.
 */
@RestController
@RequestMapping("/canvasdata")
@Slf4j
@Api(tags = "canvasData")
public class CanvasDataServiceImpl extends BaseService {

    @Autowired
    DataSource dataSource;

//    @Autowired
//    private EmailService emailService = null;

//    @Autowired
//    private PropertiesService propertiesService = null;

    /**
     * Validate that we have a good connection to canvasdata
     * @param conn
     * @throws CanvasDataServiceException
     */
    private void validateConnection(Connection conn) throws CanvasDataServiceException {
        boolean valid = false;
        try {
            if (conn != null && conn.isValid(5)) {
                valid = true;
            }
        } catch (SQLException e) {
            log.error("Error validating canvasdata connection", e);
        }
        if (!valid) {
            String message = "Null or invalid connection to canvasdata";
            log.error(message);
//            String emailAddress = propertiesService.getCanvasDataServiceFailureEmailAddress();
//            String subject = emailService.getStandardHeader() + " Null or invalid connection to canvasdata - " + propertiesService.getCanvasDataProvider();
//            try {
//                emailService.sendEmail(emailAddress, subject, subject, false);
//            } catch (LmsEmailTooBigException e) {
//                // since this isn't using an attachment this exception should never be thrown
//            }
            throw new CanvasDataServiceException(message);
        }
    }
//
//    @GetMapping("/whatever")
//    @PreAuthorize("#oauth2.hasScope('" + READ_SCOPE + "')")
//    public WithdrawnStudentGradesWrapper getStudentCourseInfo(String canvasCourseId) throws CanvasDataServiceException {
//        String sql = "select " +
//                "    user_dim.canvas_id AS canvas_user_id," +
//                "    user_dim.sortable_name AS user_sortable_name, " +
//                "    pseudonym_dim.unique_name AS sis_login_id, " +
//                "    course_section_dim.name AS sis_section_id, " +
//                "    assignment_dim.id AS assignment_id, " +
//                "    assignment_dim.title AS assignment_title, " +
//                "    assignment_dim.points_possible AS points_possible, " +
//                "    assignment_dim.grading_type AS grading_type, " +
//                "    assignment_dim.due_at AS due_date, " +
//                "    submission_dim.grade AS assignment_grade, " +
//                "    enrollment_fact.computed_final_score AS final_score, " +
//                "    enrollment_fact.computed_current_score AS current_score " +
//                "FROM course_dim course_dim " +
//                "  INNER JOIN course_section_dim course_section_dim ON (course_dim.id = course_section_dim.course_id) " +
//                "  INNER JOIN enrollment_dim enrollment_dim ON (course_section_dim.id = enrollment_dim.course_section_id) " +
//                "  INNER JOIN enrollment_fact enrollment_fact ON (enrollment_dim.id = enrollment_fact.enrollment_id) " +
//                "  INNER JOIN user_dim user_dim ON (enrollment_fact.user_id = user_dim.id) " +
//                "  INNER JOIN assignment_dim assignment_dim ON (course_dim.id = assignment_dim.course_id) " +
//                "  INNER JOIN submission_fact submission_fact ON ((assignment_dim.id = submission_fact.assignment_id) AND (user_dim.id = submission_fact.user_id)) " +
//                "  INNER JOIN submission_dim submission_dim ON (submission_fact.submission_id = submission_dim.id) " +
//                "  INNER JOIN pseudonym_dim pseudonym_dim ON (user_dim.id = pseudonym_dim.user_id) " +
//                "where course_dim.canvas_id = ? " +
//                "  AND enrollment_dim.type = 'StudentEnrollment' " +
//                "  AND enrollment_dim.workflow_state in ('deleted','inactive') " +
//                "ORDER BY user_dim.sortable_name, assignment_dim.due_at, assignment_dim.title";
//
//        PreparedStatement ps = null;
//        ResultSet rs = null;
//        Connection conn = getConnection();
//        validateConnection(conn);
//
//        WithdrawnStudentGradesWrapper withdrawnStudentGradesWrapper = new WithdrawnStudentGradesWrapper();
//
//        Map<String, WithdrawnStudentGrades> dataMap = new HashMap<>();
//        Map<String, AssignmentInfo> assignmentMap = new HashMap<>();
//
//        DecimalFormat gradeFormat = new DecimalFormat("#.##");
//
//        try {
//            ps = conn.prepareStatement(sql);
//            ps.setString(1, canvasCourseId);
//            rs = ps.executeQuery();
//
//            while (rs.next()) {
//                String sisLoginId = rs.getString("sis_login_id");
//                String assignmentId = rs.getString("assignment_id");
//
//                WithdrawnStudentGrades withdrawnStudentGrades = dataMap.get(sisLoginId);
//                if (withdrawnStudentGrades == null) {
//                    //Doesn't exist yet...create it
//                    withdrawnStudentGrades = new WithdrawnStudentGrades();
//                    withdrawnStudentGrades.setCanvasUserId(rs.getString("canvas_user_id"));
//                    withdrawnStudentGrades.setUserSortableName(rs.getString("user_sortable_name"));
//                    withdrawnStudentGrades.setUserNetworkId(sisLoginId);
//                    withdrawnStudentGrades.setSectionSisId(rs.getString("sis_section_id"));
//                    String currentScoreString = rs.getString("current_score");
//                    String finalScoreString = rs.getString("final_score");
//
//                    if (currentScoreString != null && !"".equals(currentScoreString)) {
//                        Double currentScore = new Double(currentScoreString);
//                        withdrawnStudentGrades.setCurrentScore(gradeFormat.format(currentScore));
//                    }
//
//                    if (finalScoreString != null && !"".equals(finalScoreString)) {
//                        Double finalScore = new Double(finalScoreString);
//                        withdrawnStudentGrades.setFinalScore(gradeFormat.format(finalScore));
//                    }
//
//                    withdrawnStudentGrades.setStudentAssignments(new HashMap<>());
//                    dataMap.put(sisLoginId, withdrawnStudentGrades);
//                }
//
//                AssignmentInfo assignmentInfo = assignmentMap.get(assignmentId);
//                if (assignmentInfo == null) {
//
//                    assignmentInfo = new AssignmentInfo();
//                    assignmentInfo.setAssignmentId(assignmentId);
//                    assignmentInfo.setAssignmentTitle(rs.getString("assignment_title"));
//                    assignmentInfo.setGradingType(rs.getString("grading_type"));
//                    assignmentInfo.setPointsPossible(rs.getString("points_possible"));
//                    assignmentInfo.setDueDate(rs.getDate("due_date"));
//                    assignmentMap.put(assignmentId, assignmentInfo);
//                }
//
//                StudentAssignment studentAssignment = new StudentAssignment();
//                studentAssignment.setSisLoginId(sisLoginId);
//                studentAssignment.setAssignmentId(assignmentId);
//                String assignmentGradeString = rs.getString("assignment_grade");
//
//                if (assignmentGradeString != null && !"".equals(assignmentGradeString)) {
//                    // check to see if the grade is a number first before trying to format it as a double
//                    boolean numberCheck = NumberUtils.isNumber(assignmentGradeString);
//
//                    if (numberCheck) {
//                        Double assignmentGrade = new Double(assignmentGradeString);
//                        studentAssignment.setGrade(gradeFormat.format(assignmentGrade));
//                    }
//                    else {
//                        // Not a number, so set the string as is
//                        studentAssignment.setGrade(assignmentGradeString);
//                    }
//                }
//
//                withdrawnStudentGrades.getStudentAssignments().put(assignmentId, studentAssignment);
//            }
//
//            List<AssignmentInfo> assignmentList = new ArrayList<>(assignmentMap.values());
//
//            Comparator<AssignmentInfo> dateComparator = Comparator.comparing(AssignmentInfo::getDueDate, Comparator.nullsFirst(Comparator.naturalOrder()));
//            Comparator<AssignmentInfo> titleComparator = Comparator.comparing(AssignmentInfo::getAssignmentTitle, Comparator.nullsFirst(Comparator.naturalOrder()));
//
//            assignmentList.sort(dateComparator.thenComparing(titleComparator));
//
//            List<WithdrawnStudentGrades> gradesInfoList = new ArrayList(dataMap.values());
//
//            //sorting by the canvas user id, then section, then networkid desc
//            Comparator<WithdrawnStudentGrades> canvasUserIdComparator = Comparator.comparing(WithdrawnStudentGrades::getCanvasUserId, Comparator.nullsFirst(Comparator.naturalOrder()));
//            Comparator<WithdrawnStudentGrades> sectionComparator = Comparator.comparing(WithdrawnStudentGrades::getSectionSisId, Comparator.nullsFirst(Comparator.naturalOrder()));
//            Comparator<WithdrawnStudentGrades> sisUserIdComparator = Comparator.comparing(WithdrawnStudentGrades::getUserNetworkId, Comparator.nullsFirst(Comparator.reverseOrder()));
//            gradesInfoList.sort(canvasUserIdComparator.thenComparing(sectionComparator).thenComparing(sisUserIdComparator));
//
//            List<WithdrawnStudentGrades> updatedWithdrawnStudentGradesList = removeDuplicatesForGradesReport(gradesInfoList);
//
//            withdrawnStudentGradesWrapper.setAssignmentList(assignmentList);
//            withdrawnStudentGradesWrapper.setWithdrawnStudentGrades(updatedWithdrawnStudentGradesList);
//
//        } catch (SQLException e) {
//            log.error("uh oh", e);
//            throw new IllegalStateException(e);
//        } finally {
//            close(conn, ps, rs);
//        }
//        return withdrawnStudentGradesWrapper;
//    }
//
//    public List<Enrollment> getRosterStatusInfo(String canvasCourseId) throws CanvasDataServiceException {
//        String sql = "select " +
//                "    user_dim.canvas_id AS canvas_user_id," +
//                "    user_dim.sortable_name AS name, " +
//                "    pseudonym_dim.unique_name AS username, " +
//                "    role_dim.name AS role, " +
//                "    course_section_dim.name AS section, " +
//                "    enrollment_dim.workflow_state AS status, " +
//                "    enrollment_dim.created_at As createdDate, " +
//                "    enrollment_dim.updated_at AS updatedDate " +
//                "FROM course_dim course_dim " +
//                "  INNER JOIN course_section_dim course_section_dim ON (course_dim.id = course_section_dim.course_id) " +
//                "  INNER JOIN enrollment_dim enrollment_dim ON (course_section_dim.id = enrollment_dim.course_section_id) " +
//                "  INNER JOIN enrollment_fact enrollment_fact ON (enrollment_dim.id = enrollment_fact.enrollment_id) " +
//                "  INNER JOIN user_dim user_dim ON (enrollment_fact.user_id = user_dim.id) " +
//                "  INNER JOIN pseudonym_dim pseudonym_dim ON (user_dim.id = pseudonym_dim.user_id) " +
//                "  INNER JOIN role_dim role_dim ON (enrollment_dim.role_id = role_dim.id) " +
//                "where course_dim.canvas_id = ? and user_dim.sortable_name != 'Student, Test' " +
//                "  AND pseudonym_dim.workflow_state = 'active' " +
//                "ORDER BY user_dim.sortable_name, user_dim.canvas_id, course_section_dim.name, role_dim.name desc, pseudonym_dim.unique_name desc";
//
//        PreparedStatement ps = null;
//        ResultSet rs = null;
//        Connection conn = getConnection();
//        validateConnection(conn);
//
//        List<Enrollment> rosterStatusInfoList = new ArrayList<>();
//        List<Enrollment> updatedRosterStatusInfoList = new ArrayList<>();
//
//        try {
//            ps = conn.prepareStatement(sql);
//            ps.setString(1, canvasCourseId);
//            rs = ps.executeQuery();
//
//            while (rs.next()) {
//                Enrollment enrollment = new Enrollment();
//                enrollment.setCanvasUserId(rs.getString("canvas_user_id"));
//                enrollment.setName(rs.getString("name"));
//                enrollment.setUsername(rs.getString("username"));
//                enrollment.setRole(rs.getString("role"));
//                enrollment.setSection(rs.getString("section"));
//                enrollment.setStatus(rs.getString("status"));
//                enrollment.setCreatedDate(rs.getTimestamp("createdDate"));
//                enrollment.setUpdatedDate(rs.getTimestamp("updatedDate"));
//
//                rosterStatusInfoList.add(enrollment);
//            }
//
//            updatedRosterStatusInfoList = removeDuplicates(rosterStatusInfoList);
//            log.info("Found " + updatedRosterStatusInfoList.size() + " enrollment records for Canvas course " + canvasCourseId);
//
//        } catch (SQLException e) {
//            log.error("uh oh", e);
//            throw new IllegalStateException(e);
//        } finally {
//            close(conn, ps, rs);
//        }
//        return updatedRosterStatusInfoList;
//    }
//
//    private List<Enrollment> removeDuplicates(List<Enrollment> rosterStatusInfoList) {
//        List<Enrollment> updatedRosterStatusInfoList = new ArrayList<>();
//        String reportInfoUserId = "";
//        String reportInfoRole = "";
//        String reportInfoSection = "";
//        String reportInfoStatus = "";
//
//        for (Enrollment enrollment : rosterStatusInfoList) {
//            String userId = enrollment.getCanvasUserId();
//            String role = enrollment.getRole();
//            String section = enrollment.getSection();
//            String status = enrollment.getStatus();
//
//            //if the user is the new user, definitely add that user's record in.
//            // if the user the same, check role, section, status. If one or any of them is different, add the record in,
//            // otherwise, (means it is the duplicate), ignore it.
//            if ((userId != null) && !userId.equals(reportInfoUserId)) {
//                updatedRosterStatusInfoList.add(enrollment);
//
//                reportInfoUserId = userId;
//                reportInfoRole = role;
//                reportInfoSection = section;
//                reportInfoStatus = status;
//            } else if ((role != null && !role.equals(reportInfoRole))
//                    || (section != null && !section.equals(reportInfoSection))
//                    || (status != null && !status.equals(reportInfoStatus))) {
//                updatedRosterStatusInfoList.add(enrollment);
//
//                reportInfoUserId = userId;
//                reportInfoRole = role;
//                reportInfoSection = section;
//                reportInfoStatus = status;
//            } else {
//                continue;
//            }
//        }
//        return updatedRosterStatusInfoList;
//    }
//
//    private List<WithdrawnStudentGrades> removeDuplicatesForGradesReport(List<WithdrawnStudentGrades> withdrawStudentGradesList) {
//        List<WithdrawnStudentGrades> updatedGradesInfoList = new ArrayList<>();
//        String gradeReportInfoUserId = "";
//        String gradeReportInfoSection = "";
//
//        for (WithdrawnStudentGrades withdrawnStudentGrades : withdrawStudentGradesList) {
//            String userId = withdrawnStudentGrades.getCanvasUserId();
//            String section = withdrawnStudentGrades.getSectionSisId();
//            //if the user is the new user, definitely add that user's record in.
//            // if the user is the same user, check section, if section is different, add the record in,
//            // otherwise, (means it is the duplicate), ignore it.
//            if((userId != null) && !userId.equals(gradeReportInfoUserId)) {
//                updatedGradesInfoList.add(withdrawnStudentGrades);
//                gradeReportInfoUserId = userId;
//                gradeReportInfoSection = section;
//            } else if ((section != null) && !section.equals(gradeReportInfoSection)) {
//                updatedGradesInfoList.add(withdrawnStudentGrades);
//                gradeReportInfoUserId = userId;
//                gradeReportInfoSection = section;
//            } else {
//                continue;
//            }
//        }
//        return updatedGradesInfoList;
//    }
//
//    public Map<String, List<Enrollment>> getDeletedEnrollments(List<String> canvasCourseIds, boolean includeInactive) throws CanvasDataServiceException {
//        Map<String, List<Enrollment>> courseEnrollmentMap = new HashMap<>();
//
//        if (canvasCourseIds != null && canvasCourseIds.size() > 0) {
//            String sql = "select distinct " +
//                    "    user_dim.canvas_id AS canvas_user_id," +
//                    "    user_dim.sortable_name AS name, " +
//                    "    role_dim.name AS role, " +
//                    "    course_section_dim.name AS section, " +
//                    "    enrollment_dim.workflow_state AS status, " +
//                    "    enrollment_dim.created_at As createdDate, " +
//                    "    enrollment_dim.updated_at AS updatedDate, " +
//                    "    course_dim.canvas_id as canvasCourseId, " +
//                    "    communication_channel_dim.address as default_email, " +
//                    "    pseudonym_dim.unique_name as user_name " +
//                    "FROM course_dim course_dim " +
//                    "  INNER JOIN course_section_dim course_section_dim ON (course_dim.id = course_section_dim.course_id) " +
//                    "  INNER JOIN enrollment_dim enrollment_dim ON (course_section_dim.id = enrollment_dim.course_section_id)" +
//                    "  INNER JOIN enrollment_fact enrollment_fact ON (enrollment_dim.id = enrollment_fact.enrollment_id)" +
//                    "  INNER JOIN user_dim user_dim ON (enrollment_fact.user_id = user_dim.id)" +
//                    "  INNER JOIN pseudonym_dim ON (enrollment_dim.user_id = pseudonym_dim.user_id)" +
//                    "  INNER JOIN role_dim role_dim ON (enrollment_dim.role_id = role_dim.id)" +
//                    "  LEFT OUTER JOIN communication_channel_dim on (communication_channel_dim.user_id = user_dim.id " +
//                    "        and communication_channel_dim.position = 1 and communication_channel_dim.type = 'email' and communication_channel_dim.workflow_state = 'active') " +
//                    "where " + LmsSqlUtils.buildWhereInClause("course_dim.canvas_id", canvasCourseIds, false) +
//                    "  and (enrollment_dim.workflow_state = 'deleted' ";
//
//            if (includeInactive) {
//                sql += " or enrollment_dim.workflow_state = 'inactive' ";
//            }
//
//            sql += ") ORDER BY user_dim.sortable_name, user_dim.canvas_id, course_section_dim.name, role_dim.name desc";
//
//            log.debug(sql);
//
//            PreparedStatement ps = null;
//            ResultSet rs = null;
//            Connection conn = getConnection();
//            validateConnection(conn);
//
//            try {
//                ps = conn.prepareStatement(sql);
//                rs = ps.executeQuery();
//
//                while (rs.next()) {
//                    Enrollment enrollment = new Enrollment();
//                    enrollment.setCanvasUserId(rs.getString("canvas_user_id"));
//                    enrollment.setName(rs.getString("name"));
////                enrollment.setSisUserId(rs.getString("sisUserId"));
//                    enrollment.setRole(rs.getString("role"));
//                    enrollment.setSection(rs.getString("section"));
//                    enrollment.setStatus(rs.getString("status"));
//                    enrollment.setCreatedDate(rs.getTimestamp("createdDate"));
//                    enrollment.setUpdatedDate(rs.getTimestamp("updatedDate"));
//                    enrollment.setPrimaryEmail(rs.getString("default_email"));
//                    enrollment.setUsername(rs.getString("user_name"));
//                    String canvasCourseId = rs.getString("canvasCourseId");
//
//                    List<Enrollment> enrollments = courseEnrollmentMap.get(canvasCourseId);
//                    if (enrollments == null) {
//                        enrollments = new ArrayList<>();
//                        courseEnrollmentMap.put(canvasCourseId, enrollments);
//                    }
//
//                    enrollments.add(enrollment);
//                }
//            } catch (SQLException e) {
//                log.error("uh oh", e);
//                throw new IllegalStateException(e);
//            } finally {
//                close(conn, ps, rs);
//            }
//        }
//        return courseEnrollmentMap;
//    }
//
//    public List<Course> getCourses(List<String> canvasCourseIds) throws CanvasDataServiceException {
//        List<Course> courses = new ArrayList<>();
//
//        if (canvasCourseIds != null && canvasCourseIds.size() > 0) {
//
//            String sql = "select " +
//                    "    course_dim.canvas_id as canvasCourseId, " +
//                    "    course_dim.account_id as accountId, " +
//                    "    course_dim.enrollment_term_id as enrollmentTermId, " +
//                    "    course_dim.name as courseName, " +
//                    "    course_dim.code as courseCode, " +
//                    "    course_dim.sis_source_id as sisCourseId, " +
//                    "    course_dim.start_at as startDate, " +
//                    "    course_dim.conclude_at as endDate, " +
//                    "    enrollment_term_dim.canvas_id as canvasTermId, " +
//                    "    enrollment_term_dim.name as termName, " +
//                    "    enrollment_term_dim.date_start as termStart, " +
//                    "    enrollment_term_dim.date_end as termEnd, " +
//                    "    enrollment_term_dim.sis_source_id as sisTermId " +
//                    "FROM course_dim course_dim " +
//                    "  INNER JOIN enrollment_term_dim enrollment_term_dim ON (enrollment_term_dim.id = course_dim.enrollment_term_id) " +
//                    "where " + LmsSqlUtils.buildWhereInClause("course_dim.canvas_id", canvasCourseIds, false, false) +
//                    " ORDER BY course_dim.canvas_id";
//
//            PreparedStatement ps = null;
//            ResultSet rs = null;
//            Connection conn = getConnection();
//            validateConnection(conn);
//
//            try {
//                ps = conn.prepareStatement(sql);
//                log.debug(sql);
//                rs = ps.executeQuery();
//
//                Map<String, EnrollmentTerm> enrollmentTermMap = new HashMap<>();
//
//                while (rs.next()) {
//                    Course course = new Course();
//                    course.setCanvasCourseId(rs.getString("canvasCourseId"));
//                    course.setAccountId(rs.getString("accountId"));
//
//                    course.setTitle(rs.getString("courseName"));
//                    course.setCode(rs.getString("courseCode"));
//                    course.setSisCourseId(rs.getString("sisCourseId"));
//                    course.setStartDate(rs.getTimestamp("startDate"));
//                    course.setEndDate(rs.getTimestamp("endDate"));
//
//                    String enrollmentTermId = rs.getString("enrollmentTermId");
//                    EnrollmentTerm enrollmentTerm = enrollmentTermMap.get(enrollmentTermId);
//                    if (enrollmentTerm == null) {
//                        enrollmentTerm = new EnrollmentTerm();
//                        enrollmentTerm.setEnrollmentTermId(enrollmentTermId);
//                        enrollmentTerm.setCanvasTermId(rs.getString("canvasTermId"));
//                        enrollmentTerm.setTitle(rs.getString("termName"));
//                        enrollmentTerm.setSisTermId(rs.getString("sisTermId"));
//                        enrollmentTerm.setStartDate(rs.getTimestamp("termStart"));
//                        enrollmentTerm.setEndDate(rs.getTimestamp("termEnd"));
//                        enrollmentTermMap.put(enrollmentTermId, enrollmentTerm);
//                    }
//
//                    course.setEnrollmentTerm(enrollmentTerm);
//                    courses.add(course);
//                }
//
//            } catch (SQLException e) {
//                log.error("query error", e);
//                throw new IllegalStateException(e);
//            } finally {
//                close(conn, ps, rs);
//            }
//        }
//
//        return courses;
//    }
//
//    public List<CloseExpireCourse> getManuallyCreatedCoursesWithTerm() throws CanvasDataServiceException {
//        List<CloseExpireCourse> notificationCourses = new ArrayList<>();
//        String sql = "select distinct " +
//                "course_dim.canvas_id AS canvas_id, " +
//                "course_dim.name AS coursename, " +
//                "course_dim.conclude_at AS end_date, " +
//                "enrollment_term_dim.canvas_id AS canvas_term_id, " +
//                "communication_channel_dim.address AS emailAdress " +
//                "FROM course_dim course_dim " +
//                "INNER JOIN course_section_dim course_section_dim ON (course_dim.id = course_section_dim.course_id) " +
//                "INNER JOIN enrollment_dim enrollment_dim ON (course_section_dim.id = enrollment_dim.course_section_id) " +
//                "INNER JOIN enrollment_fact enrollment_fact ON (enrollment_dim.id = enrollment_fact.enrollment_id) " +
//                "INNER JOIN user_dim user_dim ON (enrollment_fact.user_id = user_dim.id) " +
//                "INNER JOIN pseudonym_dim pseudonym_dim ON (user_dim.id = pseudonym_dim.user_id) " +
//                "INNER JOIN role_dim role_dim ON (enrollment_dim.role_id = role_dim.id) " +
//                "INNER JOIN enrollment_term_dim enrollment_term_dim ON (course_dim.enrollment_term_id = enrollment_term_dim.id) " +
//                "INNER JOIN communication_channel_dim communication_channel_dim ON (communication_channel_dim.user_id = user_dim.id) " +
//                "where  course_dim.sis_source_id is null "  +
//                "and role_dim.base_role_type='TeacherEnrollment' " +
//                "and course_dim.workflow_state != 'deleted' " +
//                "and role_dim.workflow_state != 'deleted' " +
//                "and enrollment_dim.workflow_state != 'deleted' " +
//                "and communication_channel_dim.position=1 " +
//                "order by course_dim.canvas_id ";
//
//        PreparedStatement ps = null;
//        ResultSet rs = null;
//        Connection conn = getConnection();
//        validateConnection(conn);
//        try {
//            ps = conn.prepareStatement(sql);
//            rs = ps.executeQuery();
//            Calendar cal = Calendar.getInstance();
//            cal.setTimeZone(TimeZone.getTimeZone("UTC"));
//
//            while (rs.next()) {
//                CloseExpireCourse course = new CloseExpireCourse();
//                course.setCanvasCourseId(rs.getString("canvas_id"));
//                course.setCourseName(rs.getString("coursename"));
//                course.setEndDate(rs.getTimestamp("end_date", cal));
//                course.setTermId(rs.getString("canvas_term_id"));
//                course.setEmailAddress(rs.getString("emailAdress"));
//                notificationCourses.add(course);
//            }
//        } catch (SQLException e) {
//            log.error("uh oh", e);
//            throw new IllegalStateException(e);
//        } finally {
//            close(conn, ps, rs);
//        }
//        return notificationCourses;
//    }
//
//    public User getActiveUserByIuUsername(String iuUsername) throws CanvasDataServiceException {
//        User user = null;
//
//        if (iuUsername == null || iuUsername.isEmpty()) {
//            return user;
//        }
//
//        final String sql  =
//                "SELECT  user_dim.canvas_id AS canvas_id, pseudonym_dim.unique_name AS iu_username, pseudonym_dim.workflow_state AS status " +
//                        "FROM canvas_warehouse.user_dim user_dim " +
//                        "INNER JOIN canvas_warehouse.pseudonym_dim pseudonym_dim ON user_dim.id = pseudonym_dim.user_id " +
//                        "WHERE pseudonym_dim.unique_name = '" + iuUsername + "' and pseudonym_dim.workflow_state = 'active'";
//
//        PreparedStatement ps = null;
//        ResultSet rs = null;
//        Connection conn = getConnection();
//        validateConnection(conn);
//        try {
//            ps = conn.prepareStatement(sql);
//            rs = ps.executeQuery();
//
//            while (rs.next()) {
//                user = new User();
//                user.setCanvasId(rs.getString("canvas_id"));
//                user.setIuUsername(rs.getString("iu_username"));
//                user.setStatus(rs.getString("status"));
//            }
//        } catch (SQLException e) {
//            log.error("uh oh", e);
//            throw new IllegalStateException(e);
//        } finally {
//            close(conn, ps, rs);
//        }
//        return user;
//    }

    @GetMapping("/activemap")
    @PreAuthorize("#oauth2.hasScope('" + READ_SCOPE + "')")
    public Map<String, String> getActiveUserMapOfIuUsernameToCanvasId(@RequestParam(value = "iuUsernames") List<String> iuUsernames) throws CanvasDataServiceException {
        Map<String, String> userMap = new HashMap<>();

        if (iuUsernames == null || iuUsernames.isEmpty()) {
            return userMap;
        }

        // denodo can't seem to handle queries over 1000 in the where.  Not jsut in the IN but total (even OR'd wheres)
        // so we'll go through the list 1000 at a time to build the map

        final int MAX_LIST_SIZE = 1000;

        int startIndex = 0;


        while (startIndex < iuUsernames.size()) {
            int endIndex = startIndex + MAX_LIST_SIZE;

            if (endIndex > iuUsernames.size()) {
                endIndex = iuUsernames.size();
            }

            List<String> subListIuUsernames = iuUsernames.subList(startIndex, endIndex);

            String whereUsernameClause = LmsSqlUtils.buildWhereInClause("pseudonym_dim.unique_name", subListIuUsernames, false, true);


            String sql =
                    "SELECT  user_dim.canvas_id AS canvas_id, pseudonym_dim.unique_name AS iu_username, pseudonym_dim.workflow_state AS status " +
                            "FROM canvas_warehouse.user_dim user_dim " +
                            "INNER JOIN canvas_warehouse.pseudonym_dim pseudonym_dim ON user_dim.id = pseudonym_dim.user_id " +
                            "WHERE " + whereUsernameClause + " and pseudonym_dim.workflow_state = 'active'";

            PreparedStatement ps = null;
            ResultSet rs = null;
            Connection conn = getConnection();
            validateConnection(conn);
            try {
                ps = conn.prepareStatement(sql);

                rs = ps.executeQuery();

                while (rs.next()) {
                    String username = rs.getString("iu_username");
                    String canvasId = rs.getString("canvas_id");

                    if (username != null && !username.isEmpty() && canvasId != null && !canvasId.isEmpty()) {
                        userMap.put(username, canvasId);
                    }
                }
            } catch (SQLException e) {
                log.error("uh oh", e);
                throw new IllegalStateException(e);
            } finally {
                close(conn, ps, rs);
            }

            startIndex = endIndex;
        }


        return userMap;
    }

    private Connection getConnection() {
        try {
            if (dataSource != null) {
                return dataSource.getConnection();
            }
        } catch (SQLException sqle) {
            log.error("Error getting connection", sqle);
        }

        return null;
    }

    private void close(Connection connection, Statement statement, ResultSet resultSet) {
        try {
            if (resultSet != null) {
                resultSet.close();
            }
        } catch (SQLException sqle) {
            log.error("Error closing resultset ", sqle);
        }

        try {
            if (statement != null) {
                statement.close();
            }
        } catch (SQLException sqle) {
            log.error("Error closing statement ", sqle);
        }

        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException sqle) {
            log.error("Error closing connection ", sqle);
        }
    }

}
