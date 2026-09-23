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
CREATE TABLE municipality (
  id SERIAL PRIMARY KEY,
  province_id SMALLINT NOT NULL REFERENCES province (id) ON DELETE RESTRICT,
  slug VARCHAR(100) NOT NULL,
  CONSTRAINT uq_municipality_province_slug UNIQUE (province_id, slug)
);

CREATE INDEX idx_municipality_province_id ON municipality (province_id);

CREATE TABLE municipality_translation (
  municipality_id INT NOT NULL REFERENCES municipality (id) ON DELETE CASCADE,
  language_id SMALLINT NOT NULL REFERENCES language (id) ON DELETE RESTRICT,
  name VARCHAR(150) NOT NULL,
  PRIMARY KEY (municipality_id, language_id)
);

CREATE INDEX idx_municipality_translation_name_trgm ON municipality_translation USING gin (name gin_trgm_ops);
