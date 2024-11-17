-- ChangeSet to create unique sections for each existing request
-- ChangeSet ID: add_unique_sections_for_each_request
-- Author: Monand
-- Date: 2024-11-09
-- Description: Create a unique section for each existing request, referencing the associated conversation,
-- add a created timestamp to sections, and remove the conversation_id column from the requests table.

-- Step 1: Create the sections table with a created timestamp
CREATE TABLE sections
(
  id              uuid        NOT NULL DEFAULT gen_random_uuid(),
  conversation_id uuid        NOT NULL,
  created         timestamptz NOT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (conversation_id) REFERENCES conversations (id)
);

-- Step 2: Add section_id column to requests table
ALTER TABLE requests
  ADD COLUMN section_id uuid;

-- Step 3: Insert a new section for each existing request, using the earliest created timestamp
INSERT INTO sections (conversation_id, created)
SELECT r.conversation_id, MIN(r.created) -- Select the earliest created timestamp for each conversation
FROM requests r
GROUP BY r.conversation_id;

-- Step 4: Update requests to link to their corresponding sections
UPDATE requests r
SET section_id = (SELECT s.id
                  FROM sections s
                  WHERE s.conversation_id = r.conversation_id
                  LIMIT 1 -- Assuming one section per conversation
);

-- Step 5: Add foreign key constraint on section_id in requests table
ALTER TABLE requests
  ADD CONSTRAINT fk_requests_section
    FOREIGN KEY (section_id) REFERENCES sections (id);

-- Step 6: Remove conversation_id column from requests table
ALTER TABLE requests
  DROP COLUMN conversation_id;
--liquibase formatted sql