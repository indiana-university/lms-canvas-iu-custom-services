package lms.iuonly.services;

import io.swagger.annotations.Api;
import lms.iuonly.exceptions.CanvasDataServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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

    @GetMapping("/activemap")
    @PreAuthorize("#oauth2.hasScope('" + READ_SCOPE + "')")
    public Map<String, String> getActiveUserMapOfIuUsernameToCanvasId(@RequestParam(value = "iuUsernames") List<String> iuUsernames) throws CanvasDataServiceException {
        Map<String, String> userMap = new HashMap<>();

        if (iuUsernames == null || iuUsernames.isEmpty()) {
            return userMap;
        }

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
