DROP TABLE IF EXISTS echo;

CREATE TABLE IF NOT EXISTS echo (
  id BIGINT NOT NULL AUTO_INCREMENT,
  message VARCHAR(255) NOT NULL,
  PRIMARY KEY (id)
);