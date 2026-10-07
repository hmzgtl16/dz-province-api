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

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.groups.Tuple
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest
import org.springframework.test.context.ActiveProfiles

@DataJpaTest
@ActiveProfiles("test")
class DistrictRepositoryTest {
    @Autowired
    private lateinit var districtRepository: DistrictRepository

    // ------------------------------------------------------------------
    // findDistrictBySlug
    // ------------------------------------------------------------------

    @Test
    fun findDistrictBySlug_returnsDistrict_whenSlugExists() {
        val result = districtRepository.findDistrictBySlug("bab-el-oued")

        assertThat(result).isNotNull
        assertThat(result!!.slug).isEqualTo("bab-el-oued")
    }

    @Test
    fun findDistrictBySlug_returnsNull_whenSlugAbsent() {
        val result = districtRepository.findDistrictBySlug("non-existing-slug")

        assertThat(result).isNull()
    }

    @Test
    fun findDistrictBySlug_isCaseSensitive() {
        assertThat(districtRepository.findDistrictBySlug("BAB-EL-OUED")).isNull()
        assertThat(districtRepository.findDistrictBySlug("Bab-El-Oued")).isNull()
        assertThat(districtRepository.findDistrictBySlug("bab-el-oued")).isNotNull
    }

    @Test
    fun findDistrictBySlug_returnsNull_whenSlugIsEmpty() {
        val result = districtRepository.findDistrictBySlug("")

        assertThat(result).isNull()
    }

    // ------------------------------------------------------------------
    // Translations
    // ------------------------------------------------------------------

    @Test
    fun findDistrictBySlug_loadsTranslations() {
        val result = districtRepository.findDistrictBySlug("bab-el-oued")

        assertThat(result!!.districtTranslations)
            .extracting(DistrictTranslation::name)
            .containsExactlyInAnyOrder(Tuple("Bab El Oued"), Tuple("باب الوادي"), Tuple("Bab El Oued"))
    }
}
