-- Test STAFF user for order detail testing
-- Password: staff123 (BCrypt: $2a$12$Wq9yF1wI5t5Y6z3m8q2uOe6jKz9x1V2rN4p7Q8s0T3uW5vY6zA1B2)

INSERT INTO users (email, password, full_name, phone, role, active, created_at) VALUES 
('staff@phukienrom.com', '$2a$12$Wq9yF1wI5t5Y6z3m8q2uOe6jKz9x1V2rN4p7Q8s0T3uW5vY6zA1B2', 'Nhân viên Test', '0123456789', 'STAFF', true, NOW())
ON CONFLICT (email) DO NOTHING;

-- Run: psql -U postgres -d phukienrom_dev -f create_staff_test_user.sql
