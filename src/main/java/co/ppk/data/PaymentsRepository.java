package co.ppk.data;

import co.ppk.domain.Balance;
import co.ppk.domain.Load;
import co.ppk.domain.Service;
import co.ppk.enums.Status;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;

public class PaymentsRepository {

    private final DataSource ds;

    public PaymentsRepository(DataSource ds) {

        this.ds = ds;
    }
    public String createLoad(Load load) {
        QueryRunner run = new QueryRunner(ds);

        String loadId = UUID.randomUUID().toString();
        try {
            Connection conn = ds.getConnection();
            conn.setAutoCommit(false);
            try {
                String insert = "INSERT INTO ppk_payments.loads " +
                        "(id, " +
                        "  customer_id," +
                        "  client_id," +
                        "  amount," +
                        "  currency," +
                        "  payer_name," +
                        "  payer_card_last_digits," +
                        "  method," +
                        "  order_id," +
                        "  transaction_id," +
                        "  status," +
                        "  network_code," +
                        "  network_message," +
                        "  trazability_code," +
                        "  response_code," +
                        "  country) " +
                        "VALUES " +
                        "('" + loadId + "', " +
                        "'" + load.getCustomerId() + "', " +
                        "'" + load.getClientId() + "', " +
                        "'" + load.getAmount() + "', " +
                        "'" + load.getCurrency() + "', " +
                        "'" + load.getPayerName() + "', " +
                        "'" + load.getPayerCardLastDigits() + "', " +
                        "'" + load.getMethod() + "', " +
                        "'" + load.getOrderId() + "', " +
                        "'" + load.getTransactionId() + "', " +
                        "'" + load.getStatus() + "', " +
                        "'" + load.getNetworkCode() + "', " +
                        "'" + load.getNetworkMessage() + "', " +
                        "'" + load.getTrazabilityCode() + "', " +
                        "'" + load.getResponseCode() + "', " +
                        "'" + load.getCountry() + "');";
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

        return loadId;
    }

    public void updateLoad(Load load) {
        try {
            Connection conn = ds.getConnection();
            conn.setAutoCommit(false);
            Statement stmt = conn.createStatement();
            try {
                String update = "UPDATE ppk_payments.loads " +
                        "SET customer_id = '" + load.getCustomerId() + "'," +
                        "  client_id = '" + load.getClientId() + "'," +
                        "  amount = '" + load.getAmount() + "'," +
                        "  currency = '" + load.getCurrency() + "'," +
                        "  payer_name = '" + load.getPayerName() + "'," +
                        "  payer_card_last_digits = '" + load.getPayerCardLastDigits() + "'," +
                        "  method = '" + load.getMethod() + "'," +
                        "  order_id = '" + load.getOrderId() + "'," +
                        "  transaction_id = '" + load.getTransactionId() + "'," +
                        "  status = '" + load.getStatus() + "'," +
                        "  network_code = '" + load.getNetworkCode() + "'," +
                        "  network_message = '" + load.getNetworkMessage() + "'," +
                        "  trazability_code = '" + load.getTrazabilityCode() + "'," +
                        "  response_code = '" + load.getResponseCode() + "'," +
                        "  country = '" + load.getCountry() + "' " +
                        "WHERE " +
                        "id = '" + load.getId() + "';";
                stmt.executeUpdate(update);

                if (load.getStatus().equals(Status.APPROVED.name())) {
                    updateBalance(load.getCustomerId(), load.getClientId(), load.getAmount());
                }

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


    public void updateBalance(String customerId, String clientId, double amount) {
        QueryRunner run = new QueryRunner(ds);
        Timestamp now = Timestamp.from(Instant.now());
        try {
            Connection conn = ds.getConnection();
            conn.setAutoCommit(false);
            Statement stmt = conn.createStatement();
            try {
                String query = "SELECT balance FROM ppk_payments.balances WHERE customer_id = '" + customerId + "' " +
                        "AND client_id = '" + clientId + "';";
                String balance = run.query(query,
                        rs -> {
                            if (!rs.next()) {
                                return "";
                            }
                            rs.last();
                            return rs.getString(1);
                        });

                if(!balance.isEmpty()) {
                    String updateBalance = "UPDATE ppk_payments.balances SET balance = " + (Float.valueOf(balance) + amount) +
                             " WHERE customer_id = '" + customerId + "' " +
                            "AND client_id = '" + clientId + "';";
                    stmt.executeUpdate(updateBalance);
                } else {
                    String balanceId = UUID.randomUUID().toString();
                    run.insert(conn, "INSERT INTO ppk_payments.balances(id, " +
                            "customer_id, " +
                            "client_id, " +
                            "balance, " +
                            "status) " +
                            "VALUES('" + balanceId + "', '" + customerId + "', '" + clientId + "', " + amount + ", '" +
                            Status.ACTIVE.name() + "');", new ScalarHandler<>());
                }
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

    public void updateLoadStatus(String loadId, String clientId, String status) {
        try {
            Connection conn = ds.getConnection();
            conn.setAutoCommit(false);
            Statement stmt = conn.createStatement();
            try {
                String update = "UPDATE ppk_payments.loads " +
                        "SET status = '" + status + "' " +
                        "WHERE " +
                        "id = '" + loadId + "' " +
                        "AND client_id = '" + clientId + "';";
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

    public List<Load> getLoadsByStatus(String status, String clientId) {
        QueryRunner run = new QueryRunner(ds);
        try {
            String query = "SELECT id, customer_id, amount, currency, payer_name, payer_card_last_digits, method, " +
                    "order_id, transaction_id, status, network_code, network_message, trazability_code, response_code, " +
                    "country, create_date, update_date FROM ppk_payments.loads WHERE status = ? and client_id = ?;";

            List<Load> loads = run.query(query,
                    rs -> {
                        List<Load> newLoadsList = new LinkedList<>();
                        while (rs.next()){
                            newLoadsList.add(new Load.Builder()
                                    .setId(rs.getString(1))
                                    .setCustomerId(rs.getString(2))
                                    .setAmount(rs.getFloat(3))
                                    .setCurrency(rs.getString(4))
                                    .setPayerName(rs.getString(5))
                                    .setPayerCardLastDigits(rs.getString(6))
                                    .setMethod(rs.getString(7))
                                    .setOrderId(rs.getString(8))
                                    .setTransactionId(rs.getString(9))
                                    .setStatus(rs.getString(10))
                                    .setNetworkCode(rs.getString(11))
                                    .setNetworkMessage(rs.getString(12))
                                    .setTrazabilityCode(rs.getString(13))
                                    .setResponseCode(rs.getString(14))
                                    .setCountry(rs.getString(15))
                                    .setCreatedAt(rs.getString(16))
                                    .setUpdatedAt(rs.getString(17))
                                    .build()
                            );
                        }
                        return newLoadsList;
                    }, status, clientId);
            return loads;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<Load> getLoadById(String loadId) {
        QueryRunner run = new QueryRunner(ds);
        try {
            String query = "SELECT id, customer_id, client_id, amount, currency, payer_name, payer_card_last_digits, method, " +
                    "order_id, transaction_id, status, network_code, network_message, trazability_code, response_code, " +
                    "country, create_date, update_date FROM ppk_payments.loads WHERE id = ?;";

            Load load =  run.query(query,
                    rs -> {
                        if(!rs.next()){
                            return null;
                        }
                        rs.last();
                        return new Load.Builder()
                                .setId(rs.getString(1))
                                .setCustomerId(rs.getString(2))
                                .setClientId(rs.getString(3))
                                .setAmount(rs.getFloat(4))
                                .setCurrency(rs.getString(5))
                                .setPayerName(rs.getString(6))
                                .setPayerCardLastDigits(rs.getString(7))
                                .setMethod(rs.getString(8))
                                .setOrderId(rs.getString(9))
                                .setTransactionId(rs.getString(10))
                                .setStatus(rs.getString(11))
                                .setNetworkCode(rs.getString(12))
                                .setNetworkMessage(rs.getString(13))
                                .setTrazabilityCode(rs.getString(14))
                                .setResponseCode(rs.getString(15))
                                .setCountry(rs.getString(16))
                                .setCreatedAt(rs.getString(17))
                                .setUpdatedAt(rs.getString(18))
                                .build();
                        }, loadId);
            return Optional.ofNullable(load);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<Service> getServiceById(String serviceId, String clientId) {
        QueryRunner run = new QueryRunner(ds);
        try {
            String query = "SELECT id, service_id, amount, status, create_date, update_date FROM ppk_payments.services " +
                    "WHERE service_id = ? AND client_id = ?;";

            Service service = run.query(query,
                    rs -> {
                        if(!rs.next()){
                            return null;
                        }
                        rs.last();
                        return new Service.Builder()
                                .setId(rs.getString(1))
                                .setServiceId(rs.getString(2))
                                .setAmount(rs.getFloat(3))
                                .setStatus(rs.getString(4))
                                .setCreatedAt(rs.getString(5))
                                .setUpdatedAt(rs.getString(6))
                                .build();
                        }, serviceId, clientId);
            return Optional.ofNullable(service);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String createServicePayment(Service service) {
        QueryRunner run = new QueryRunner(ds);
        String serviceId = UUID.randomUUID().toString();
        try {
            Connection conn = ds.getConnection();
            conn.setAutoCommit(false);
            Statement stmt = conn.createStatement();
            try {
                run.insert(conn, "INSERT INTO ppk_payments.services(id, " +
                        "service_id, " +
                        "customer_id, " +
                        "client_id, " +
                        "amount, " +
                        "status) " +
                        "VALUES('" + serviceId + "', '" + service.getServiceId() + "', '" + service.getCustomerId() + "', '" +
                        service.getClientId() + "', " + service.getAmount() + ", '" + Status.APPROVED.name() + "');",
                        new ScalarHandler<>());

                String updateBalance = "UPDATE ppk_payments.balances SET balance = balance - " + (service.getAmount()) +
                             " WHERE customer_id = '" + service.getCustomerId() + "';";
                stmt.executeUpdate(updateBalance);
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
        return serviceId;
    }

    public Optional<Balance> getBalance(String customerId, String clientId) {
        QueryRunner run = new QueryRunner(ds);
        try {
            String query = "SELECT id, customer_id, balance, status, create_date, update_date FROM ppk_payments.balances " +
                    "WHERE customer_id = ? AND client_id = ?;";

            Balance balance = run.query(query,
                    rs -> {
                        if(!rs.next()){
                            return null;
                        }
                        rs.last();
                        return new Balance.Builder()
                                .setId(rs.getString(1))
                                .setCustomerId(rs.getString(2))
                                .setBalance(rs.getFloat(3))
                                .setStatus(rs.getString(4))
                                .setCreatedAt(rs.getString(5))
                                .setUpdatedAt(rs.getString(6))
                                .build();
                    }, customerId, clientId);
            return Optional.ofNullable(balance);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
