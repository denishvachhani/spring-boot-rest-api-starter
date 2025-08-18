-- V1__Create_customers_table.sql
-- Creates the customers table with all necessary columns and constraints

CREATE TABLE customers (
    id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    ssn VARCHAR(255) NOT NULL,
    phone VARCHAR(255),
    status VARCHAR(50) DEFAULT 'PENDING_VERIFICATION',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP NULL
);

-- Add unique constraints
ALTER TABLE customers ADD CONSTRAINT uk_customers_email UNIQUE (email);
ALTER TABLE customers ADD CONSTRAINT uk_customers_ssn UNIQUE (ssn);

-- Add indexes for better performance
CREATE INDEX idx_customers_email ON customers(email);
CREATE INDEX idx_customers_ssn ON customers(ssn);
CREATE INDEX idx_customers_deleted_at ON customers(deleted_at);