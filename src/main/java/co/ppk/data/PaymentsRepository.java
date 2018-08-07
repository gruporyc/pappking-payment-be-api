package co.ppk.data;

import co.ppk.data.DataSourceSingleton;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class PaymentsRepository {

    private final DataSource ds;

    public PaymentsRepository() {

        this.ds = DataSourceSingleton.getInstance();
    }

//    public Optional<Payments> getCustomer(String customerId) {
//        QueryRunner run = new QueryRunner(ds);
//        try {
//            String query = "SELECT * FROM ppk_customers.customers WHERE id = '" + customerId + "';";
//            Optional<Customer> customer = run.query(query,
//                rs -> {
//                    if (!rs.next()) {
//                        Optional<Object> empty = Optional.empty();
//                        return Optional.empty();
//                    }
//                    rs.last();
//                    return Optional.ofNullable(new Customer.Builder()
//                            .setId(rs.getString(1))
//                            .setIdentification(rs.getString(2))
//                            .setName(rs.getString(3))
//                            .setLastName(rs.getString(4))
//                            .setEmail(rs.getString(5))
//                            .setAddress(rs.getString(6))
//                            .setPhone(rs.getString(7))
//                            .setType(rs.getString(8))
//                            .setStatus(rs.getString(9))
//                            .setCreateDate(rs.getString(10))
//                            .setUpdateDate(rs.getString(11))
//                            .build());
//                });
//            return customer;
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//    }
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
//    public String createCustomer(CustomerDto customer) {
//        QueryRunner run = new QueryRunner(ds);
////        Timestamp now = Timestamp.from(Instant.now());
//
//        String customerId = UUID.randomUUID().toString();
//        try {
//            Connection conn = ds.getConnection();
//            conn.setAutoCommit(false);
//            try {
//                String insert = "INSERT INTO ppk_customers.customers " +
//                        "(id, " +
//                        "identification, " +
//                        "name, " +
//                        "last_name, " +
//                        "mail, " +
//                        "address, " +
//                        "phone, " +
//                        "status, " +
//                        "type) " +
//                        "VALUES " +
//                        "('" + customerId + "', " +
//                        "'" + customer.getIdentification() + "', " +
//                        "'" + customer.getName() + "', " +
//                        "'" + customer.getLastName() + "', " +
//                        "'" + customer.getEmail() + "', " +
//                        "'" + customer.getAddress() + "', " +
//                        "'" + customer.getPhone() + "', " +
//                        "'" + customer.getStatus() + "', " +
//                        "'" + customer.getType() + "');";
//                run.insert(conn, insert, new ScalarHandler<>());
//                conn.commit();
//            } catch (SQLException e) {
//                conn.rollback();
//                throw new RuntimeException(e);
//            } finally {
//                DbUtils.close(conn);
//            }
//
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//
//        return customerId;
//    }
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
