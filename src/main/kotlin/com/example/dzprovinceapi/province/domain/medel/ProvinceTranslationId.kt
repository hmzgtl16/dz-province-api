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
package com.example.dzprovinceapi.province.domain.medel

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import java.io.Serializable
import java.util.Objects
import java.util.UUID

@Embeddable
class ProvinceTranslationId : Serializable {
    @Column(name = "province_id")
    var provinceId: UUID? = null

    @Column(name = "language_id")
    var languageId: UUID? = null

    override fun equals(other: Any?): Boolean =
        this === other ||
            (
                other is ProvinceTranslationId &&
                    provinceId == other.provinceId &&
                    languageId == other.languageId
            )

    override fun hashCode(): Int = Objects.hash(provinceId, languageId)
}
