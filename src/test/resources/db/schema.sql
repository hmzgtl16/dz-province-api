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

CREATE TABLE IF NOT EXISTS language (
  id UUID PRIMARY KEY DEFAULT uuidv7 (),
  code VARCHAR(2) NOT NULL UNIQUE, -- ISO 639-1: ar, fr, en
  name VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS province (
  id UUID PRIMARY KEY DEFAULT uuidv7 (),
  code SMALLINT NOT NULL UNIQUE,
  slug VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS province_translation (
  language_id UUID NOT NULL REFERENCES language (id) ON DELETE RESTRICT,
  province_id UUID NOT NULL REFERENCES province (id) ON DELETE CASCADE,
  name VARCHAR(150) NOT NULL,
  PRIMARY KEY (province_id, language_id)
);

CREATE TABLE IF NOT EXISTS district (
  id UUID PRIMARY KEY DEFAULT uuidv7 (),
  slug VARCHAR(100) NOT NULL,
  province_id UUID NOT NULL REFERENCES province (id) ON DELETE RESTRICT,
  CONSTRAINT uq_district_province_slug UNIQUE (province_id, slug)
);

CREATE TABLE IF NOT EXISTS district_translation (
  language_id UUID NOT NULL REFERENCES language (id) ON DELETE RESTRICT,
  district_id UUID NOT NULL REFERENCES district (id) ON DELETE CASCADE,
  name VARCHAR(150) NOT NULL,
  PRIMARY KEY (district_id, language_id)
);

CREATE INDEX IF NOT EXISTS idx_province_translation_name_trgm ON province_translation USING gin (name gin_trgm_ops);

CREATE INDEX IF NOT EXISTS idx_district_translation_name_trgm ON district_translation USING gin (name gin_trgm_ops);
