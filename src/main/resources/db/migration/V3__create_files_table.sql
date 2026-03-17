-- 1. Create files table
CREATE TABLE files (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    original_name VARCHAR(255) NOT NULL,
    path VARCHAR(1024) NOT NULL,
    mime_type VARCHAR(100) NOT NULL,
    size BIGINT NOT NULL,
    usage_type VARCHAR(20) NOT NULL,
    uploaded_by VARCHAR(36) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

-- 2. Foreign key: files.uploaded_by -> users.uid
ALTER TABLE files
ADD CONSTRAINT fk_uploaded_by
FOREIGN KEY (uploaded_by)
REFERENCES users(uid)
ON DELETE CASCADE;

-- 3. Indexes
CREATE INDEX idx_files_usage_type ON files(usage_type);
CREATE INDEX idx_files_path ON files(path);
CREATE INDEX idx_files_uploaded_by ON files(uploaded_by);