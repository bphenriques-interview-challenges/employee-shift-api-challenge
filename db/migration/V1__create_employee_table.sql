CREATE TABLE employee (
  id SERIAL,
  first_name VARCHAR(50) NOT NULL,
  last_name VARCHAR(50) NOT NULL,
  address VARCHAR(255) UNIQUE NOT NULL
);
