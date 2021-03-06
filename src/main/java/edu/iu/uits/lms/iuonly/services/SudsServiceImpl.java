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

import edu.iu.uits.lms.iuonly.model.ListWrapper;
import edu.iu.uits.lms.iuonly.model.SudsAdvisor;
import edu.iu.uits.lms.iuonly.model.SudsClass;
import edu.iu.uits.lms.iuonly.model.SudsCourse;
import edu.iu.uits.lms.iuonly.model.SudsFerpaEntry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class SudsServiceImpl {
    private static final String SUDS_ADVISOR_COLUMNS = "emplid, institution, advisor_role, stdnt_advisor_nbr, advisor_id, acad_career, acad_prog, acad_plan, descr, acad_career_descr, iu_ims_username, emailid, first_name, last_name, status, iu_active, audit_stamp";
    private static final String SUDS_ADVISOR_TABLE = "sysadm.ps_iu_oncext_advr";
    private static final String SUDS_COURSE_COLUMNS = "year, term, descrshort, campus, iu_dept_cd, iu_course_cd, iu_site_id, descr, iu_crseld_status, iu_scs_flag, status, iu_active, class_nbr, strm, iu_instrc_mode_des, iu_etext_isbns";
    private static final String SUDS_COURSE_TABLE = "sysadm.ps_iu_oncext_clas";
    private static final String SUDS_ROSTER_FERPA_COLUMNS = "ferpa, iu_ims_username";
    private static final String SUDS_ROSTER_TABLE = "sysadm.ps_iu_oncext_rstr";
    private static final String SUDS_CLASS_COLUMNS = "crse_id, crse_offer_nbr, strm, institution, class_nbr";
    private static final String SUDS_CLASS_TABLE = "sysadm.ps_class_tbl";
    private static final String SUDS_COURSE_ARCHIVE_TABLE = "lms.ps_iu_oncext_clas_archive";

    @Autowired
    DataSource dataSource;

    public SudsCourse getSudsCourseBySiteId(String siteId) {
        SudsCourse sudsCourse = null;
        Connection conn = getConnection();

        String sql = "select " + SUDS_COURSE_COLUMNS + " from " + SUDS_COURSE_TABLE + " where iu_site_id = ?";
        log.debug("Executing SQL: " + sql + " with query parameters: " + siteId);

        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, siteId);
            rs = stmt.executeQuery();

            if (rs.next()) {
                sudsCourse = translateRsToSudsCourse(rs);
            }
            if (sudsCourse == null) {
                log.warn("Could not find SudsCourseBySiteId:" + siteId);
                return null;
            }
        } catch (SQLException e) {
            log.error("Error getting suds course", e);
            throw new IllegalStateException();
        } finally {
            close(conn, stmt, rs);
        }

        return sudsCourse;
    }

    public SudsCourse getSudsArchiveCourseBySiteId(String siteId) {
        SudsCourse sudsCourse = null;
        Connection conn = getConnection();

        String sql = "select " + SUDS_COURSE_COLUMNS + " from " + SUDS_COURSE_ARCHIVE_TABLE + " where iu_site_id = ?";
        log.debug("Executing SQL: " + sql + " with query parameters: " + siteId);

        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, siteId);
            rs = stmt.executeQuery();

            if (rs.next()) {
                sudsCourse = translateRsToSudsCourse(rs);
            }
            if (sudsCourse == null) {
                log.warn("Could not find SudsCourseBySiteId:" + siteId);
                return null;
            }
        } catch (SQLException e) {
            log.error("Error getting suds course", e);
            throw new IllegalStateException();
        } finally {
            close(conn, stmt, rs);
        }

        return sudsCourse;
    }

    public List<SudsFerpaEntry> getFerpaEntriesByListOfSisUserIds(ListWrapper listWrapper, boolean justYs) {
        long start = System.currentTimeMillis();
        List<SudsFerpaEntry> entries = new ArrayList<>();
        Connection conn = getConnection();

        String sql = "select distinct " + SUDS_ROSTER_FERPA_COLUMNS + " from " + SUDS_ROSTER_TABLE + " where " +
              LmsSqlUtils.buildWhereInClause("iu_ims_username", listWrapper.getListItems(), false);

        if (justYs) {
            sql = sql + " and ferpa = ?";
        }

        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.prepareStatement(sql);
            if (justYs) {
                stmt.setString(1, "Y");
            }
            rs = stmt.executeQuery();

            while (rs.next()) {
                SudsFerpaEntry entry = translateRsToFerpaSudsEntry(rs);
                entries.add(entry);
            }
            if (entries.isEmpty()) {
                return entries;
            }
        } catch (SQLException e) {
            log.error("Error getting ferpa entries ", e);
            throw new IllegalStateException(e);
        } finally {
            close(conn, stmt, rs);
        }
        long end = System.currentTimeMillis();
        log.debug("getFerpaEntriesByListOfSisUserIds took " + (end - start) + " millis");
        return entries;
    }

    public List<SudsAdvisor> getActiveSudsAdvisorByEmplid(String emplid) {
        long start = System.currentTimeMillis();
        Connection conn = getConnection();

        List<SudsAdvisor> sudsAdvisors = new ArrayList<SudsAdvisor>();

        String sql = "select " + SUDS_ADVISOR_COLUMNS + " from " + SUDS_ADVISOR_TABLE + " where emplid = ? and IU_ACTIVE = ?";

        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, emplid);
            stmt.setString(2, "A");
            rs = stmt.executeQuery();

            while (rs.next()) {
                SudsAdvisor advisor = translateRsToSudsAdvisor(rs);
                sudsAdvisors.add(advisor);
            }
            if (sudsAdvisors.isEmpty()) {
                log.warn("Could not find SudsAdvisorByEmplid:" + emplid);
                return sudsAdvisors;
            }
        } catch (SQLException e) {
            log.error("{}", e);
            throw new IllegalStateException(e);
        } finally {
            close(conn, stmt, rs);
        }
        long end = System.currentTimeMillis();
        log.debug("getActiveSudsAdvisorByEmplid took " + (end - start) + " millis");
        return sudsAdvisors;
    }

    public boolean isLegitSisCourse(String iu_site_id, String strm) {
        // translating iu_site_id into a SudsCourse object that will be useful in getSudsClassByCourse
        SudsCourse sudsCourse = new SudsCourse();
        sudsCourse.setSTerm(strm);

        // parsing is lame! But, this will get everything after the last dash of the iu_site_id
        String[] idSplit = iu_site_id.split("-");
        int dashes = idSplit.length - 1;
        if (dashes == 4) {
            String classNumber = idSplit[4];
            sudsCourse.setClassNumber(classNumber);

            // got the stuff, now send it to getSudsClassByCourse to see if it finds anything
            SudsClass sudsClass = getSudsClassByCourse(sudsCourse.getSTerm(), sudsCourse.getClassNumber(),
                  sudsCourse.getCampus(), false);
            return sudsClass != null;
        } else {
            return false;
        }
    }

    public SudsClass getSudsClassByCourse(String strm, String classNumber, String campus,  boolean includeCampus) {
        long start = System.currentTimeMillis();

        SudsClass sudsClass = null;
        Connection conn = getConnection();

        String sql = "select " + SUDS_CLASS_COLUMNS + " from " + SUDS_CLASS_TABLE + " " +
              "where strm = ? and class_nbr = ?";

        if (includeCampus) {
            sql += " and campus = ?";
        }

        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, strm);
            stmt.setString(2, classNumber);
            if (includeCampus) {
                stmt.setString(3, campus);
            }
            rs = stmt.executeQuery();

            if (rs.next()) {
                sudsClass = translateRsToSudsClass(rs);
            }
            if (sudsClass == null) {
                String message = "Could not find SudsClassByCourse: ({0}, {1}, {2})";
                log.warn(MessageFormat.format(message, strm, classNumber, campus));
                return null;
            }
        } catch (SQLException e) {
            log.error("Error getting suds class by course", e);
            throw new IllegalStateException(e);
        } finally {
            close(conn, stmt, rs);
        }
        long end = System.currentTimeMillis();
        log.debug("getSudsClassByCourse took " + (end - start) + " millis");
        return sudsClass;
    }

    public int getIuoccCourseCount(String siteId) {
        int courseCount = 0;
        Connection conn = getConnection();

        String sql = "select count(*) as total from " + SUDS_COURSE_TABLE + " where iu_occ_site_id = ?";
        log.debug("Executing SQL: " + sql + " with query parameters: " + siteId);

        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, siteId);
            rs = stmt.executeQuery();

            if (rs.next()) {
                courseCount = rs.getInt("total");
            }
        } catch (SQLException e) {
            log.error("Error getting suds course", e);
            throw new IllegalStateException();
        } finally {
            close(conn, stmt, rs);
        }
        return courseCount;
    }

    /*
     * simply translates JDBC into POJO without closing ResultSet
     */
    private SudsAdvisor translateRsToSudsAdvisor(ResultSet rs) {
        SudsAdvisor sudsAdvisor = new SudsAdvisor();
        try {
            sudsAdvisor.setEmplId(rs.getString(1));
            sudsAdvisor.setInstitution(rs.getString(2));
            sudsAdvisor.setAdvisorRole(rs.getString(3));
            sudsAdvisor.setStudentAdvisorNumber(rs.getString(4));
            sudsAdvisor.setAdvisorId(rs.getString(5));
            sudsAdvisor.setAcademicCareer(rs.getString(6));
            sudsAdvisor.setAcademicProgram(rs.getString(7));
            sudsAdvisor.setAcademicPlan(rs.getString(8));
            sudsAdvisor.setDescription(rs.getString(9));
            sudsAdvisor.setAcademicCareerDescription(rs.getString(10));
            sudsAdvisor.setAdvisorIuImsUsername(rs.getString(11));
            sudsAdvisor.setAdvisorEmailId(rs.getString(12));
            sudsAdvisor.setAdvisorFirstName(rs.getString(13));
            sudsAdvisor.setAdvisorLastName(rs.getString(14));
            sudsAdvisor.setStatus(rs.getString(15));
            sudsAdvisor.setIuActive(rs.getString(16));
            sudsAdvisor.setAuditStampString(rs.getString(17));
        } catch (SQLException e) {
            log.error("{}", e);
            throw new IllegalStateException(e);
        }
        return sudsAdvisor;
    }

    private SudsClass translateRsToSudsClass(ResultSet rs) {
        SudsClass sudsClass = new SudsClass();
        try {
            //crse_id, crse_offer_nbr, strm, institution, class_nbr";
            sudsClass.setCourseId(rs.getString(1));
            sudsClass.setCourseOfferNumber(rs.getString(2));
            sudsClass.setSTerm(rs.getString(3));
            sudsClass.setInstitution(rs.getString(4));
            sudsClass.setClassNumber(rs.getString(5));
        } catch (SQLException e) {
            log.error("Error: ", e);
            throw new IllegalStateException(e);
        }
        return sudsClass;
    }

    /*
     * simply translates JDBC into POJO without closing ResultSet
     */
    private SudsCourse translateRsToSudsCourse(ResultSet rs) {
        SudsCourse sudsCourse = new SudsCourse();
        try {
            sudsCourse.setYear(rs.getString(1));
            sudsCourse.setTerm(rs.getString(2));
            sudsCourse.setDescriptionShort(rs.getString(3));
            sudsCourse.setCampus(rs.getString(4));
            sudsCourse.setIuDeptCd(rs.getString(5));
            sudsCourse.setIuCourseCd(rs.getString(6));
            sudsCourse.setIuSiteId(rs.getString(7));
            sudsCourse.setDescription(rs.getString(8));
            sudsCourse.setIuCourseLoadStatus(rs.getString(9));
            sudsCourse.setIuScsFlag(rs.getString(10));
            sudsCourse.setStatus(rs.getString(11));
            sudsCourse.setIuActive(rs.getString(12));
            sudsCourse.setClassNumber(rs.getString(13));
            sudsCourse.setSTerm(rs.getString(14));
            sudsCourse.setInstructionMode(rs.getString(15));
            sudsCourse.setEtextIsbns(rs.getString(16));
        } catch (SQLException e) {
            log.error("Error: ", e);
            throw new IllegalStateException(e);
        }
        return sudsCourse;
    }

    /*
     * simply translates JDBC into POJO without closing ResultSet
     */
    private SudsFerpaEntry translateRsToFerpaSudsEntry(ResultSet rs) {
        SudsFerpaEntry entry = new SudsFerpaEntry();
        try {
            entry.setFerpa(rs.getString(1));
            entry.setIuImsUsername(rs.getString(2));
        } catch (SQLException e) {
            log.error("Error:", e);
            throw new IllegalStateException(e);
        }
        return entry;
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
