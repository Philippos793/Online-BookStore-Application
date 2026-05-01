-- Inserting values into the categories table only if they do not already exist
INSERT INTO categories (name)
SELECT * FROM (SELECT 'Art' AS name UNION
               SELECT 'Comic' UNION
               SELECT 'Fantasy' UNION
               SELECT 'Fiction' UNION
               SELECT 'Biographies' UNION
               SELECT 'History' UNION
               SELECT 'Science' UNION
               SELECT 'Literature' UNION
               SELECT 'Adventure' UNION
               SELECT 'Crime' UNION
               SELECT 'Other') AS new_categories
WHERE NOT EXISTS (
    SELECT 1 FROM categories WHERE name = new_categories.name
);
