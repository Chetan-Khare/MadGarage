-- V27: Allow Guest Part Requests by removing the NOT NULL constraint on user_id
-- This enabling users to submit requests without being logged in.

ALTER TABLE part_requests MODIFY user_id BIGINT NULL;
