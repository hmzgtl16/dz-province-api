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
package com.example.dzprovinceapi.province.service

import com.example.dzprovinceapi.helpers.testDistrict
import com.example.dzprovinceapi.helpers.testDistrictTranslation
import com.example.dzprovinceapi.helpers.testLanguage
import com.example.dzprovinceapi.helpers.testMunicipality
import com.example.dzprovinceapi.helpers.testMunicipalityTranslation
import com.example.dzprovinceapi.helpers.testProvince
import com.example.dzprovinceapi.helpers.testProvinceTranslation
import com.example.dzprovinceapi.province.domain.ProvinceRepository
import com.example.dzprovinceapi.shared.error.ProvinceCodeNotFoundException
import com.example.dzprovinceapi.shared.error.ProvinceSlugNotFoundException
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.test.context.ActiveProfiles
import java.util.UUID

@ExtendWith(MockitoExtension::class)
@ActiveProfiles("test")
class ProvinceServiceTest {
    @Mock
    private lateinit var provinceRepository: ProvinceRepository

    @InjectMocks
    private lateinit var provinceService: ProvinceServiceImpl

    // ------------------------------------------------------------------
    // getAllProvinces
    // ------------------------------------------------------------------

    @Test
    fun `getAllProvinces returns a page of mapped responses`() {
        val province =
            testProvince(code = 16, slug = "algiers", translations = emptySet())

        val pageable = PageRequest.of(0, 10)
        val provincePage = PageImpl(listOf(province), pageable, 1)
        `when`(provinceRepository.findAll(pageable)).thenReturn(provincePage)

        val result = provinceService.getProvinces(emptyList(), pageable)

        assertThat(result.totalElements).isEqualTo(1)
        assertThat(result.content).isNotEmpty
        assertThat(result.content[0].code).isEqualTo("16")
        assertThat(result.content[0].slug).isEqualTo(province.slug)
        assertThat(result.content[0].names).isEmpty()

        verify(provinceRepository).findAll(pageable)
    }

    @Test
    fun `getAllProvinces falls back to default languages when none are provided`() {
        val id = UUID.randomUUID()
        val province =
            testProvince(
                id = id,
                code = 16,
                slug = "algiers",
                translations =
                    setOf(
                        testProvinceTranslation(provinceId = id, name = "Alger", language = testLanguage(code = "fr")),
                        testProvinceTranslation(
                            provinceId = id,
                            name = "Algiers",
                            language = testLanguage(code = "en"),
                        ),
                        testProvinceTranslation(
                            provinceId = id,
                            name = "الجزائر",
                            language = testLanguage(code = "ar"),
                        ),
                    ),
            )

        val pageable = PageRequest.of(0, 10)
        `when`(provinceRepository.findAll(pageable))
            .thenReturn(PageImpl(listOf(province), pageable, 1))

        val result = provinceService.getProvinces(emptyList(), pageable)

        assertThat(result.totalElements).isEqualTo(1)
        assertThat(result.content).isNotEmpty
        assertThat(result.content[0].code).isEqualTo("16")
        assertThat(result.content[0].slug).isEqualTo(province.slug)
        assertThat(result.content[0].names).hasSize(province.provinceTranslations.size)
        assertThat(result.content[0].names.map { it.language?.code }).containsExactlyInAnyOrder("ar", "en", "fr")

        verify(provinceRepository).findAll(pageable)
    }

    @Test
    fun `getAllProvinces filters names to the requested languages`() {
        val id = UUID.randomUUID()
        val province =
            testProvince(
                id = id,
                code = 16,
                slug = "algiers",
                translations =
                    setOf(
                        testProvinceTranslation(provinceId = id, name = "Alger", language = testLanguage(code = "fr")),
                        testProvinceTranslation(
                            provinceId = id,
                            name = "Algiers",
                            language = testLanguage(code = "en"),
                        ),
                        testProvinceTranslation(
                            provinceId = id,
                            name = "الجزائر",
                            language = testLanguage(code = "ar"),
                        ),
                    ),
            )

        val pageable = PageRequest.of(0, 10)
        `when`(provinceRepository.findAll(pageable))
            .thenReturn(PageImpl(listOf(province), pageable, 1))

        val result = provinceService.getProvinces(listOf("en"), pageable)

        assertThat(result.content[0].names).hasSize(1)
        assertThat(
            result.content[0]
                .names
                .single()
                .language
                ?.code,
        ).isEqualTo("en")

        verify(provinceRepository).findAll(pageable)
    }

    @Test
    fun `getAllProvinces normalizes languages by splitting on comma, trimming, lowercasing and de-duplicating`() {
        val id = UUID.randomUUID()
        val province =
            testProvince(
                id = id,
                code = 16,
                slug = "algiers",
                translations =
                    setOf(
                        testProvinceTranslation(provinceId = id, name = "Alger", language = testLanguage(code = "fr")),
                        testProvinceTranslation(
                            provinceId = id,
                            name = "Algiers",
                            language = testLanguage(code = "en"),
                        ),
                        testProvinceTranslation(
                            provinceId = id,
                            name = "الجزائر",
                            language = testLanguage(code = "ar"),
                        ),
                    ),
            )

        val pageable = PageRequest.of(0, 10)
        `when`(provinceRepository.findAll(pageable))
            .thenReturn(PageImpl(listOf(province), pageable, 1))

        // " EN ", "fr,ar", "en" -> ["en", "fr", "ar"]; "12" and "e" are invalid and dropped.
        val result =
            provinceService.getProvinces(
                listOf(" EN ", "fr,ar", "en", "12", "e"),
                pageable,
            )

        assertThat(result.content[0].names.map { it.language?.code })
            .containsExactlyInAnyOrder("en", "fr", "ar")

        verify(provinceRepository).findAll(pageable)
    }

    @Test
    fun `getAllProvinces falls back to default languages when every entry is invalid`() {
        val id = UUID.randomUUID()
        val province =
            testProvince(
                id = id,
                code = 16,
                slug = "algiers",
                translations =
                    setOf(
                        testProvinceTranslation(provinceId = id, name = "Alger", language = testLanguage(code = "fr")),
                        testProvinceTranslation(
                            provinceId = id,
                            name = "Algiers",
                            language = testLanguage(code = "en"),
                        ),
                        testProvinceTranslation(
                            provinceId = id,
                            name = "الجزائر",
                            language = testLanguage(code = "ar"),
                        ),
                    ),
            )

        val pageable = PageRequest.of(0, 10)
        `when`(provinceRepository.findAll(pageable))
            .thenReturn(PageImpl(listOf(province), pageable, 1))

        val result =
            provinceService.getProvinces(listOf("1", "!!", "", "e"), pageable)

        assertThat(result.content[0].names.map { it.language?.code })
            .containsExactlyInAnyOrder("ar", "en", "fr")

        verify(provinceRepository).findAll(pageable)
    }

    @Test
    fun `getAllProvinces returns an empty page when no provinces exist`() {
        val pageable = PageRequest.of(0, 10)
        `when`(provinceRepository.findAll(pageable))
            .thenReturn(PageImpl(emptyList(), pageable, 0))

        val result = provinceService.getProvinces(emptyList(), pageable)

        assertThat(result.totalElements).isZero()
        assertThat(result.content).isEmpty()

        verify(provinceRepository).findAll(pageable)
    }

    // ------------------------------------------------------------------
    // getByCode
    // ------------------------------------------------------------------

    @Test
    fun `getByCode returns the mapped response`() {
        val id = UUID.randomUUID()
        val province =
            testProvince(
                id = id,
                code = 16,
                slug = "algiers",
                translations =
                    setOf(
                        testProvinceTranslation(provinceId = id, name = "Alger", language = testLanguage(code = "fr")),
                        testProvinceTranslation(
                            provinceId = id,
                            name = "Algiers",
                            language = testLanguage(code = "en"),
                        ),
                        testProvinceTranslation(
                            provinceId = id,
                            name = "الجزائر",
                            language = testLanguage(code = "ar"),
                        ),
                    ),
            )

        `when`(provinceRepository.findProvinceByCode(16)).thenReturn(province)

        val result = provinceService.getProvinceByCode(emptyList(), 16)

        assertThat(result.code).isEqualTo("16")
        assertThat(result.slug).isEqualTo("algiers")
        assertThat(result.names.map { it.language?.code })
            .containsExactlyInAnyOrder("ar", "en", "fr")

        verify(provinceRepository).findProvinceByCode(16)
    }

    @Test
    fun `getByCode throws ProvinceCodeNotFoundException when no province matches`() {
        `when`(provinceRepository.findProvinceByCode(99)).thenReturn(null)

        assertThatThrownBy { provinceService.getProvinceByCode(emptyList(), 99) }
            .isInstanceOf(ProvinceCodeNotFoundException::class.java)

        verify(provinceRepository).findProvinceByCode(99)
    }

    @Test
    fun `getByCode normalizes the given languages`() {
        val id = UUID.randomUUID()
        val province =
            testProvince(
                id = id,
                code = 16,
                slug = "algiers",
                translations =
                    setOf(
                        testProvinceTranslation(provinceId = id, name = "Alger", language = testLanguage(code = "fr")),
                        testProvinceTranslation(
                            provinceId = id,
                            name = "Algiers",
                            language = testLanguage(code = "en"),
                        ),
                        testProvinceTranslation(
                            provinceId = id,
                            name = "الجزائر",
                            language = testLanguage(code = "ar"),
                        ),
                    ),
            )

        `when`(provinceRepository.findProvinceByCode(16)).thenReturn(province)

        val result =
            provinceService.getProvinceByCode(listOf("EN, fr", "en", "!"), 16)

        assertThat(result.names.map { it.language?.code })
            .containsExactlyInAnyOrder("en", "fr")

        verify(provinceRepository).findProvinceByCode(16)
    }

    @Test
    fun `getByCode falls back to default languages when none are provided`() {
        val id = UUID.randomUUID()
        val province =
            testProvince(
                id = id,
                code = 16,
                slug = "algiers",
                translations =
                    setOf(
                        testProvinceTranslation(provinceId = id, name = "Alger", language = testLanguage(code = "fr")),
                        testProvinceTranslation(
                            provinceId = id,
                            name = "Algiers",
                            language = testLanguage(code = "en"),
                        ),
                        testProvinceTranslation(
                            provinceId = id,
                            name = "الجزائر",
                            language = testLanguage(code = "ar"),
                        ),
                    ),
            )

        `when`(provinceRepository.findProvinceByCode(16)).thenReturn(province)

        val result = provinceService.getProvinceByCode(emptyList(), 16)

        assertThat(result.names.map { it.language?.code })
            .containsExactlyInAnyOrder("ar", "en", "fr")

        verify(provinceRepository).findProvinceByCode(16)
    }

    // ------------------------------------------------------------------
    // getProvinceDistrictsByCode
    // ------------------------------------------------------------------

    @Test
    fun `getProvinceDistrictsByCode returns a page of mapped district responses`() {
        val districtId = UUID.randomUUID()
        val district =
            testDistrict(
                id = districtId,
                slug = "bab-el-oued",
                translations =
                    setOf(
                        testDistrictTranslation(
                            districtId = districtId,
                            name = "Bab El Oued",
                            language = testLanguage(code = "fr"),
                        ),
                        testDistrictTranslation(
                            districtId = districtId,
                            name = "Bab El Oued",
                            language = testLanguage(code = "en"),
                        ),
                        testDistrictTranslation(
                            districtId = districtId,
                            name = "باب الوادي",
                            language = testLanguage(code = "ar"),
                        ),
                    ),
            )
        val province =
            testProvince(
                code = 16,
                slug = "algiers",
                districts = setOf(district),
            )

        val pageable = PageRequest.of(0, 10)
        `when`(provinceRepository.findProvinceByCode(16)).thenReturn(province)

        val result = provinceService.getProvinceDistrictsByCode(emptyList(), 16, pageable)

        assertThat(result.totalElements).isEqualTo(1)
        assertThat(result.content).hasSize(1)
        assertThat(result.content[0].slug).isEqualTo("bab-el-oued")
        assertThat(result.content[0].names.map { it.language?.code })
            .containsExactlyInAnyOrder("ar", "en", "fr")

        verify(provinceRepository).findProvinceByCode(16)
    }

    @Test
    fun `getProvinceDistrictsByCode throws ProvinceCodeNotFoundException when no province matches`() {
        val pageable = PageRequest.of(0, 10)
        `when`(provinceRepository.findProvinceByCode(99)).thenReturn(null)

        assertThatThrownBy { provinceService.getProvinceDistrictsByCode(emptyList(), 99, pageable) }
            .isInstanceOf(ProvinceCodeNotFoundException::class.java)

        verify(provinceRepository).findProvinceByCode(99)
    }

    @Test
    fun `getProvinceDistrictsByCode filters and normalizes the given languages`() {
        val districtId = UUID.randomUUID()
        val district =
            testDistrict(
                id = districtId,
                slug = "bab-el-oued",
                translations =
                    setOf(
                        testDistrictTranslation(
                            districtId = districtId,
                            name = "Bab El Oued",
                            language = testLanguage(code = "fr"),
                        ),
                        testDistrictTranslation(
                            districtId = districtId,
                            name = "Bab El Oued",
                            language = testLanguage(code = "en"),
                        ),
                        testDistrictTranslation(
                            districtId = districtId,
                            name = "باب الوادي",
                            language = testLanguage(code = "ar"),
                        ),
                    ),
            )
        val province =
            testProvince(
                code = 16,
                slug = "algiers",
                districts = setOf(district),
            )

        val pageable = PageRequest.of(0, 10)
        `when`(provinceRepository.findProvinceByCode(16)).thenReturn(province)

        val result =
            provinceService.getProvinceDistrictsByCode(listOf("EN, fr", "en", "!"), 16, pageable)

        assertThat(result.content[0].names.map { it.language?.code })
            .containsExactlyInAnyOrder("en", "fr")

        verify(provinceRepository).findProvinceByCode(16)
    }

    @Test
    fun `getProvinceDistrictsByCode returns an empty page when province has no districts`() {
        val province =
            testProvince(
                code = 16,
                slug = "algiers",
                districts = emptySet(),
            )

        val pageable = PageRequest.of(0, 10)
        `when`(provinceRepository.findProvinceByCode(16)).thenReturn(province)

        val result = provinceService.getProvinceDistrictsByCode(emptyList(), 16, pageable)

        assertThat(result.totalElements).isZero()
        assertThat(result.content).isEmpty()

        verify(provinceRepository).findProvinceByCode(16)
    }

    // ------------------------------------------------------------------
    // getProvinceMunicipalitiesByCode
    // ------------------------------------------------------------------

    @Test
    fun `getProvinceMunicipalitiesByCode returns a page of flattened municipality responses across districts`() {
        val municipality1Id = UUID.randomUUID()
        val municipality1 =
            testMunicipality(
                id = municipality1Id,
                code = 1601,
                slug = "bab-el-oued",
                translations =
                    setOf(
                        testMunicipalityTranslation(
                            municipalityId = municipality1Id,
                            name = "Bab El Oued",
                            language = testLanguage(code = "fr"),
                        ),
                        testMunicipalityTranslation(
                            municipalityId = municipality1Id,
                            name = "Bab El Oued",
                            language = testLanguage(code = "en"),
                        ),
                        testMunicipalityTranslation(
                            municipalityId = municipality1Id,
                            name = "باب الوادي",
                            language = testLanguage(code = "ar"),
                        ),
                    ),
            )

        val municipality2Id = UUID.randomUUID()
        val municipality2 =
            testMunicipality(
                id = municipality2Id,
                code = 1602,
                slug = "casbah",
                translations =
                    setOf(
                        testMunicipalityTranslation(
                            municipalityId = municipality2Id,
                            name = "Casbah",
                            language = testLanguage(code = "fr"),
                        ),
                        testMunicipalityTranslation(
                            municipalityId = municipality2Id,
                            name = "Casbah",
                            language = testLanguage(code = "en"),
                        ),
                        testMunicipalityTranslation(
                            municipalityId = municipality2Id,
                            name = "القصبة",
                            language = testLanguage(code = "ar"),
                        ),
                    ),
            )

        val district1 = testDistrict(slug = "bab-el-oued", municipalities = setOf(municipality1))
        val district2 = testDistrict(slug = "sidi-m-hamed", municipalities = setOf(municipality2))

        val province =
            testProvince(
                code = 16,
                slug = "algiers",
                districts = setOf(district1, district2),
            )

        val pageable = PageRequest.of(0, 10)
        `when`(provinceRepository.findProvinceByCode(16)).thenReturn(province)

        val result = provinceService.getProvinceMunicipalitiesByCode(emptyList(), 16, pageable)

        assertThat(result.totalElements).isEqualTo(2)
        assertThat(result.content).hasSize(2)
        assertThat(result.content.map { it.slug })
            .containsExactlyInAnyOrder("bab-el-oued", "casbah")
        assertThat(result.content[0].names.map { it.language?.code })
            .containsExactlyInAnyOrder("ar", "en", "fr")

        verify(provinceRepository).findProvinceByCode(16)
    }

    @Test
    fun `getProvinceMunicipalitiesByCode throws ProvinceCodeNotFoundException when no province matches`() {
        val pageable = PageRequest.of(0, 10)
        `when`(provinceRepository.findProvinceByCode(99)).thenReturn(null)

        assertThatThrownBy { provinceService.getProvinceMunicipalitiesByCode(emptyList(), 99, pageable) }
            .isInstanceOf(ProvinceCodeNotFoundException::class.java)

        verify(provinceRepository).findProvinceByCode(99)
    }

    @Test
    fun `getProvinceMunicipalitiesByCode filters and normalizes the given languages`() {
        val municipalityId = UUID.randomUUID()
        val municipality =
            testMunicipality(
                id = municipalityId,
                code = 1601,
                slug = "bab-el-oued",
                translations =
                    setOf(
                        testMunicipalityTranslation(
                            municipalityId = municipalityId,
                            name = "Bab El Oued",
                            language = testLanguage(code = "fr"),
                        ),
                        testMunicipalityTranslation(
                            municipalityId = municipalityId,
                            name = "Bab El Oued",
                            language = testLanguage(code = "en"),
                        ),
                        testMunicipalityTranslation(
                            municipalityId = municipalityId,
                            name = "باب الوادي",
                            language = testLanguage(code = "ar"),
                        ),
                    ),
            )
        val district = testDistrict(slug = "bab-el-oued", municipalities = setOf(municipality))
        val province =
            testProvince(
                code = 16,
                slug = "algiers",
                districts = setOf(district),
            )

        val pageable = PageRequest.of(0, 10)
        `when`(provinceRepository.findProvinceByCode(16)).thenReturn(province)

        val result =
            provinceService.getProvinceMunicipalitiesByCode(listOf(" AR , en ", "12", "!"), 16, pageable)

        assertThat(result.content[0].names.map { it.language?.code })
            .containsExactlyInAnyOrder("ar", "en")

        verify(provinceRepository).findProvinceByCode(16)
    }

    @Test
    fun `getProvinceMunicipalitiesByCode returns an empty page when province districts have no municipalities`() {
        val district = testDistrict(slug = "bab-el-oued", municipalities = emptySet())
        val province =
            testProvince(
                code = 16,
                slug = "algiers",
                districts = setOf(district),
            )

        val pageable = PageRequest.of(0, 10)
        `when`(provinceRepository.findProvinceByCode(16)).thenReturn(province)

        val result = provinceService.getProvinceMunicipalitiesByCode(emptyList(), 16, pageable)

        assertThat(result.totalElements).isZero()
        assertThat(result.content).isEmpty()

        verify(provinceRepository).findProvinceByCode(16)
    }
}
