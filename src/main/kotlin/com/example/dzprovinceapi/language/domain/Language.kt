/*
 * Copyright 2026 Hamza Gattal
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.dzprovinceapi.language.domain

import com.example.dzprovinceapi.province.domain.ProvinceTranslation
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "language", schema = "public")
class Language {
    @Id
    var id: UUID? = null

    @Column(name = "code")
    var code: String = ""

    @Column(name = "name")
    var name: String = ""

    @OneToMany
    @JoinColumn(name = "language_id")
    var provinceTranslations: MutableSet<ProvinceTranslation> = mutableSetOf()
}
