CREATE TABLE conversations
(
  id uuid NOT NULL DEFAULT gen_random_uuid(),
  account_id uuid NOT NULL,
  PRIMARY KEY (id)
);
CREATE TABLE requests
(
  id uuid NOT NULL DEFAULT gen_random_uuid(),
  conversation_id uuid NOT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (conversation_id) REFERENCES conversations (id)
);
CREATE TABLE responses
(
  id uuid NOT NULL DEFAULT gen_random_uuid(),
  request_id uuid NOT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (request_id) REFERENCES requests (id)
);
--liquibase formatted sql