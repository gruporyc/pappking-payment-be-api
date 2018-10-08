package co.ppk.data;

import co.ppk.domain.ApiKey;
import co.ppk.enums.Status;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.*;

public class ApiKeysRepository {
    private final DataSource ds;

    public ApiKeysRepository(DataSource ds) {
        this.ds = ds;
    }

    public Optional<ApiKey> getApiKeyById(String id) {
        QueryRunner run = new QueryRunner(ds);
        try {
            String query = "SELECT id, client_id, status, expiration_date ,create_date, update_date " +
                    "FROM ppk_payments.keys WHERE id = ?";

            ApiKey apiKey = run.query(query,
                rs -> {
                    if(!rs.next()){
                        return null;
                    }
                    rs.last();

                    return new ApiKey.Builder()
                            .setId(rs.getString(1))
                            .setClientId(rs.getString(2))
                            .setStatus(rs.getString(3))
                            .setExpirationDate(rs.getString(4))
                            .setCreatedAt(rs.getString(5))
                            .setUpdatedAt(rs.getString(6))
                            .build();
                }, id);
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
            try {
                String insert = "INSERT INTO ppk_payments.keys " +
                        "(id, client_id, status, expiration_date) " +
                        "VALUES " +
                        "('" + token + "', " +
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

    public void updateApiKeyStatus(String token, String status) {
        try {
            Connection conn = ds.getConnection();
            conn.setAutoCommit(false);
            Statement stmt = conn.createStatement();
            try {
                String update = "UPDATE ppk_payments.keys " +
                        "SET status = '" + status + "' " +
                        "WHERE " +
                        "id = '" + token + "';";
                stmt.executeUpdate(update);
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
