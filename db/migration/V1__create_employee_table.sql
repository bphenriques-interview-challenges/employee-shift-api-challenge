CREATE TABLE employee (
  id SERIAL,
  first_name VARCHAR(50) NOT NULL,
  last_name VARCHAR(50) PRIMARY KEY,
  address VARCHAR(50) UNIQUE
);
