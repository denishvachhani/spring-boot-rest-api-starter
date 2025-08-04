package com.example.customeridentitydemo.repository;

import com.example.customeridentitydemo.model.Customer;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class JdbcCustomerRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public JdbcCustomerRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final class CustomerRowMapper implements RowMapper<Customer> {
        @Override
        public Customer mapRow(ResultSet rs, int rowNum) throws SQLException {
            Customer customer = new Customer();
            customer.setId(rs.getLong("id"));
            customer.setFirstName(rs.getString("first_name"));
            customer.setLastName(rs.getString("last_name"));
            customer.setEmail(rs.getString("email"));
            customer.setSsn(rs.getString("ssn"));
            customer.setPhone(rs.getString("phone"));
            // customer.setStatus(CustomerStatus.valueOf(rs.getString("status")));
            // customer.setDeletedAt(rs.getTimestamp("deleted_at").toLocalDateTime());
            // customer.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
            // customer.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
            return customer;
        }
    }

    public List<Customer> findAll() {
        return jdbcTemplate.query("SELECT * FROM customers WHERE deleted_at IS NULL", new CustomerRowMapper());
    }

    public Optional<Customer> findById(Long id) {
        var sql = "SELECT * FROM customers WHERE id = :id AND deleted_at IS NULL";
        var params = new HashMap<String, Object>();
        params.put("id", id);
        return jdbcTemplate.query(sql, params, new CustomerRowMapper())
                .stream()
                .findFirst();
    }

    public Customer save(Customer customer) {
        var sql = """
                INSERT INTO customers(
                    first_name, last_name, email, ssn, phone, status, created_at, updated_at
                )
                VALUES (
                    :first_name, :last_name, :email, :ssn, :phone, :status, :created_at, :updated_at
                )
                """;
        var params = new HashMap<String, Object>();
        params.put("first_name", customer.getFirstName());
        params.put("last_name", customer.getLastName());
        params.put("email", customer.getEmail());
        params.put("ssn", customer.getSsn());
        params.put("phone", customer.getPhone());
        params.put("status", "PENDING_VERIFICATION");
        params.put("created_at", new Timestamp(System.currentTimeMillis()));
        params.put("updated_at", new Timestamp(System.currentTimeMillis()));

        jdbcTemplate.update(sql, params);
        return customer;
    }

    public Customer update(Customer customer) {
        var sql = """
                UPDATE customers
                SET first_name = :first_name, last_name = :last_name, email = :email, ssn = :ssn, phone = :phone, updated_at = :updated_at
                WHERE id = :id
                """;
        var params = new HashMap<String, Object>();
        params.put("first_name", customer.getFirstName());
        params.put("last_name", customer.getLastName());
        params.put("email", customer.getEmail());
        params.put("ssn", customer.getSsn());
        params.put("phone", customer.getPhone());
        params.put("updated_at", new Timestamp(System.currentTimeMillis()));
        params.put("id", customer.getId());

        jdbcTemplate.update(sql, params);
        return customer;
    }

    public void deleteById(Long id) {
        var sql = "UPDATE customers SET deleted_at = :deleted_at WHERE id = :id";
        var params = new HashMap<String, Object>();
        params.put("deleted_at", new Timestamp(System.currentTimeMillis()));
        params.put("id", id);
        jdbcTemplate.update(sql, params);
    }
}
