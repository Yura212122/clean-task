package academy.prog.julia.repos;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
/**
 * Repository class for managing session-related data using {@code JdbcTemplate}.
 * <p>
 * This class provides methods to query session information such as retrieving the session ID by principal name
 * or retrieving the principal name by session ID.
 */
@Repository
public class SessionRepository2 {

    private final JdbcTemplate jdbcTemplate;
    /**
     * Constructor for {@code SessionRepository2}.
     *
     * @param jdbcTemplate the {@code JdbcTemplate} used for interacting with the database
     */
    public SessionRepository2(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    /**
     * Retrieves the most recent session ID for a given principal name.
     * <p>
     * This method queries the {@code SPRING_SESSION} table and fetches the session ID associated
     * with the specified principal name, ordered by creation time in descending order, and returns
     * the most recent session ID.
     *
     * @param principalName the name of the principal (user) whose session ID is being retrieved
     * @return the session ID of the most recent session for the given principal name, or {@code null} if no session is found
     */
    public String getSessionIdByPrincipalName(String principalName) {
        String sql = "SELECT SESSION_ID FROM SPRING_SESSION WHERE PRINCIPAL_NAME = ? ORDER BY CREATION_TIME DESC LIMIT 1";
        List<String> sessionIdList = jdbcTemplate.queryForList(sql, String.class, principalName);
        return sessionIdList.isEmpty() ? null : sessionIdList.get(0);
    }
    /**
     * Retrieves the principal name associated with a given session ID.
     * <p>
     * This method queries the {@code SPRING_SESSION} table and fetches the principal name
     * associated with the specified session ID, ordered by creation time in descending order, and
     * returns the principal name.
     *
     * @param sessionId the session ID whose associated principal name is being retrieved
     * @return the principal name associated with the given session ID, or {@code null} if no principal is found
     */
    public String getPrincipalNameBySessionId(String sessionId) {
        String sql = "SELECT PRINCIPAL_NAME FROM SPRING_SESSION WHERE SESSION_ID = ? ORDER BY CREATION_TIME DESC LIMIT 1";
        List<String> principalNameList = jdbcTemplate.queryForList(sql, String.class, sessionId);
        return principalNameList.isEmpty() ? null : principalNameList.get(0);
    }
}