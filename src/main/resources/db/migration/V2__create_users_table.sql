-- 1. Create users table
CREATE TABLE users (
    uid VARCHAR(36) PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    is_completed_profile BOOLEAN DEFAULT FALSE,
    profile_image_path VARCHAR(255),
    create_at TIMESTAMP NOT NULL DEFAULT NOW(),
    update_at TIMESTAMP NOT NULL DEFAULT NOW()
);
-- 2. Create GIN index for fast ILIKE '%text%' searches
CREATE INDEX idx_users_first_name_trgm
ON users USING GIN (first_name gin_trgm_ops);

CREATE INDEX idx_users_last_name_trgm
ON users USING GIN (last_name gin_trgm_ops);

CREATE INDEX idx_users_email_trgm
ON users USING GIN (email gin_trgm_ops);