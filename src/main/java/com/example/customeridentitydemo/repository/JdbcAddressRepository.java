package com.example.customeridentitydemo.repository;

import com.example.customeridentitydemo.model.Address;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class JdbcAddressRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public JdbcAddressRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final class AddressRowMapper implements RowMapper<Address> {
        @Override
        public Address mapRow(ResultSet rs, int rowNum) throws SQLException {
            Address address = new Address();
            address.setId(rs.getLong("id"));
            address.setStreet(rs.getString("street"));
            address.setCity(rs.getString("city"));
            address.setState(rs.getString("state"));
            address.setZipCode(rs.getString("zip_code"));
            // address.setAddressType(AddressType.valueOf(rs.getString("address_type")));
            return address;
        }
    }

    public List<Address> findByCustomerId(Long customerId) {
        var sql = "SELECT * FROM addresses WHERE customer_id = :customer_id";
        var params = new HashMap<String, Object>();
        params.put("customer_id", customerId);
        return jdbcTemplate.query(sql, params, new AddressRowMapper());
    }

    public Address save(Address address) {
        var sql = """
                INSERT INTO addresses(
                    street, city, state, zip_code, address_type, customer_id
                )
                VALUES (
                    :street, :city, :state, :zip_code, :address_type, :customer_id
                )
                """;
        var params = new HashMap<String, Object>();
        params.put("street", address.getStreet());
        params.put("city", address.getCity());
        params.put("state", address.getState());
        params.put("zip_code", address.getZipCode());
        params.put("address_type", address.getAddressType().toString());
        params.put("customer_id", address.getCustomer().getId());

        jdbcTemplate.update(sql, params);
        return address;
    }

    public void deleteById(Long id) {
        var sql = "DELETE FROM addresses WHERE id = :id";
        var params = new HashMap<String, Object>();
        params.put("id", id);
        jdbcTemplate.update(sql, params);
    }
}
