-- Copyright 2026 Hamza Gattal
--
-- Licensed under the Apache License, Version 2.0 (the "License");
-- you may not use this file except in compliance with the License.
-- You may obtain a copy of the License at
--
--     http://www.apache.org/licenses/LICENSE-2.0
--
-- Unless required by applicable law or agreed to in writing, software
-- distributed under the License is distributed on an "AS IS" BASIS,
-- WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
-- See the License for the specific language governing permissions and
-- limitations under the License.
CREATE EXTENSION IF NOT EXISTS pg_trgm SCHEMA public;

CREATE TABLE province (
  id SMALLINT PRIMARY KEY,
  slug VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE province_translation (
  name VARCHAR(150) NOT NULL,
  language VARCHAR(2) NOT NULL CHECK (language IN ('ar', 'fr', 'en')),
  province_id SMALLINT NOT NULL REFERENCES province (id) ON DELETE CASCADE,
  PRIMARY KEY (province_id, language)
);

CREATE INDEX idx_province_translation_name_trgm ON province_translation USING gin (name gin_trgm_ops);
