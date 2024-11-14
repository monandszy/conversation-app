-- Request entity indexes
CREATE INDEX idx_request_section_selected ON requests (section_id, selected);
CREATE INDEX idx_request_section_created ON requests (section_id, created);

-- Response entity indexes
CREATE INDEX idx_response_request_selected ON responses (request_id, selected);
CREATE INDEX idx_response_request_created ON responses (request_id, created);

-- Section entity index
CREATE INDEX idx_section_conversation ON sections (conversation_id);