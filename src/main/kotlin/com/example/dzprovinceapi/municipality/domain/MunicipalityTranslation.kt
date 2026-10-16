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
package com.example.dzprovinceapi.municipality.domain

import com.example.dzprovinceapi.language.domain.Language
import jakarta.persistence.Column
import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.MapsId
import jakarta.persistence.Table
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction

@Entity
@Table(name = "municipality_translation", schema = "public")
class MunicipalityTranslation {
    @EmbeddedId
    var id: MunicipalityTranslationId? = null

    @MapsId("municipalityId")
    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "municipality_id")
    var municipality: Municipality? = null

    @MapsId("languageId")
    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "language_id")
    var language: Language? = null

    @Column(name = "name")
    var name: String = ""
}
