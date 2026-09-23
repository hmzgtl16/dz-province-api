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
CREATE TABLE postal_code (
  id SERIAL PRIMARY KEY,
  municipality_id INT NOT NULL REFERENCES municipality (id) ON DELETE CASCADE,
  code VARCHAR(5) NOT NULL,
  locality_name VARCHAR(150),
  CONSTRAINT uq_postal_code_municipality_code_locality UNIQUE (municipality_id, code, locality_name)
);

CREATE INDEX idx_postal_code_municipality_id ON postal_code (municipality_id);

CREATE INDEX idx_postal_code_code ON postal_code (code);
