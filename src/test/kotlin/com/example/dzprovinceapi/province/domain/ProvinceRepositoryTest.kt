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
package com.example.dzprovinceapi.province.domain

import com.example.dzprovinceapi.shared.AbstractDataJpaTest
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.groups.Tuple
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class ProvinceRepositoryTest : AbstractDataJpaTest() {
    @Autowired
    private lateinit var provinceRepository: ProvinceRepository

    // ------------------------------------------------------------------
    // findProvinceByCode
    // ------------------------------------------------------------------

    @Test
    fun findProvinceByCode_returnsProvince_whenCodeExists() {
        val result = provinceRepository.findProvinceByCode(16)

        assertThat(result).isNotNull
        assertThat(result!!.code).isEqualTo(16)
        assertThat(result.slug).isEqualTo("algiers")
    }

    @Test
    fun findProvinceByCode_returnsNull_whenCodeAbsent() {
        val result = provinceRepository.findProvinceByCode(99)

        assertThat(result).isNull()
    }

    @Test
    fun findProvinceByCode_returnsExactMatch_whenMultipleExist() {
        val result = provinceRepository.findProvinceByCode(31)

        assertThat(result).isNotNull
        assertThat(result!!.slug).isEqualTo("oran")
    }

    // ------------------------------------------------------------------
    // findProvinceBySlug
    // ------------------------------------------------------------------

    @Test
    fun findProvinceBySlug_returnsProvince_whenSlugExists() {
        val result = provinceRepository.findProvinceBySlug("algiers")

        assertThat(result).isNotNull
        assertThat(result!!.code).isEqualTo(16)
    }

    @Test
    fun findProvinceBySlug_returnsNull_whenSlugAbsent() {
        val result = provinceRepository.findProvinceBySlug("abcde")

        assertThat(result).isNull()
    }

    @Test
    fun findProvinceBySlug_isCaseSensitive() {
        assertThat(provinceRepository.findProvinceBySlug("ALGIERS")).isNull()
        assertThat(provinceRepository.findProvinceBySlug("Algiers")).isNull()
        assertThat(provinceRepository.findProvinceBySlug("algiers")).isNotNull
    }

    // ------------------------------------------------------------------
    // Translations
    // ------------------------------------------------------------------

    @Test
    fun findProvinceByCode_loadsTranslations() {
        val result = provinceRepository.findProvinceByCode(16)

        assertThat(result).isNotNull
        assertThat(result!!.provinceTranslations)
            .extracting(ProvinceTranslation::name)
            .containsExactlyInAnyOrder(Tuple("Alger"), Tuple("Algiers"), Tuple("الجزائر"))
    }
}
