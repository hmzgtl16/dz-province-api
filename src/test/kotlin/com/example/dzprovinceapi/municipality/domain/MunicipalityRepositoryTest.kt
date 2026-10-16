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

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.groups.Tuple
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest
import org.springframework.test.context.ActiveProfiles

@DataJpaTest
@ActiveProfiles("test")
class MunicipalityRepositoryTest {
    @Autowired
    private lateinit var municipalityRepository: MunicipalityRepository

    // ------------------------------------------------------------------
    // findMunicipalityByCode
    // ------------------------------------------------------------------

    @Test
    fun findMunicipalityByCode_returnsMunicipality_whenCodeExists() {
        val result = municipalityRepository.findMunicipalityByCode(1601)

        assertThat(result).isNotNull
        assertThat(result!!.code).isEqualTo(1601)
        assertThat(result.slug).isEqualTo("bab-el-oued")
    }

    @Test
    fun findMunicipalityByCode_returnsNull_whenCodeAbsent() {
        val result = municipalityRepository.findMunicipalityByCode(9999)

        assertThat(result).isNull()
    }

    @Test
    fun findMunicipalityByCode_returnsExactMatch_whenMultipleExist() {
        val result = municipalityRepository.findMunicipalityByCode(1601)

        assertThat(result).isNotNull
        assertThat(result!!.slug).isEqualTo("bab-el-oued")
    }

    // ------------------------------------------------------------------
    // findMunicipalityBySlug
    // ------------------------------------------------------------------

    @Test
    fun findMunicipalityBySlug_returnsMunicipality_whenSlugExists() {
        val result = municipalityRepository.findMunicipalityBySlug("bab-el-oued")

        assertThat(result).isNotNull
        assertThat(result!!.code).isEqualTo(1601)
    }

    @Test
    fun findMunicipalityBySlug_returnsNull_whenSlugAbsent() {
        val result = municipalityRepository.findMunicipalityBySlug("abcde")

        assertThat(result).isNull()
    }

    @Test
    fun findMunicipalityBySlug_isCaseSensitive() {
        assertThat(municipalityRepository.findMunicipalityBySlug("BAB-EL-OUED")).isNull()
        assertThat(municipalityRepository.findMunicipalityBySlug("Bab-El-Oued")).isNull()
        assertThat(municipalityRepository.findMunicipalityBySlug("bab-el-oued")).isNotNull
    }

    // ------------------------------------------------------------------
    // Translations
    // ------------------------------------------------------------------

    @Test
    fun findMunicipalityByCode_loadsTranslations() {
        val result = municipalityRepository.findMunicipalityByCode(1601)

        assertThat(result).isNotNull
        assertThat(result!!.municipalityTranslations)
            .extracting(MunicipalityTranslation::name)
            .contains(Tuple("Bab El Oued"), Tuple("Bab El Oued"), Tuple("باب الوادي"))
    }
}
