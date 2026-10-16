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
package com.example.dzprovinceapi.municipality.service

import com.example.dzprovinceapi.helpers.testLanguage
import com.example.dzprovinceapi.helpers.testMunicipality
import com.example.dzprovinceapi.helpers.testMunicipalityTranslation
import com.example.dzprovinceapi.municipality.domain.MunicipalityRepository
import com.example.dzprovinceapi.shared.error.MunicipalityCodeNotFoundException
import com.example.dzprovinceapi.shared.error.MunicipalitySlugNotFoundException
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
class MunicipalityServiceTest {
    @Mock
    private lateinit var municipalityRepository: MunicipalityRepository

    @InjectMocks
    private lateinit var municipalityService: MunicipalityServiceImpl

    // ------------------------------------------------------------------
    // getAllMunicipalities
    // ------------------------------------------------------------------

    @Test
    fun `getAllMunicipalities returns a page of mapped responses`() {
        val municipality =
            testMunicipality(code = 1601, slug = "bab-el-oued", translations = emptySet())

        val pageable = PageRequest.of(0, 10)
        val municipalityPage = PageImpl(listOf(municipality), pageable, 1)
        `when`(municipalityRepository.findAll(pageable)).thenReturn(municipalityPage)

        val result = municipalityService.getAllMunicipalities(emptyList(), pageable)

        assertThat(result.totalElements).isEqualTo(1)
        assertThat(result.content).isNotEmpty
        assertThat(result.content[0].code).isEqualTo("1601")
        assertThat(result.content[0].slug).isEqualTo(municipality.slug)
        assertThat(result.content[0].names).isEmpty()

        verify(municipalityRepository).findAll(pageable)
    }

    @Test
    fun `getAllMunicipalities falls back to default languages when none are provided`() {
        val id = UUID.randomUUID()
        val municipality =
            testMunicipality(
                id = id,
                code = 1601,
                slug = "bab-el-oued",
                translations =
                    setOf(
                        testMunicipalityTranslation(
                            municipalityId = id,
                            name = "Bab El Oued",
                            language = testLanguage(code = "fr"),
                        ),
                        testMunicipalityTranslation(
                            municipalityId = id,
                            name = "Bab El Oued",
                            language = testLanguage(code = "en"),
                        ),
                        testMunicipalityTranslation(
                            municipalityId = id,
                            name = "باب الوادي",
                            language = testLanguage(code = "ar"),
                        ),
                    ),
            )

        val pageable = PageRequest.of(0, 10)
        `when`(municipalityRepository.findAll(pageable))
            .thenReturn(PageImpl(listOf(municipality), pageable, 1))

        val result = municipalityService.getAllMunicipalities(emptyList(), pageable)

        assertThat(result.totalElements).isEqualTo(1)
        assertThat(result.content).isNotEmpty
        assertThat(result.content[0].code).isEqualTo("1601")
        assertThat(result.content[0].slug).isEqualTo(municipality.slug)
        assertThat(result.content[0].names).hasSize(municipality.municipalityTranslations.size)
        assertThat(result.content[0].names.map { it.language?.code }).containsExactlyInAnyOrder("ar", "en", "fr")

        verify(municipalityRepository).findAll(pageable)
    }

    @Test
    fun `getAllMunicipalities filters names to the requested languages`() {
        val id = UUID.randomUUID()
        val municipality =
            testMunicipality(
                id = id,
                code = 1601,
                slug = "bab-el-oued",
                translations =
                    setOf(
                        testMunicipalityTranslation(
                            municipalityId = id,
                            name = "Bab El Oued",
                            language = testLanguage(code = "fr"),
                        ),
                        testMunicipalityTranslation(
                            municipalityId = id,
                            name = "Bab El Oued",
                            language = testLanguage(code = "en"),
                        ),
                        testMunicipalityTranslation(
                            municipalityId = id,
                            name = "باب الوادي",
                            language = testLanguage(code = "ar"),
                        ),
                    ),
            )

        val pageable = PageRequest.of(0, 10)
        `when`(municipalityRepository.findAll(pageable))
            .thenReturn(PageImpl(listOf(municipality), pageable, 1))

        val result = municipalityService.getAllMunicipalities(listOf("en"), pageable)

        assertThat(result.content[0].names).hasSize(1)
        assertThat(
            result.content[0]
                .names
                .single()
                .language
                ?.code,
        ).isEqualTo("en")

        verify(municipalityRepository).findAll(pageable)
    }

    @Test
    fun `getAllMunicipalities normalizes languages by splitting on comma, trimming, lowercasing and de-duplicating`() {
        val id = UUID.randomUUID()
        val municipality =
            testMunicipality(
                id = id,
                code = 1601,
                slug = "bab-el-oued",
                translations =
                    setOf(
                        testMunicipalityTranslation(
                            municipalityId = id,
                            name = "Bab El Oued",
                            language = testLanguage(code = "fr"),
                        ),
                        testMunicipalityTranslation(
                            municipalityId = id,
                            name = "Bab El Oued",
                            language = testLanguage(code = "en"),
                        ),
                        testMunicipalityTranslation(
                            municipalityId = id,
                            name = "باب الوادي",
                            language = testLanguage(code = "ar"),
                        ),
                    ),
            )

        val pageable = PageRequest.of(0, 10)
        `when`(municipalityRepository.findAll(pageable))
            .thenReturn(PageImpl(listOf(municipality), pageable, 1))

        // " EN ", "fr,ar", "en" -> ["en", "fr", "ar"]; "12" and "e" are invalid and dropped.
        val result =
            municipalityService.getAllMunicipalities(
                listOf(" EN ", "fr,ar", "en", "12", "e"),
                pageable,
            )

        assertThat(result.content[0].names.map { it.language?.code })
            .containsExactlyInAnyOrder("en", "fr", "ar")

        verify(municipalityRepository).findAll(pageable)
    }

    @Test
    fun `getAllMunicipalities falls back to default languages when every entry is invalid`() {
        val id = UUID.randomUUID()
        val municipality =
            testMunicipality(
                id = id,
                code = 1601,
                slug = "bab-el-oued",
                translations =
                    setOf(
                        testMunicipalityTranslation(
                            municipalityId = id,
                            name = "Bab El Oued",
                            language = testLanguage(code = "fr"),
                        ),
                        testMunicipalityTranslation(
                            municipalityId = id,
                            name = "Bab El Oued",
                            language = testLanguage(code = "en"),
                        ),
                        testMunicipalityTranslation(
                            municipalityId = id,
                            name = "باب الوادي",
                            language = testLanguage(code = "ar"),
                        ),
                    ),
            )

        val pageable = PageRequest.of(0, 10)
        `when`(municipalityRepository.findAll(pageable))
            .thenReturn(PageImpl(listOf(municipality), pageable, 1))

        val result =
            municipalityService.getAllMunicipalities(listOf("1", "!!", "", "e"), pageable)

        assertThat(result.content[0].names.map { it.language?.code })
            .containsExactlyInAnyOrder("ar", "en", "fr")

        verify(municipalityRepository).findAll(pageable)
    }

    @Test
    fun `getAllMunicipalities returns an empty page when no municipalities exist`() {
        val pageable = PageRequest.of(0, 10)
        `when`(municipalityRepository.findAll(pageable))
            .thenReturn(PageImpl(emptyList(), pageable, 0))

        val result = municipalityService.getAllMunicipalities(emptyList(), pageable)

        assertThat(result.totalElements).isZero()
        assertThat(result.content).isEmpty()

        verify(municipalityRepository).findAll(pageable)
    }

    // ------------------------------------------------------------------
    // getBySlug
    // ------------------------------------------------------------------

    @Test
    fun `getBySlug returns the mapped response`() {
        val id = UUID.randomUUID()
        val municipality =
            testMunicipality(
                id = id,
                code = 1601,
                slug = "bab-el-oued",
                translations =
                    setOf(
                        testMunicipalityTranslation(
                            municipalityId = id,
                            name = "Bab El Oued",
                            language = testLanguage(code = "fr"),
                        ),
                        testMunicipalityTranslation(
                            municipalityId = id,
                            name = "Bab El Oued",
                            language = testLanguage(code = "en"),
                        ),
                        testMunicipalityTranslation(
                            municipalityId = id,
                            name = "باب الوادي",
                            language = testLanguage(code = "ar"),
                        ),
                    ),
            )

        `when`(municipalityRepository.findMunicipalityBySlug("bab-el-oued")).thenReturn(municipality)

        val result = municipalityService.getBySlug(emptyList(), "bab-el-oued")

        assertThat(result.code).isEqualTo("1601")
        assertThat(result.slug).isEqualTo("bab-el-oued")
        assertThat(result.names.map { it.name })
            .containsExactlyInAnyOrder("Bab El Oued", "Bab El Oued", "باب الوادي")
        assertThat(result.names.map { it.language?.code })
            .containsExactlyInAnyOrder("ar", "en", "fr")

        verify(municipalityRepository).findMunicipalityBySlug("bab-el-oued")
    }

    @Test
    fun `getBySlug throws MunicipalitySlugNotFoundException when no municipality matches`() {
        `when`(municipalityRepository.findMunicipalityBySlug("unknown")).thenReturn(null)

        assertThatThrownBy { municipalityService.getBySlug(emptyList(), "unknown") }
            .isInstanceOf(MunicipalitySlugNotFoundException::class.java)

        verify(municipalityRepository).findMunicipalityBySlug("unknown")
    }

    @Test
    fun `getBySlug normalizes the given languages`() {
        val id = UUID.randomUUID()
        val municipality =
            testMunicipality(
                id = id,
                code = 1601,
                slug = "bab-el-oued",
                translations =
                    setOf(
                        testMunicipalityTranslation(
                            municipalityId = id,
                            name = "Bab El Oued",
                            language = testLanguage(code = "fr"),
                        ),
                        testMunicipalityTranslation(
                            municipalityId = id,
                            name = "Bab El Oued",
                            language = testLanguage(code = "en"),
                        ),
                        testMunicipalityTranslation(
                            municipalityId = id,
                            name = "باب الوادي",
                            language = testLanguage(code = "ar"),
                        ),
                    ),
            )

        `when`(municipalityRepository.findMunicipalityBySlug("bab-el-oued")).thenReturn(municipality)

        val result =
            municipalityService.getBySlug(listOf(" FR ", "en,AR", "fr"), "bab-el-oued")

        assertThat(result.names.map { it.language?.code })
            .containsExactlyInAnyOrder("fr", "en", "ar")

        verify(municipalityRepository).findMunicipalityBySlug("bab-el-oued")
    }

    @Test
    fun `getBySlug falls back to default languages when none are provided`() {
        val id = UUID.randomUUID()
        val municipality =
            testMunicipality(
                id = id,
                code = 1601,
                slug = "bab-el-oued",
                translations =
                    setOf(
                        testMunicipalityTranslation(
                            municipalityId = id,
                            name = "Bab El Oued",
                            language = testLanguage(code = "fr"),
                        ),
                        testMunicipalityTranslation(
                            municipalityId = id,
                            name = "Bab El Oued",
                            language = testLanguage(code = "en"),
                        ),
                        testMunicipalityTranslation(
                            municipalityId = id,
                            name = "باب الوادي",
                            language = testLanguage(code = "ar"),
                        ),
                    ),
            )

        `when`(municipalityRepository.findMunicipalityBySlug("bab-el-oued")).thenReturn(municipality)

        val result = municipalityService.getBySlug(emptyList(), "bab-el-oued")

        assertThat(result.names.map { it.language?.code })
            .containsExactlyInAnyOrder("ar", "en", "fr")

        verify(municipalityRepository).findMunicipalityBySlug("bab-el-oued")
    }

    // ------------------------------------------------------------------
    // getByCode
    // ------------------------------------------------------------------

    @Test
    fun `getByCode returns the mapped response`() {
        val id = UUID.randomUUID()
        val municipality =
            testMunicipality(
                id = id,
                code = 1601,
                slug = "bab-el-oued",
                translations =
                    setOf(
                        testMunicipalityTranslation(
                            municipalityId = id,
                            name = "Bab El Oued",
                            language = testLanguage(code = "fr"),
                        ),
                        testMunicipalityTranslation(
                            municipalityId = id,
                            name = "Bab El Oued",
                            language = testLanguage(code = "en"),
                        ),
                        testMunicipalityTranslation(
                            municipalityId = id,
                            name = "باب الوادي",
                            language = testLanguage(code = "ar"),
                        ),
                    ),
            )

        `when`(municipalityRepository.findMunicipalityByCode(1601)).thenReturn(municipality)

        val result = municipalityService.getByCode(emptyList(), 1601)

        assertThat(result.code).isEqualTo("1601")
        assertThat(result.slug).isEqualTo("bab-el-oued")
        assertThat(result.names.map { it.language?.code })
            .containsExactlyInAnyOrder("ar", "en", "fr")

        verify(municipalityRepository).findMunicipalityByCode(1601)
    }

    @Test
    fun `getByCode throws MunicipalityCodeNotFoundException when no municipality matches`() {
        `when`(municipalityRepository.findMunicipalityByCode(9999)).thenReturn(null)

        assertThatThrownBy { municipalityService.getByCode(emptyList(), 9999) }
            .isInstanceOf(MunicipalityCodeNotFoundException::class.java)

        verify(municipalityRepository).findMunicipalityByCode(9999)
    }

    @Test
    fun `getByCode normalizes the given languages`() {
        val id = UUID.randomUUID()
        val municipality =
            testMunicipality(
                id = id,
                code = 1601,
                slug = "bab-el-oued",
                translations =
                    setOf(
                        testMunicipalityTranslation(
                            municipalityId = id,
                            name = "Bab El Oued",
                            language = testLanguage(code = "fr"),
                        ),
                        testMunicipalityTranslation(
                            municipalityId = id,
                            name = "Bab El Oued",
                            language = testLanguage(code = "en"),
                        ),
                        testMunicipalityTranslation(
                            municipalityId = id,
                            name = "باب الوادي",
                            language = testLanguage(code = "ar"),
                        ),
                    ),
            )

        `when`(municipalityRepository.findMunicipalityByCode(1601)).thenReturn(municipality)

        val result =
            municipalityService.getByCode(listOf("EN, fr", "en", "!"), 1601)

        assertThat(result.names.map { it.language?.code })
            .containsExactlyInAnyOrder("en", "fr")

        verify(municipalityRepository).findMunicipalityByCode(1601)
    }

    @Test
    fun `getByCode falls back to default languages when none are provided`() {
        val id = UUID.randomUUID()
        val municipality =
            testMunicipality(
                id = id,
                code = 1601,
                slug = "bab-el-oued",
                translations =
                    setOf(
                        testMunicipalityTranslation(
                            municipalityId = id,
                            name = "Bab El Oued",
                            language = testLanguage(code = "fr"),
                        ),
                        testMunicipalityTranslation(
                            municipalityId = id,
                            name = "Bab El Oued",
                            language = testLanguage(code = "en"),
                        ),
                        testMunicipalityTranslation(
                            municipalityId = id,
                            name = "باب الوادي",
                            language = testLanguage(code = "ar"),
                        ),
                    ),
            )

        `when`(municipalityRepository.findMunicipalityByCode(1601)).thenReturn(municipality)

        val result = municipalityService.getByCode(emptyList(), 1601)

        assertThat(result.names.map { it.language?.code })
            .containsExactlyInAnyOrder("ar", "en", "fr")

        verify(municipalityRepository).findMunicipalityByCode(1601)
    }
}
