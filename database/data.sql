-- ========================
-- AUTHORS DATA
-- ========================

INSERT INTO authors (name) VALUES 
('J.K. Rowling'), 
('George R.R. Martin'), 
('Agatha Christie'), 
('Leo Tolstoy'), 
('Jane Austen'), 
('Mark Twain'), 
('Charles Dickens'), 
('Ernest Hemingway'), 
('Haruki Murakami'), 
('Isabel Allende');

-- ========================
-- CATEGORIES DATA
-- ========================

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


-- ========================
-- USERS DATA
-- ========================
CREATE DATABASE  IF NOT EXISTS `secure_users_directory`;
USE `secure_users_directory`;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;


CREATE TABLE `users` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_name` text DEFAULT NULL,
  `password` text DEFAULT NULL,
  `role` text DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;

