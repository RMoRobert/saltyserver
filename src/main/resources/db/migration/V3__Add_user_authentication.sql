-- User table for authentication
CREATE TABLE IF NOT EXISTS "app_user" (
    "id" VARCHAR(255) NOT NULL PRIMARY KEY,
    "username" VARCHAR(255) NOT NULL UNIQUE,
    "password" VARCHAR(255) NOT NULL,
    "email" VARCHAR(255),
    "enabled" BOOLEAN DEFAULT TRUE,
    "created_date" TIMESTAMP
);

-- User roles table (for Spring Security authorities)
CREATE TABLE IF NOT EXISTS "user_roles" (
    "user_id" VARCHAR(255) NOT NULL,
    "role" VARCHAR(50) NOT NULL,
    PRIMARY KEY ("user_id", "role"),
    FOREIGN KEY ("user_id") REFERENCES "app_user"("id") ON DELETE CASCADE
);

-- Add user_id to recipes
ALTER TABLE "recipe" ADD COLUMN IF NOT EXISTS "user_id" VARCHAR(255);
ALTER TABLE "recipe" ADD CONSTRAINT IF NOT EXISTS "fk_recipe_user" 
    FOREIGN KEY ("user_id") REFERENCES "app_user"("id") ON DELETE CASCADE;
CREATE INDEX IF NOT EXISTS "idx_recipe_user_id" ON "recipe"("user_id");

-- Add user_id to courses
ALTER TABLE "course" ADD COLUMN IF NOT EXISTS "user_id" VARCHAR(255);
ALTER TABLE "course" ADD CONSTRAINT IF NOT EXISTS "fk_course_user" 
    FOREIGN KEY ("user_id") REFERENCES "app_user"("id") ON DELETE CASCADE;
CREATE INDEX IF NOT EXISTS "idx_course_user_id" ON "course"("user_id");

-- Add user_id to categories
ALTER TABLE "category" ADD COLUMN IF NOT EXISTS "user_id" VARCHAR(255);
ALTER TABLE "category" ADD CONSTRAINT IF NOT EXISTS "fk_category_user" 
    FOREIGN KEY ("user_id") REFERENCES "app_user"("id") ON DELETE CASCADE;
CREATE INDEX IF NOT EXISTS "idx_category_user_id" ON "category"("user_id");

-- Add user_id to tags
ALTER TABLE "tag" ADD COLUMN IF NOT EXISTS "user_id" VARCHAR(255);
ALTER TABLE "tag" ADD CONSTRAINT IF NOT EXISTS "fk_tag_user" 
    FOREIGN KEY ("user_id") REFERENCES "app_user"("id") ON DELETE CASCADE;
CREATE INDEX IF NOT EXISTS "idx_tag_user_id" ON "tag"("user_id");

-- Add user_id to device_sync (each device belongs to a user)
ALTER TABLE "device_sync" ADD COLUMN IF NOT EXISTS "user_id" VARCHAR(255);
ALTER TABLE "device_sync" ADD CONSTRAINT IF NOT EXISTS "fk_device_sync_user" 
    FOREIGN KEY ("user_id") REFERENCES "app_user"("id") ON DELETE CASCADE;
CREATE INDEX IF NOT EXISTS "idx_device_sync_user_id" ON "device_sync"("user_id");

-- Create a default admin user (password: admin123 - BCrypt hashed)
-- You should change this password immediately after first login!
INSERT INTO "app_user" ("id", "username", "password", "email", "enabled", "created_date") 
VALUES (
    '00000000-0000-0000-0000-000000000001',
    'admin',
    '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG',  -- admin123
    'admin@example.com',
    TRUE,
    CURRENT_TIMESTAMP
);

INSERT INTO "user_roles" ("user_id", "role") VALUES 
    ('00000000-0000-0000-0000-000000000001', 'ROLE_USER'),
    ('00000000-0000-0000-0000-000000000001', 'ROLE_ADMIN');
