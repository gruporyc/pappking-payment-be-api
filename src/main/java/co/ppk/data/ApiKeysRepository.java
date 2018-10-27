package co.ppk.data;

import co.ppk.domain.ApiKey;
import co.ppk.enums.Status;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;

@Component
public class ApiKeysRepository {
    private final DataSource ds;

    public ApiKeysRepository() {
        this.ds = DataSourceSingleton.getInstance();
    }

    public Optional<ApiKey> getApiKeyByToken(String token) {
        QueryRunner run = new QueryRunner(ds);
        try {
            String query = "SELECT id, token, client_id, status, expiration_date ,create_date, update_date " +
                    "FROM ppk_payments.keys WHERE token = ?";

            ApiKey apiKey = run.query(query,
                rs -> {
                    if(!rs.next()){
                        return null;
                    }
                    rs.last();

                    return new ApiKey.Builder()
                            .setId(rs.getString(1))
                            .setToken(rs.getString(2))
                            .setClientId(rs.getString(3))
                            .setStatus(rs.getString(4))
                            .setExpirationDate(rs.getString(5))
                            .setCreatedAt(rs.getString(6))
                            .setUpdatedAt(rs.getString(7))
                            .build();
                }, token);
            return Optional.ofNullable(apiKey);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void createApiKey(String token, String clientId, Timestamp expDate) {
        QueryRunner run = new QueryRunner(ds);

        try {
            Connection conn = ds.getConnection();
            conn.setAutoCommit(false);
            String id = UUID.randomUUID().toString();
            try {
                String insert = "INSERT INTO ppk_payments.keys " +
                        "(id, token, client_id, status, expiration_date) " +
                        "VALUES " +
                        "('" + id + "', " +
                        "'" + token + "', " +
                        "'" + clientId + "', " +
                        "'" + Status.ACTIVE.name() + "', " +
                        "'" + expDate + "');";
                run.insert(conn, insert, new ScalarHandler<>());
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw new RuntimeException(e);
            } finally {
                DbUtils.close(conn);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
