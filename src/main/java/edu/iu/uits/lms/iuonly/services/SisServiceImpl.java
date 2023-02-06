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
import edu.iu.uits.lms.iuonly.model.SisClass;
import edu.iu.uits.lms.iuonly.model.SisCourse;
import edu.iu.uits.lms.iuonly.model.SisFerpaEntry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
public class SisServiceImpl {
    private static final String SIS_COURSE_COLUMNS = "year, term, descrshort, campus, iu_dept_cd, iu_course_cd, iu_site_id, descr, iu_crseld_status, iu_scs_flag, status, iu_active, class_nbr, strm, iu_instrc_mode_des, iu_etext_isbns";
    private static final String SIS_COURSE_TABLE = "iu_lms.ps_iu_oncext_clas";
    private static final String SIS_ROSTER_FERPA_COLUMNS = "ferpa, iu_ims_username";
    private static final String SIS_ROSTER_TABLE = "iu_lms.ps_iu_oncext_rstr";
    private static final String SIS_CLASS_COLUMNS = "crse_id, crse_offer_nbr, strm, institution, class_nbr";
    private static final String SIS_CLASS_TABLE = "iu_lms.ps_class_tbl";
    private static final String SIS_COURSE_LOOKUP_COLUMNS = "iu_site_id, strm, institution, subject, catalog_nbr, class_nbr";
    private static final String SIS_COURSE_LOOKUP = "iu_lms.ps_sis_lookup";

    @Autowired
    @Qualifier("denododb")
    DataSource dataSource;

    public SisCourse getSisCourseBySiteId(String siteId) {
        SisCourse sisCourse = null;
        Connection conn = getConnection();

        String sql = "select " + SIS_COURSE_COLUMNS + " from " + SIS_COURSE_TABLE + " where iu_site_id = ?";
        log.debug("Executing SQL: " + sql + " with query parameters: " + siteId);

        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, siteId);
            rs = stmt.executeQuery();

            if (rs.next()) {
                sisCourse = translateRsToSisCourse(rs);
            }
            if (sisCourse == null) {
                log.warn("Could not find SisCourseBySiteId:" + siteId);
                return null;
            }
        } catch (SQLException e) {
            log.error("Error getting sis course", e);
            throw new IllegalStateException();
        } finally {
            close(conn, stmt, rs);
        }

        return sisCourse;
    }

    public List<SisFerpaEntry> getFerpaEntriesByListOfSisUserIds(ListWrapper listWrapper, boolean justYs) {
        long start = System.currentTimeMillis();
        List<SisFerpaEntry> entries = new ArrayList<>();
        Connection conn = getConnection();

        String sql = "select distinct " + SIS_ROSTER_FERPA_COLUMNS + " from " + SIS_ROSTER_TABLE + " where " +
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
                SisFerpaEntry entry = translateRsToFerpaSisEntry(rs);
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

    public boolean isLegitSisCourse(String iu_site_id) {
        boolean sisCourse = false;
        Connection conn = getConnection();

        String sql = "select " + SIS_COURSE_LOOKUP_COLUMNS + " from " + SIS_COURSE_LOOKUP + " where iu_site_id = ?";
        log.debug("Executing SQL: " + sql + " with query parameters: " + iu_site_id);

        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, iu_site_id);
            rs = stmt.executeQuery();

            if (rs.next()) {
                // if a record exists, set to true!
                sisCourse = true;
            }
        } catch (SQLException e) {
            log.error("Error getting sis course", e);
            throw new IllegalStateException();
        } finally {
            close(conn, stmt, rs);
        }

        // return boolean result
        return sisCourse;
    }

    public SisClass getSisClassByCourse(String strm, String classNumber, String campus, boolean includeCampus) {
        long start = System.currentTimeMillis();

        SisClass sisClass = null;
        Connection conn = getConnection();

        String sql = "select " + SIS_CLASS_COLUMNS + " from " + SIS_CLASS_TABLE + " " +
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
                sisClass = translateRsToSisClass(rs);
            }
            if (sisClass == null) {
                String message = "Could not find SisClassByCourse: ({0}, {1}, {2})";
                log.warn(MessageFormat.format(message, strm, classNumber, campus));
                return null;
            }
        } catch (SQLException e) {
            log.error("Error getting sis class by course", e);
            throw new IllegalStateException(e);
        } finally {
            close(conn, stmt, rs);
        }
        long end = System.currentTimeMillis();
        log.debug("getSisClassByCourse took " + (end - start) + " millis");
        return sisClass;
    }

    public int getIuoccCourseCount(String siteId) {
        int courseCount = 0;
        Connection conn = getConnection();

        String sql = "select count(*) as total from " + SIS_COURSE_TABLE + " where iu_occ_site_id = ?";
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
            log.error("Error getting sis course", e);
            throw new IllegalStateException();
        } finally {
            close(conn, stmt, rs);
        }
        return courseCount;
    }

    private SisClass translateRsToSisClass(ResultSet rs) {
        SisClass sisClass = new SisClass();
        try {
            //crse_id, crse_offer_nbr, strm, institution, class_nbr";
            sisClass.setCourseId(rs.getString(1));
            sisClass.setCourseOfferNumber(rs.getString(2));
            sisClass.setSTerm(rs.getString(3));
            sisClass.setInstitution(rs.getString(4));
            sisClass.setClassNumber(rs.getString(5));
        } catch (SQLException e) {
            log.error("Error: ", e);
            throw new IllegalStateException(e);
        }
        return sisClass;
    }

    /*
     * simply translates JDBC into POJO without closing ResultSet
     */
    private SisCourse translateRsToSisCourse(ResultSet rs) {
        SisCourse sisCourse = new SisCourse();
        try {
            sisCourse.setYear(rs.getString(1));
            sisCourse.setTerm(rs.getString(2));
            sisCourse.setDescriptionShort(rs.getString(3));
            sisCourse.setCampus(rs.getString(4));
            sisCourse.setIuDeptCd(rs.getString(5));
            sisCourse.setIuCourseCd(rs.getString(6));
            sisCourse.setIuSiteId(rs.getString(7));
            sisCourse.setDescription(rs.getString(8));
            sisCourse.setIuCourseLoadStatus(rs.getString(9));
            sisCourse.setIuScsFlag(rs.getString(10));
            sisCourse.setStatus(rs.getString(11));
            sisCourse.setIuActive(rs.getString(12));
            sisCourse.setClassNumber(rs.getString(13));
            sisCourse.setSTerm(rs.getString(14));
            sisCourse.setInstructionMode(rs.getString(15));
            sisCourse.setEtextIsbns(rs.getString(16));
        } catch (SQLException e) {
            log.error("Error: ", e);
            throw new IllegalStateException(e);
        }
        return sisCourse;
    }

    /*
     * simply translates JDBC into POJO without closing ResultSet
     */
    private SisFerpaEntry translateRsToFerpaSisEntry(ResultSet rs) {
        SisFerpaEntry entry = new SisFerpaEntry();
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
