-- 1. Foreign key: users.profile_image_id -> files.id
ALTER TABLE users
ADD CONSTRAINT fk_profile_image
FOREIGN KEY (profile_image_id)
REFERENCES files(id)
ON DELETE SET NULL;