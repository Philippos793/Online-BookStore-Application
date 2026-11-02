-- Seed data, executed on every startup - each insert skips rows that already exist

INSERT INTO authors (name)
SELECT * FROM (SELECT 'J.K. Rowling' AS name UNION
               SELECT 'George R.R. Martin' UNION
               SELECT 'Agatha Christie' UNION
               SELECT 'Leo Tolstoy' UNION
               SELECT 'Jane Austen' UNION
               SELECT 'Mark Twain' UNION
               SELECT 'Charles Dickens' UNION
               SELECT 'Ernest Hemingway' UNION
               SELECT 'Haruki Murakami' UNION
               SELECT 'Isabel Allende') AS new_authors
WHERE NOT EXISTS (
    SELECT 1 FROM authors WHERE name = new_authors.name
);

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
