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

import com.example.dzprovinceapi.language.domain.Language
import com.example.dzprovinceapi.province.domain.Province
import com.example.dzprovinceapi.province.domain.ProvinceRepository
import com.example.dzprovinceapi.province.domain.ProvinceTranslation
import com.example.dzprovinceapi.province.domain.ProvinceTranslationId
import com.example.dzprovinceapi.shared.error.ProvinceCodeNotFoundException
import com.example.dzprovinceapi.shared.error.ProvinceSlugNotFoundException
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import java.util.UUID

@ExtendWith(MockitoExtension::class)
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
            province(code = "16", slug = "algiers", translations = emptySet())

        val pageable = PageRequest.of(0, 10)
        val provincePage = PageImpl(listOf(province), pageable, 1)
        `when`(provinceRepository.findAll(pageable)).thenReturn(provincePage)

        val result = provinceService.getAllProvinces(emptyList(), pageable)

        assertThat(result.totalElements).isEqualTo(1)
        assertThat(result.content).isNotEmpty
        assertThat(result.content[0].code).isEqualTo(province.code)
        assertThat(result.content[0].slug).isEqualTo(province.slug)
        assertThat(result.content[0].names).isEmpty()

        verify(provinceRepository).findAll(pageable)
    }

    @Test
    fun `getAllProvinces falls back to default languages when none are provided`() {
        val id = UUID.randomUUID()
        val province =
            province(
                id = id,
                code = "16",
                slug = "algiers",
                translations =
                    setOf(
                        translation(provinceId = id, name = "Alger", language = "fr"),
                        translation(provinceId = id, name = "Algiers", language = "en"),
                        translation(provinceId = id, name = "الجزائر", language = "ar"),
                    ),
            )

        val pageable = PageRequest.of(0, 10)
        `when`(provinceRepository.findAll(pageable))
            .thenReturn(PageImpl(listOf(province), pageable, 1))

        val result = provinceService.getAllProvinces(emptyList(), pageable)

        assertThat(result.totalElements).isEqualTo(1)
        assertThat(result.content).isNotEmpty
        assertThat(result.content[0].code).isEqualTo(province.code)
        assertThat(result.content[0].slug).isEqualTo(province.slug)
        assertThat(result.content[0].names).hasSize(province.provinceTranslations.size)
        assertThat(result.content[0].names.map { it.language?.code }).containsExactlyInAnyOrder("ar", "en", "fr")

        verify(provinceRepository).findAll(pageable)
    }

    @Test
    fun `getAllProvinces filters names to the requested languages`() {
        val id = UUID.randomUUID()
        val province =
            province(
                id = id,
                code = "16",
                slug = "algiers",
                translations =
                    setOf(
                        translation(provinceId = id, name = "Alger", language = "fr"),
                        translation(provinceId = id, name = "Algiers", language = "en"),
                        translation(provinceId = id, name = "الجزائر", language = "ar"),
                    ),
            )

        val pageable = PageRequest.of(0, 10)
        `when`(provinceRepository.findAll(pageable))
            .thenReturn(PageImpl(listOf(province), pageable, 1))

        val result = provinceService.getAllProvinces(listOf("en"), pageable)

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
            province(
                id = id,
                code = "16",
                slug = "algiers",
                translations =
                    setOf(
                        translation(provinceId = id, name = "Alger", language = "fr"),
                        translation(provinceId = id, name = "Algiers", language = "en"),
                        translation(provinceId = id, name = "الجزائر", language = "ar"),
                    ),
            )

        val pageable = PageRequest.of(0, 10)
        `when`(provinceRepository.findAll(pageable))
            .thenReturn(PageImpl(listOf(province), pageable, 1))

        // " EN ", "fr,ar", "en" -> ["en", "fr", "ar"]; "12" and "e" are invalid and dropped.
        val result =
            provinceService.getAllProvinces(
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
            province(
                id = id,
                code = "16",
                slug = "algiers",
                translations =
                    setOf(
                        translation(provinceId = id, name = "Alger", language = "fr"),
                        translation(provinceId = id, name = "Algiers", language = "en"),
                        translation(provinceId = id, name = "الجزائر", language = "ar"),
                    ),
            )

        val pageable = PageRequest.of(0, 10)
        `when`(provinceRepository.findAll(pageable))
            .thenReturn(PageImpl(listOf(province), pageable, 1))

        val result =
            provinceService.getAllProvinces(listOf("1", "!!", "", "e"), pageable)

        assertThat(result.content[0].names.map { it.language?.code })
            .containsExactlyInAnyOrder("ar", "en", "fr")

        verify(provinceRepository).findAll(pageable)
    }

    @Test
    fun `getAllProvinces returns an empty page when no provinces exist`() {
        val pageable = PageRequest.of(0, 10)
        `when`(provinceRepository.findAll(pageable))
            .thenReturn(PageImpl(emptyList(), pageable, 0))

        val result = provinceService.getAllProvinces(emptyList(), pageable)

        assertThat(result.totalElements).isZero()
        assertThat(result.content).isEmpty()

        verify(provinceRepository).findAll(pageable)
    }

    // ------------------------------------------------------------------
    // getBySlug
    // ------------------------------------------------------------------

    @Test
    fun `getBySlug returns the mapped response`() {
        val id = UUID.randomUUID()
        val province =
            province(
                id = id,
                code = "16",
                slug = "algiers",
                translations =
                    setOf(
                        translation(provinceId = id, name = "Alger", language = "fr"),
                        translation(provinceId = id, name = "Algiers", language = "en"),
                        translation(provinceId = id, name = "الجزائر", language = "ar"),
                    ),
            )

        `when`(provinceRepository.findProvinceBySlug("algiers")).thenReturn(province)

        val result = provinceService.getBySlug(emptyList(), "algiers")

        assertThat(result.code).isEqualTo("16")
        assertThat(result.slug).isEqualTo("algiers")
        assertThat(result.names.map { it.name })
            .containsExactlyInAnyOrder("Alger", "Algiers", "الجزائر")
        assertThat(result.names.map { it.language?.code })
            .containsExactlyInAnyOrder("ar", "en", "fr")

        verify(provinceRepository).findProvinceBySlug("algiers")
    }

    @Test
    fun `getBySlug throws ProvinceSlugNotFoundException when no province matches`() {
        `when`(provinceRepository.findProvinceBySlug("unknown")).thenReturn(null)

        assertThatThrownBy { provinceService.getBySlug(emptyList(), "unknown") }
            .isInstanceOf(ProvinceSlugNotFoundException::class.java)

        verify(provinceRepository).findProvinceBySlug("unknown")
    }

    @Test
    fun `getBySlug normalizes the given languages`() {
        val id = UUID.randomUUID()
        val province =
            province(
                id = id,
                code = "16",
                slug = "algiers",
                translations =
                    setOf(
                        translation(provinceId = id, name = "Alger", language = "fr"),
                        translation(provinceId = id, name = "Algiers", language = "en"),
                        translation(provinceId = id, name = "الجزائر", language = "ar"),
                    ),
            )

        `when`(provinceRepository.findProvinceBySlug("algiers")).thenReturn(province)

        val result =
            provinceService.getBySlug(listOf(" FR ", "en,AR", "fr"), "algiers")

        assertThat(result.names.map { it.language?.code })
            .containsExactlyInAnyOrder("fr", "en", "ar")

        verify(provinceRepository).findProvinceBySlug("algiers")
    }

    @Test
    fun `getBySlug falls back to default languages when none are provided`() {
        val id = UUID.randomUUID()
        val province =
            province(
                id = id,
                code = "16",
                slug = "algiers",
                translations =
                    setOf(
                        translation(provinceId = id, name = "Alger", language = "fr"),
                        translation(provinceId = id, name = "Algiers", language = "en"),
                        translation(provinceId = id, name = "الجزائر", language = "ar"),
                    ),
            )

        `when`(provinceRepository.findProvinceBySlug("algiers")).thenReturn(province)

        val result = provinceService.getBySlug(emptyList(), "algiers")

        assertThat(result.names.map { it.language?.code })
            .containsExactlyInAnyOrder("ar", "en", "fr")

        verify(provinceRepository).findProvinceBySlug("algiers")
    }

    // ------------------------------------------------------------------
    // getByCode
    // ------------------------------------------------------------------

    @Test
    fun `getByCode returns the mapped response`() {
        val id = UUID.randomUUID()
        val province =
            province(
                id = id,
                code = "16",
                slug = "algiers",
                translations =
                    setOf(
                        translation(provinceId = id, name = "Alger", language = "fr"),
                        translation(provinceId = id, name = "Algiers", language = "en"),
                        translation(provinceId = id, name = "الجزائر", language = "ar"),
                    ),
            )

        `when`(provinceRepository.findProvinceByCode("16")).thenReturn(province)

        val result = provinceService.getByCode(emptyList(), "16")

        assertThat(result.code).isEqualTo("16")
        assertThat(result.slug).isEqualTo("algiers")
        assertThat(result.names.map { it.language?.code })
            .containsExactlyInAnyOrder("ar", "en", "fr")

        verify(provinceRepository).findProvinceByCode("16")
    }

    @Test
    fun `getByCode throws ProvinceCodeNotFoundException when no province matches`() {
        `when`(provinceRepository.findProvinceByCode("99")).thenReturn(null)

        assertThatThrownBy { provinceService.getByCode(emptyList(), "99") }
            .isInstanceOf(ProvinceCodeNotFoundException::class.java)

        verify(provinceRepository).findProvinceByCode("99")
    }

    @Test
    fun `getByCode normalizes the given languages`() {
        val id = UUID.randomUUID()
        val province =
            province(
                id = id,
                code = "16",
                slug = "algiers",
                translations =
                    setOf(
                        translation(provinceId = id, name = "Alger", language = "fr"),
                        translation(provinceId = id, name = "Algiers", language = "en"),
                        translation(provinceId = id, name = "الجزائر", language = "ar"),
                    ),
            )

        `when`(provinceRepository.findProvinceByCode("16")).thenReturn(province)

        val result =
            provinceService.getByCode(listOf("EN, fr", "en", "!"), "16")

        assertThat(result.names.map { it.language?.code })
            .containsExactlyInAnyOrder("en", "fr")

        verify(provinceRepository).findProvinceByCode("16")
    }

    @Test
    fun `getByCode falls back to default languages when none are provided`() {
        val id = UUID.randomUUID()
        val province =
            province(
                id = id,
                code = "16",
                slug = "algiers",
                translations =
                    setOf(
                        translation(provinceId = id, name = "Alger", language = "fr"),
                        translation(provinceId = id, name = "Algiers", language = "en"),
                        translation(provinceId = id, name = "الجزائر", language = "ar"),
                    ),
            )

        `when`(provinceRepository.findProvinceByCode("16")).thenReturn(province)

        val result = provinceService.getByCode(emptyList(), "16")

        assertThat(result.names.map { it.language?.code })
            .containsExactlyInAnyOrder("ar", "en", "fr")

        verify(provinceRepository).findProvinceByCode("16")
    }

    // ------------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------------

    private fun province(
        id: UUID = UUID.randomUUID(),
        code: String,
        slug: String,
        translations: Set<ProvinceTranslation> = emptySet(),
    ): Province =
        Province().apply {
            this.id = id
            this.code = code
            this.slug = slug
            this.provinceTranslations = translations.toMutableSet()
        }

    private fun translation(
        provinceId: UUID,
        name: String,
        language: String,
    ): ProvinceTranslation {
        val language =
            Language().apply {
                id = UUID.randomUUID()
                code = language
            }
        return ProvinceTranslation().apply {
            id =
                ProvinceTranslationId().apply {
                    this.provinceId = provinceId
                    this.languageId = language.id
                }
            this.language = language
            this.name = name
        }
    }
}
