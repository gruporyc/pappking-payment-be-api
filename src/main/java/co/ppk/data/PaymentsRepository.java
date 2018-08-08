package co.ppk.data;

import co.ppk.data.DataSourceSingleton;
import co.ppk.domain.Load;
import co.ppk.enums.Status;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;

@Component
public class PaymentsRepository {

    private final DataSource ds;

    public PaymentsRepository() {

        this.ds = DataSourceSingleton.getInstance();
    }

//
//    public List<Customer> getCustomers() {
//        QueryRunner run = new QueryRunner(ds);
//        List<Customer> customers = new LinkedList<>();
//        try {
//            String query = "SELECT * FROM ppk_customers.customers;";
//            List<Customer> customerList = run.query(query,
//                    rs -> {
//                        while (rs.next()) {
//                            customers.add(new Customer.Builder()
//                                    .setId(rs.getString(1))
//                                    .setIdentification(rs.getString(2))
//                                    .setName(rs.getString(3))
//                                    .setLastName(rs.getString(4))
//                                    .setEmail(rs.getString(5))
//                                    .setAddress(rs.getString(6))
//                                    .setPhone(rs.getString(7))
//                                    .setType(rs.getString(8))
//                                    .setStatus(rs.getString(9))
//                                    .setCreateDate(rs.getString(10))
//                                    .setUpdateDate(rs.getString(11))
//                                    .build());
//                        }
//                        return customers;
//                    });
//            return customerList;
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
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

    public void uppdateLoad(Load load) {
        QueryRunner run = new QueryRunner(ds);
        Timestamp now = Timestamp.from(Instant.now());
        try {
            Connection conn = ds.getConnection();
            conn.setAutoCommit(false);
            Statement stmt = conn.createStatement();
            try {
                String update = "UPDATE ppk_payments.loads " +
                        "SET customer_id = '" + load.getCustomerId() + "'," +
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
                        "  country = '" + load.getCountry() + "'," +
                        "  update_date = '" + now + "' " +
                        "WHERE " +
                        "id = '" + load.getId() + "';";
                stmt.executeUpdate(update);

                String query = "SELECT balance FROM ppk_payments.balances WHERE customer_id = '" + load.getCustomerId() + "';";
                String balance = run.query(query,
                    rs -> {
                        if (!rs.next()) {
                            return "";
                        }
                        rs.last();
                        return rs.getString(1);
                    });

                if(!balance.isEmpty()) {
                    String updateBalance = "UPDATE ppk_payments.balances SET balance = " + (Float.valueOf(balance) + load.getAmount()) +
                            ", update_date = '" + now + "' WHERE customer_id = '" + load.getCustomerId() + "';";
                    stmt.executeUpdate(updateBalance);
                } else {
                    String balanceId = UUID.randomUUID().toString();
                    run.insert(conn, "INSERT INTO ppk_payments.balances(id, " +
                            "customer_id, " +
                            "balance, " +
                            "status) " +
                            "VALUES('" + balanceId + "', '" + load.getCustomerId() + "', " + load.getAmount() + ", '" +
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
//
//    public Optional<Customer> getCustomerByIdentification(String identification) {
//        QueryRunner run = new QueryRunner(ds);
//        try {
//            String query = "SELECT * FROM ppk_customers.customers WHERE identification = '" + identification + "';";
//            Optional<Customer> customer = run.query(query,
//                    rs -> {
//                        if (!rs.next()) {
//                            Optional<Object> empty = Optional.empty();
//                            return Optional.empty();
//                        }
//                        rs.last();
//                        return Optional.ofNullable(new Customer.Builder()
//                                .setId(rs.getString(1))
//                                .setIdentification(rs.getString(2))
//                                .setName(rs.getString(3))
//                                .setLastName(rs.getString(4))
//                                .setEmail(rs.getString(5))
//                                .setAddress(rs.getString(6))
//                                .setPhone(rs.getString(7))
//                                .setType(rs.getString(8))
//                                .setStatus(rs.getString(9))
//                                .setCreateDate(rs.getString(10))
//                                .setUpdateDate(rs.getString(11))
//                                .build());
//                    });
//            return customer;
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//    }
}
