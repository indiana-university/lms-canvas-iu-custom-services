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

import edu.iu.uits.lms.iuonly.exceptions.CanvasDataServiceException;
import edu.iu.uits.lms.iuonly.model.CloseExpireCourse;
import edu.iu.uits.lms.iuonly.model.Enrollment;
import edu.iu.uits.lms.iuonly.model.ListWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

/**
 * Created by chmaurer on 11/10/15.
 */
@Profile("denodo")
@Service
@Slf4j
public class CanvasDataServiceImpl {

    @Autowired
    @Qualifier("denododb")
    DataSource dataSource;

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
            //TODO Add in some sort of notification to alert team that denodo is having problems
            throw new CanvasDataServiceException(message);
        }
    }

    public Map<String, String> getActiveUserMapOfIuUsernameToCanvasId(ListWrapper iuUsernameWrapper) throws CanvasDataServiceException {
        Map<String, String> userMap = new HashMap<>();

        if (iuUsernameWrapper == null || iuUsernameWrapper.getListItems() == null || iuUsernameWrapper.getListItems().isEmpty()) {
            return userMap;
        }

        List<String> iuUsernames = iuUsernameWrapper.getListItems();

        // denodo can't seem to handle queries over 1000 in the where.  Not just in the IN but total (even OR'd wheres)
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

    public List<Enrollment> getRosterStatusInfo(String canvasCourseId) throws CanvasDataServiceException {
        String sql = "select " +
              "    user_dim.canvas_id AS canvas_user_id," +
              "    user_dim.sortable_name AS name, " +
              "    pseudonym_dim.unique_name AS username, " +
              "    role_dim.name AS role, " +
              "    course_section_dim.name AS section, " +
              "    enrollment_dim.workflow_state AS status, " +
              "    enrollment_dim.created_at As createdDate, " +
              "    enrollment_dim.updated_at AS updatedDate " +
              "FROM course_dim course_dim " +
              "  INNER JOIN course_section_dim course_section_dim ON (course_dim.id = course_section_dim.course_id) " +
              "  INNER JOIN enrollment_dim enrollment_dim ON (course_section_dim.id = enrollment_dim.course_section_id) " +
              "  INNER JOIN enrollment_fact enrollment_fact ON (enrollment_dim.id = enrollment_fact.enrollment_id) " +
              "  INNER JOIN user_dim user_dim ON (enrollment_fact.user_id = user_dim.id) " +
              "  INNER JOIN pseudonym_dim pseudonym_dim ON (user_dim.id = pseudonym_dim.user_id) " +
              "  INNER JOIN role_dim role_dim ON (enrollment_dim.role_id = role_dim.id) " +
              "where course_dim.canvas_id = ? and user_dim.sortable_name != 'Student, Test' " +
              "  AND pseudonym_dim.workflow_state = 'active' " +
              "ORDER BY user_dim.sortable_name, user_dim.canvas_id, course_section_dim.name, role_dim.name desc, pseudonym_dim.unique_name desc";

        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection conn = getConnection();
        validateConnection(conn);

        List<Enrollment> rosterStatusInfoList = new ArrayList<>();
        List<Enrollment> updatedRosterStatusInfoList = new ArrayList<>();

        try {
            ps = conn.prepareStatement(sql);
            ps.setString(1, canvasCourseId);
            rs = ps.executeQuery();

            while (rs.next()) {
                Enrollment enrollment = new Enrollment();
                enrollment.setCanvasUserId(rs.getString("canvas_user_id"));
                enrollment.setName(rs.getString("name"));
                enrollment.setUsername(rs.getString("username"));
                enrollment.setRole(rs.getString("role"));
                enrollment.setSection(rs.getString("section"));
                enrollment.setStatus(rs.getString("status"));
                enrollment.setCreatedDate(rs.getTimestamp("createdDate"));
                enrollment.setUpdatedDate(rs.getTimestamp("updatedDate"));

                rosterStatusInfoList.add(enrollment);
            }

            updatedRosterStatusInfoList = removeDuplicates(rosterStatusInfoList);
            log.info("Found " + updatedRosterStatusInfoList.size() + " enrollment records for Canvas course " + canvasCourseId);

        } catch (SQLException e) {
            log.error("error getting roster information for course " + canvasCourseId, e);
            throw new IllegalStateException(e);
        } finally {
            close(conn, ps, rs);
        }
        return updatedRosterStatusInfoList;
    }

    private List<Enrollment> removeDuplicates(List<Enrollment> rosterStatusInfoList) {
        List<Enrollment> updatedRosterStatusInfoList = new ArrayList<>();
        String reportInfoUserId = "";
        String reportInfoRole = "";
        String reportInfoSection = "";
        String reportInfoStatus = "";

        for (Enrollment enrollment : rosterStatusInfoList) {
            String userId = enrollment.getCanvasUserId();
            String role = enrollment.getRole();
            String section = enrollment.getSection();
            String status = enrollment.getStatus();

            //if the user is the new user, definitely add that user's record in.
            // if the user the same, check role, section, status. If one or any of them is different, add the record in,
            // otherwise, (means it is the duplicate), ignore it.
            if ((userId != null) && !userId.equals(reportInfoUserId)) {
                updatedRosterStatusInfoList.add(enrollment);

                reportInfoUserId = userId;
                reportInfoRole = role;
                reportInfoSection = section;
                reportInfoStatus = status;
            } else if ((role != null && !role.equals(reportInfoRole))
                  || (section != null && !section.equals(reportInfoSection))
                  || (status != null && !status.equals(reportInfoStatus))) {
                updatedRosterStatusInfoList.add(enrollment);

                reportInfoUserId = userId;
                reportInfoRole = role;
                reportInfoSection = section;
                reportInfoStatus = status;
            } else {
                continue;
            }
        }
        return updatedRosterStatusInfoList;
    }

    public List<CloseExpireCourse> getManuallyCreatedCoursesWithTerm() throws CanvasDataServiceException {
        List<CloseExpireCourse> notificationCourses = new ArrayList<>();
        String sql = "select distinct " +
              "course_dim.canvas_id AS canvas_id, " +
              "course_dim.name AS coursename, " +
              "course_dim.conclude_at AS end_date, " +
              "enrollment_term_dim.canvas_id AS canvas_term_id, " +
              "communication_channel_dim.address AS emailAdress " +
              "FROM course_dim course_dim " +
              "INNER JOIN course_section_dim course_section_dim ON (course_dim.id = course_section_dim.course_id) " +
              "INNER JOIN enrollment_dim enrollment_dim ON (course_section_dim.id = enrollment_dim.course_section_id) " +
              "INNER JOIN enrollment_fact enrollment_fact ON (enrollment_dim.id = enrollment_fact.enrollment_id) " +
              "INNER JOIN user_dim user_dim ON (enrollment_fact.user_id = user_dim.id) " +
              "INNER JOIN pseudonym_dim pseudonym_dim ON (user_dim.id = pseudonym_dim.user_id) " +
              "INNER JOIN role_dim role_dim ON (enrollment_dim.role_id = role_dim.id) " +
              "INNER JOIN enrollment_term_dim enrollment_term_dim ON (course_dim.enrollment_term_id = enrollment_term_dim.id) " +
              "INNER JOIN communication_channel_dim communication_channel_dim ON (communication_channel_dim.user_id = user_dim.id) " +
              "where  course_dim.sis_source_id is null "  +
              "and role_dim.base_role_type='TeacherEnrollment' " +
              "and course_dim.workflow_state != 'deleted' " +
              "and role_dim.workflow_state != 'deleted' " +
              "and enrollment_dim.workflow_state = 'active' " +
              "and communication_channel_dim.position=1 " +
              "order by course_dim.canvas_id ";

        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection conn = getConnection();
        validateConnection(conn);
        try {
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            Calendar cal = Calendar.getInstance();
            cal.setTimeZone(TimeZone.getTimeZone("UTC"));

            while (rs.next()) {
                CloseExpireCourse course = new CloseExpireCourse();
                course.setCanvasCourseId(rs.getString("canvas_id"));
                course.setCourseName(rs.getString("coursename"));
                course.setEndDate(rs.getTimestamp("end_date", cal));
                course.setTermId(rs.getString("canvas_term_id"));
                course.setEmailAddress(rs.getString("emailAdress"));
                notificationCourses.add(course);
            }
        } catch (SQLException e) {
            log.error("Error getting data", e);
            throw new IllegalStateException(e);
        } finally {
            close(conn, ps, rs);
        }
        return notificationCourses;
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
