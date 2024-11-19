-- Liquibase SQL diff to remove DEFAULT gen_random_uuid() from id columns
-- switching to manual id generation

ALTER TABLE conversations ALTER COLUMN id DROP DEFAULT;
ALTER TABLE sections ALTER COLUMN id DROP DEFAULT;
ALTER TABLE requests ALTER COLUMN id DROP DEFAULT;
ALTER TABLE responses ALTER COLUMN id DROP DEFAULT;
--liquibase formatted sql