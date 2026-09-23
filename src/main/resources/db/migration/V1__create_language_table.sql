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

CREATE TABLE language (
  id SMALLINT PRIMARY KEY,
  code VARCHAR(2) NOT NULL UNIQUE, -- ISO 639-1: ar, fr, en
  name VARCHAR(50) NOT NULL,
  is_default BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE UNIQUE INDEX idx_language_single_default ON language (is_default)
WHERE
  is_default = TRUE;

INSERT INTO
  language (id, code, name, is_default)
VALUES
  (1, 'fr', 'Français', TRUE),
  (2, 'ar', 'العربية', FALSE),
  (3, 'en', 'English', FALSE);
