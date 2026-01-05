-- src/main/resources/data.sql
-- Insert default roles
INSERT INTO roles (role_name, description, is_active, created_at, updated_at) 
VALUES 
('ADMIN', 'Administrator', true, NOW(), NOW()),
('MANAGER', 'Manager', true, NOW(), NOW()),
('STAFF', 'Staff', true, NOW(), NOW()),
('USER', 'Regular User', true, NOW(), NOW());

-- Insert default admin user (password: admin123)
INSERT INTO users (email, name, password, phone, status, role_id, email_verified, created_at, updated_at)
VALUES ('admin@badminton.com', 'Admin User', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '0123456789', 'ACTIVE', 1, true, NOW(), NOW());
