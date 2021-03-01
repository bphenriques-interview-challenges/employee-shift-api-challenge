-- https://www.postgresql.org/docs/current/rangetypes.html
CREATE EXTENSION btree_gist;
CREATE TABLE shift (
  id SERIAL PRIMARY KEY,
  employee_id SERIAL NOT NULL,
  start_shift TIMESTAMP(0) WITHOUT TIME ZONE NOT NULL, -- Use-case does not require ms precision.
  end_shift TIMESTAMP(0) WITHOUT TIME ZONE NOT NULL,   -- Use-case does not require ms precision.

  CONSTRAINT start_shift_after_end_shift CHECK (start_shift < end_shift),
  -- The shifts mustn't overlap given the same employee
  CONSTRAINT employee_non_overlapping_shifts EXCLUDE USING gist (employee_id WITH =, tsrange(start_shift, end_shift) WITH &&),
  CONSTRAINT fk_employee
    FOREIGN KEY(employee_id)
    REFERENCES employee(id)
    ON DELETE CASCADE
);
