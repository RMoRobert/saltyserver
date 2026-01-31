-- Create initial tables for Salty application
-- This matches the Swift SQLiteData schema as closely as possible
-- Column names match JPA's default naming strategy (camelCase -> snake_case)
-- All identifiers are quoted to preserve case in H2
--
-- SQLiteData type mappings:
--   .text -> TEXT (unlimited) -> H2: VARCHAR(255) for short fields, CLOB for long text/JSON
--   .integer -> INTEGER -> H2: INTEGER
--   .boolean -> INTEGER (0/1) -> H2: BOOLEAN
--   .datetime -> DATETIME -> H2: TIMESTAMP
--   .blob -> BLOB -> H2: BLOB
--   .jsonText -> TEXT -> H2: CLOB
-- Note: introduction uses CLOB because the entity has @Lob annotation

-- Course table
CREATE TABLE IF NOT EXISTS "course" (
    "id" VARCHAR(255) NOT NULL PRIMARY KEY,
    "name" VARCHAR(255),
    "last_modified_date" TIMESTAMP
);

-- Category table
CREATE TABLE IF NOT EXISTS "category" (
    "id" VARCHAR(255) NOT NULL PRIMARY KEY,
    "name" VARCHAR(255),
    "last_modified_date" TIMESTAMP
);

-- Recipe table
CREATE TABLE IF NOT EXISTS "recipe" (
    "id" VARCHAR(255) NOT NULL PRIMARY KEY,
    "name" VARCHAR(255) NOT NULL,
    "created_date" TIMESTAMP,
    "last_modified_date" TIMESTAMP,
    "last_prepared" TIMESTAMP,
    "source" VARCHAR(255),
    "source_details" VARCHAR(255),
    "introduction" CLOB,
    "difficulty" INTEGER,
    "rating" INTEGER,
    "image_filename" VARCHAR(255),
    "image_thumbnail_data" BLOB,
    "is_favorite" BOOLEAN,
    "want_to_make" BOOLEAN,
    "yield" VARCHAR(255),
    "servings" INTEGER,
    "course_id" VARCHAR(255),
    "directions" CLOB,
    "ingredients" CLOB,
    "notes" CLOB,
    "variations" CLOB,
    "preparation_times" CLOB,
    "nutrition" CLOB,
    FOREIGN KEY ("course_id") REFERENCES "course"("id") ON DELETE SET NULL
);

-- RecipeCategory junction table
-- Note: Swift schema has an "id" column, but JPA @ManyToMany uses composite PK
-- For data migration compatibility, we could add an "id" column if needed
CREATE TABLE IF NOT EXISTS "recipe_category" (
    "recipe_id" VARCHAR(255) NOT NULL,
    "category_id" VARCHAR(255) NOT NULL,
    PRIMARY KEY ("recipe_id", "category_id"),
    FOREIGN KEY ("recipe_id") REFERENCES "recipe"("id") ON DELETE CASCADE,
    FOREIGN KEY ("category_id") REFERENCES "category"("id") ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS "idx_recipe_category_recipe_id" ON "recipe_category"("recipe_id");
CREATE INDEX IF NOT EXISTS "idx_recipe_category_category_id" ON "recipe_category"("category_id");

-- Tag table
CREATE TABLE IF NOT EXISTS "tag" (
    "id" VARCHAR(255) NOT NULL PRIMARY KEY,
    "name" VARCHAR(255),
    "last_modified_date" TIMESTAMP
);

-- RecipeTag junction table
-- Note: Swift schema has an "id" column, but JPA @ManyToMany uses composite PK
-- For data migration compatibility, we could add an "id" column if needed
CREATE TABLE IF NOT EXISTS "recipe_tag" (
    "recipe_id" VARCHAR(255) NOT NULL,
    "tag_id" VARCHAR(255) NOT NULL,
    PRIMARY KEY ("recipe_id", "tag_id"),
    FOREIGN KEY ("recipe_id") REFERENCES "recipe"("id") ON DELETE CASCADE,
    FOREIGN KEY ("tag_id") REFERENCES "tag"("id") ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS "idx_recipe_tag_recipe_id" ON "recipe_tag"("recipe_id");
CREATE INDEX IF NOT EXISTS "idx_recipe_tag_tag_id" ON "recipe_tag"("tag_id");

-- ShoppingList table
CREATE TABLE IF NOT EXISTS "shopping_list" (
    "id" VARCHAR(255) NOT NULL PRIMARY KEY,
    "name" VARCHAR(255),
    "is_freeform" BOOLEAN,
    "contents_for_list" CLOB,
    "contents_for_freeform" CLOB
);

-- Track device sync times for deletion detection
-- Each device registers and tracks when it last synced
-- This allows detecting deletions without tombstone records

CREATE TABLE IF NOT EXISTS "device_sync" (
    "device_id" VARCHAR(255) NOT NULL PRIMARY KEY,
    "device_name" VARCHAR(255),
    "last_sync_date" TIMESTAMP NOT NULL,
    "first_sync_date" TIMESTAMP NOT NULL
);

CREATE INDEX IF NOT EXISTS "idx_device_sync_last_sync" ON "device_sync"("last_sync_date");

