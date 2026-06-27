-- Bảng users
CREATE TABLE IF NOT EXISTS users (
    user_id SERIAL PRIMARY KEY,
    username VARCHAR(255),
    email VARCHAR(255) UNIQUE,
    balance DOUBLE PRECISION DEFAULT 0,
    card_number VARCHAR(255) UNIQUE,
    bank VARCHAR(255),
    hashed_password TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Bảng transition (lịch sử giao dịch)
CREATE TABLE IF NOT EXISTS transition (
    transition_id SERIAL PRIMARY KEY,
    from_user VARCHAR(255),
    to_user VARCHAR(255),
    amount DOUBLE PRECISION,
    fee DOUBLE PRECISION,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Dữ liệu mẫu (Tùy chọn)
-- Password mặc định thường được mã hóa, đây là ví dụ text thô nếu chưa dùng BCrypt
-- INSERT INTO users (username, email, balance, card_number, bank, hashed_password)
-- VALUES ('Admin', 'admin@gmail.com', 1000000, '123456789', 'Matcha Bank', '123456');
