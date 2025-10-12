CREATE TABLE IF NOT EXISTS admin_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    nickname VARCHAR(50),
    avatar VARCHAR(255),
    email VARCHAR(100),
    create_time DATETIME,
    update_time DATETIME,
    status INT DEFAULT 1
    role INT
    login_attempts INT DEFAULT 0,
    last_failed_login_time DATETIME
);