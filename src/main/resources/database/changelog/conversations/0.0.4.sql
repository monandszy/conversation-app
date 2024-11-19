ALTER TABLE responses
  ADD COLUMN created  timestamptz NOT NULL DEFAULT NOW(),
  ADD COLUMN selected BOOLEAN     NOT NULL DEFAULT FALSE;

ALTER TABLE requests
  ADD COLUMN selected BOOLEAN NOT NULL DEFAULT FALSE;
--liquibase formatted sql