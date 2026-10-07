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
package com.example.dzprovinceapi.district.domain

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.validation.constraints.NotNull
import org.hibernate.Hibernate
import java.io.Serializable
import java.util.Objects
import java.util.UUID

@Embeddable
class DistrictTranslationId : Serializable {
    @Column(name = "district_id", nullable = false)
    var districtId: UUID? = null

    @Column(name = "language_id", nullable = false)
    var languageId: UUID? = null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false

        other as DistrictTranslationId

        return districtId == other.districtId &&
            languageId == other.languageId
    }

    override fun hashCode(): Int = Objects.hash(districtId, languageId)

    companion object {
        private const val serialVersionUID = 0L
    }
}
