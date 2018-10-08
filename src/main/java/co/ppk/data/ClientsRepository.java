package co.ppk.data;

import co.ppk.domain.Client;
import co.ppk.enums.Status;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class ClientsRepository {
    private final DataSource ds;

    public ClientsRepository(DataSource ds) {
        this.ds = ds;
    }

    public Optional<Client> getClientById(String id) {
        QueryRunner run = new QueryRunner(ds);
        try {
            String query = "SELECT id," +
                    "    name," +
                    "    status," +
                    "    gateway_account_id," +
                    "    gateway_merchant_id," +
                    "    gateway_api_key," +
                    "    gateway_api_login," +
                    "    create_date," +
                    "    update_date " +
                    " FROM ppk_payments.clients WHERE id = ?";

            Client client = run.query(query,
                rs -> {
                    if(!rs.next()){
                        return null;
                    }
                    rs.last();

                    return new Client.Builder()
                            .setId(rs.getString(1))
                            .setName(rs.getString(2))
                            .setStatus(rs.getString(3))
                            .setGatewayAccountId(rs.getString(4))
                            .setGatewayMerchantId(rs.getString(5))
                            .setGatewayApiKey(rs.getString(6))
                            .setGatewayApiLogin(rs.getString(7))
                            .setCreatedAt(rs.getString(8))
                            .setUpdatedAt(rs.getString(9))
                            .build();
                }, id);
            return Optional.ofNullable(client);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Client> getClients() {
        QueryRunner run = new QueryRunner(ds);
        try {
            String query = "SELECT id," +
                    "    name," +
                    "    status," +
                    "    gateway_account_id," +
                    "    gateway_merchant_id," +
                    "    gateway_api_key," +
                    "    gateway_api_login," +
                    "    create_date," +
                    "    update_date " +
                    " FROM ppk_payments.clients";

            return run.query(query,
                    rs -> {
                        List<Client> newClientsList = new LinkedList<>();
                        while (rs.next()){
                            newClientsList.add(new Client.Builder()
                                    .setId(rs.getString(1))
                                    .setName(rs.getString(2))
                                    .setStatus(rs.getString(3))
                                    .setGatewayAccountId(rs.getString(4))
                                    .setGatewayMerchantId(rs.getString(5))
                                    .setGatewayApiKey(rs.getString(6))
                                    .setGatewayApiLogin(rs.getString(7))
                                    .setCreatedAt(rs.getString(8))
                                    .setUpdatedAt(rs.getString(9))
                                    .build()
                            );
                        }
                        return newClientsList;
                    });
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Client> getClientsByStatus(String status) {
        QueryRunner run = new QueryRunner(ds);
        try {
            String query = "SELECT id, name, status, gateway_account_id, gateway_merchant_id, gateway_api_key, " +
                    "gateway_api_login, create_date, update_date FROM ppk_payments.clients WHERE status = ?";

            List<Client> clients = run.query(query,
                    rs -> {
                        List<Client> newClientsList = new LinkedList<>();
                        while (rs.next()){
                            newClientsList.add(new Client.Builder()
                                    .setId(rs.getString(1))
                                    .setName(rs.getString(2))
                                    .setStatus(rs.getString(3))
                                    .setGatewayAccountId(rs.getString(4))
                                    .setGatewayMerchantId(rs.getString(5))
                                    .setGatewayApiKey(rs.getString(6))
                                    .setGatewayApiLogin(rs.getString(7))
                                    .setCreatedAt(rs.getString(8))
                                    .setUpdatedAt(rs.getString(9))
                                    .build()
                            );
                        }
                        return newClientsList;
                    }, status);
            return clients;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateClientStatus(String id, String status) {
        try {
            Connection conn = ds.getConnection();
            conn.setAutoCommit(false);
            Statement stmt = conn.createStatement();
            try {
                String update = "UPDATE ppk_payments.clients " +
                        "SET status = '" + status + "'"+
                        "WHERE " +
                        "id = '" + id + "';";
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

    public String createClient(Client client) {
        QueryRunner run = new QueryRunner(ds);

        String clientId = UUID.randomUUID().toString();
        try {
            Connection conn = ds.getConnection();
            conn.setAutoCommit(false);
            try {
                String insert = "INSERT INTO ppk_payments.clients " +
                        "(id, name, status, gateway_account_id, gateway_merchant_id, gateway_api_key, gateway_api_login) " +
                        "VALUES " +
                        "('" + clientId + "', " +
                        "'" + client.getName() + "', " +
                        "'" + Status.ACTIVE.name() + "', " +
                        "'" + client.getGatewayAccountId() + "', " +
                        "'" + client.getGatewayMerchantId() + "', " +
                        "'" + client.getGatewayApiKey() + "', " +
                        "'" + client.getGatewayApiLogin() + "');";
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

        return clientId;
    }
}