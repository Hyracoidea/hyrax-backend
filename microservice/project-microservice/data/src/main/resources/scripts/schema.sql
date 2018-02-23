DROP TABLE IF EXISTS team_member;

DROP TABLE IF EXISTS team;



CREATE TABLE IF NOT EXISTS team (
  team_id BIGINT NOT NULL AUTO_INCREMENT,
  name VARCHAR(255) NOT NULL,
  description VARCHAR(255) NULL,
  owner_username VARCHAR(255) NOT NULL,

  PRIMARY KEY (team_id),
  UNIQUE KEY (name)
);

CREATE TABLE IF NOT EXISTS team_member (
  team_name VARCHAR(255) NOT NULL,
  username VARCHAR(255) NOT NULL,

  FOREIGN KEY (team_name) REFERENCES team(name),
  UNIQUE KEY (team_name, username)
);