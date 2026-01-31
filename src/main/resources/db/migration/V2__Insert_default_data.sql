-- Populate default categories, courses, and shopping lists
-- This matches migration 0002 from the Swift schema
-- All identifiers are quoted to preserve case in H2

-- Insert default categories
INSERT INTO "category" ("id", "name") VALUES 
    (RANDOM_UUID(7), 'Breads'),
    (RANDOM_UUID(7), 'Breakfast'),
    (RANDOM_UUID(7), 'Soups'),
    (RANDOM_UUID(7), 'Pasta'),
    (RANDOM_UUID(7), 'Holiday');

-- Insert default courses
INSERT INTO "course" ("id", "name") VALUES 
    (RANDOM_UUID(7), 'Appetizer'),
    (RANDOM_UUID(7), 'Main'),
    (RANDOM_UUID(7), 'Dessert'),
    (RANDOM_UUID(7), 'Snack'),
    (RANDOM_UUID(7), 'Salad'),
    (RANDOM_UUID(7), 'Fruit'),
    (RANDOM_UUID(7), 'Cheese'),
    (RANDOM_UUID(7), 'Vegetable'),
    (RANDOM_UUID(7), 'Side Dish'),
    (RANDOM_UUID(7), 'Bread'),
    (RANDOM_UUID(7), 'Sauce');

-- Insert default shopping list
INSERT INTO "shopping_list" ("id", "name", "is_freeform", "contents_for_freeform") VALUES 
    (RANDOM_UUID(7), 'Shopping List', true, '# Shopping List

##Store Name
* Item Name');
