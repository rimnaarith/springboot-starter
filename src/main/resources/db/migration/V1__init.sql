-- Enable trigram extension - for fast substring / fuzzy search on first_name, last_name, etc.
CREATE EXTENSION IF NOT EXISTS "pg_trgm";

-- Enable unaccent extension - For full-text search with tsvector, it useful for ignoring accents in names (José → Jose)
CREATE EXTENSION IF NOT EXISTS "unaccent";