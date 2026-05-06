-- Migration V55: Compress chat message storage
-- Changes message column from TEXT to MEDIUMBLOB to support GZip-compressed content.
-- The application layer (GzipStringConverter) transparently compresses on write
-- and decompresses on read — no query changes required.
--
-- Expected savings: 50-80% reduction in chat storage vs plain TEXT.
-- MEDIUMBLOB supports up to 16MB of compressed data per row.
--
-- NOTE: Existing rows with plain TEXT data will become unreadable after this migration
-- until they are re-saved through the application (which will compress them).
-- For a fresh deployment this is safe. For a live migration with existing data,
-- run a one-time data migration script before applying this.

ALTER TABLE chat_messages
    MODIFY COLUMN message MEDIUMBLOB;
