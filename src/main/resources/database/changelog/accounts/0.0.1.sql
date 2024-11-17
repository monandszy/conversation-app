CREATE TABLE accounts
(
  id       uuid DEFAULT gen_random_uuid(),
  email    VARCHAR(50)  NOT NULL,
  password VARCHAR(500) NOT NULL,
  enabled  BOOLEAN      NOT NULL,
  PRIMARY KEY (id)
);
CREATE UNIQUE INDEX unique_email ON accounts (LOWER(email));

CREATE TABLE authorities
(
  id   uuid DEFAULT gen_random_uuid(),
  name VARCHAR(50) NOT NULL,
  PRIMARY KEY (id)
);
CREATE UNIQUE INDEX unique_name ON authorities (LOWER(name));

CREATE TABLE accounts_authorities
(
  account_id   uuid NOT NULL,
  authority_id uuid NOT NULL,
  FOREIGN KEY (account_id) REFERENCES accounts (id),
  FOREIGN KEY (authority_id) REFERENCES authorities (id)
);
CREATE UNIQUE INDEX ix_authorization ON accounts_authorities (account_id, authority_id);
--liquibase formatted sql
-- https://docs.spring.io/spring-security/reference/servlet/authentication/passwords/jdbc.html
-- TODO replace with centralized account storage.