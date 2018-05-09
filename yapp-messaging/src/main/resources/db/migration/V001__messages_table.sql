CREATE TABLE messages (
  id     SERIAL PRIMARY KEY,
  text   VARCHAR(1000) NOT NULL,
  "from" INTEGER       NOT NULL,
  "to"   INTEGER       NOT NULL,
  date   timestamp DEFAULT current_timestamp
);
