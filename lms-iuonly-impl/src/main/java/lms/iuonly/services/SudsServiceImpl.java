package lms.iuonly.services;

import io.swagger.annotations.Api;
import lms.iuonly.model.SudsCourse;
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

@RestController
@RequestMapping("/suds")
@Slf4j
@Api(tags = "suds")
public class SudsServiceImpl extends BaseService {
    private static final String SUDS_COURSE_COLUMNS = "year, term, descrshort, campus, iu_dept_cd, iu_course_cd, iu_site_id, descr, iu_crseld_status, iu_scs_flag, status, iu_active, class_nbr, strm, iu_instrc_mode_des, iu_etext_isbns";
    private static final String SUDS_COURSE_TABLE = "sysadm.ps_iu_oncext_clas";

    @Autowired
    DataSource dataSource;

    @GetMapping("/course/siteid")
    @PreAuthorize("#oauth2.hasScope('" + READ_SCOPE + "')")
    public SudsCourse getSudsCourseBySiteId(@RequestParam(value = "id", required = false) String siteId) {
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