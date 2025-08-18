-- V3__Insert_sample_data.sql
-- Optional: Insert some sample data for development/testing

INSERT INTO customers (first_name, last_name, email, ssn, phone, status) VALUES
('John', 'Doe', 'john.doe@example.com', '123-45-6789', '555-0101', 'ACTIVE'),
('Jane', 'Smith', 'jane.smith@example.com', '987-65-4321', '555-0102', 'ACTIVE'),
('Bob', 'Johnson', 'bob.johnson@example.com', '456-78-9123', '555-0103', 'PENDING_VERIFICATION');

INSERT INTO addresses (customer_id, street, city, state, zip_code, address_type) VALUES
(1, '123 Main St', 'New York', 'NY', '10001', 'HOME'),
(1, '456 Work Ave', 'New York', 'NY', '10002', 'WORK'),
(2, '789 Oak Rd', 'Los Angeles', 'CA', '90210', 'HOME'),
(3, '321 Pine St', 'Chicago', 'IL', '60601', 'HOME');