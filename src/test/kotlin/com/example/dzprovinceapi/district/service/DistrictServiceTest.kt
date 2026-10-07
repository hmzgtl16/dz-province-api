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
package com.example.dzprovinceapi.district.service

import com.example.dzprovinceapi.district.domain.DistrictRepository
import com.example.dzprovinceapi.helpers.testDistrict
import com.example.dzprovinceapi.helpers.testDistrictTranslation
import com.example.dzprovinceapi.helpers.testLanguage
import org.assertj.core.api.Assertions.assertThat
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
class DistrictServiceTest {
    @Mock
    private lateinit var districtRepository: DistrictRepository

    @InjectMocks
    private lateinit var districtService: DistrictServiceImpl

    // ------------------------------------------------------------------
    // getAllDistricts
    // ------------------------------------------------------------------

    @Test
    fun `getAllDistricts returns a page of mapped responses`() {
        val district =
            testDistrict(slug = "bab-el-oued", translations = emptySet())

        val pageable = PageRequest.of(0, 10)
        val districtPage = PageImpl(listOf(district), pageable, 1)
        `when`(districtRepository.findAll(pageable)).thenReturn(districtPage)

        val result = districtService.getAllDistricts(emptyList(), pageable)

        assertThat(result.totalElements).isEqualTo(1)
        assertThat(result.content).isNotEmpty
        assertThat(result.content[0].slug).isEqualTo(district.slug)
        assertThat(result.content[0].names).isEmpty()

        verify(districtRepository).findAll(pageable)
    }

    @Test
    fun `getAllDistricts falls back to default languages when none are provided`() {
        val id = UUID.randomUUID()
        val district =
            testDistrict(
                id = id,
                slug = "bab-el-oued",
                translations =
                    setOf(
                        testDistrictTranslation(
                            districtId = id,
                            name = "Bab El Oued",
                            language = testLanguage(code = "fr"),
                        ),
                        testDistrictTranslation(
                            districtId = id,
                            name = "Bab El Oued",
                            language = testLanguage(code = "en"),
                        ),
                        testDistrictTranslation(
                            districtId = id,
                            name = "باب الوادي",
                            language = testLanguage(code = "ar"),
                        ),
                    ),
            )

        val pageable = PageRequest.of(0, 10)
        `when`(districtRepository.findAll(pageable))
            .thenReturn(PageImpl(listOf(district), pageable, 1))

        val result = districtService.getAllDistricts(emptyList(), pageable)

        assertThat(result.totalElements).isEqualTo(1)
        assertThat(result.content).isNotEmpty
        assertThat(result.content[0].slug).isEqualTo(district.slug)
        assertThat(result.content[0].names).hasSize(district.districtTranslations.size)
        assertThat(result.content[0].names.map { it.language?.code })
            .containsExactlyInAnyOrder("ar", "en", "fr")

        verify(districtRepository).findAll(pageable)
    }

    @Test
    fun `getAllDistricts filters names to the requested languages`() {
        val id = UUID.randomUUID()
        val district =
            testDistrict(
                id = id,
                slug = "bab-el-oued",
                translations =
                    setOf(
                        testDistrictTranslation(
                            districtId = id,
                            name = "Bab El Oued",
                            language = testLanguage(code = "fr"),
                        ),
                        testDistrictTranslation(
                            districtId = id,
                            name = "Bab El Oued",
                            language = testLanguage(code = "en"),
                        ),
                        testDistrictTranslation(
                            districtId = id,
                            name = "باب الوادي",
                            language = testLanguage(code = "ar"),
                        ),
                    ),
            )

        val pageable = PageRequest.of(0, 10)
        `when`(districtRepository.findAll(pageable))
            .thenReturn(PageImpl(listOf(district), pageable, 1))

        val result = districtService.getAllDistricts(listOf("en"), pageable)

        assertThat(result.content[0].names).hasSize(1)
        assertThat(
            result.content[0]
                .names
                .single()
                .language
                ?.code,
        ).isEqualTo("en")

        verify(districtRepository).findAll(pageable)
    }

    @Test
    fun `getAllDistricts normalizes languages by splitting on comma, trimming, lowercasing and de-duplicating`() {
        val id = UUID.randomUUID()
        val district =
            testDistrict(
                id = id,
                slug = "bab-el-oued",
                translations =
                    setOf(
                        testDistrictTranslation(
                            districtId = id,
                            name = "Bab El Oued",
                            language = testLanguage(code = "fr"),
                        ),
                        testDistrictTranslation(
                            districtId = id,
                            name = "Bab El Oued",
                            language = testLanguage(code = "en"),
                        ),
                        testDistrictTranslation(
                            districtId = id,
                            name = "باب الوادي",
                            language = testLanguage(code = "ar"),
                        ),
                    ),
            )

        val pageable = PageRequest.of(0, 10)
        `when`(districtRepository.findAll(pageable))
            .thenReturn(PageImpl(listOf(district), pageable, 1))

        // " EN ", "fr,ar", "en" -> ["en", "fr", "ar"]; "12" and "e" are invalid and dropped.
        val result =
            districtService.getAllDistricts(
                listOf(" EN ", "fr,ar", "en", "12", "e"),
                pageable,
            )

        assertThat(result.content[0].names.map { it.language?.code })
            .containsExactlyInAnyOrder("en", "fr", "ar")

        verify(districtRepository).findAll(pageable)
    }

    @Test
    fun `getAllDistricts falls back to default languages when every entry is invalid`() {
        val id = UUID.randomUUID()
        val district =
            testDistrict(
                id = id,
                slug = "bab-el-oued",
                translations =
                    setOf(
                        testDistrictTranslation(
                            districtId = id,
                            name = "Bab El Oued",
                            language = testLanguage(code = "fr"),
                        ),
                        testDistrictTranslation(
                            districtId = id,
                            name = "Bab El Oued",
                            language = testLanguage(code = "en"),
                        ),
                        testDistrictTranslation(
                            districtId = id,
                            name = "باب الوادي",
                            language = testLanguage(code = "ar"),
                        ),
                    ),
            )

        val pageable = PageRequest.of(0, 10)
        `when`(districtRepository.findAll(pageable))
            .thenReturn(PageImpl(listOf(district), pageable, 1))

        val result =
            districtService.getAllDistricts(listOf("1", "!!", "", "e"), pageable)

        assertThat(result.content[0].names.map { it.language?.code })
            .containsExactlyInAnyOrder("ar", "en", "fr")

        verify(districtRepository).findAll(pageable)
    }

    @Test
    fun `getAllDistricts returns an empty page when no districts exist`() {
        val pageable = PageRequest.of(0, 10)
        `when`(districtRepository.findAll(pageable))
            .thenReturn(PageImpl(emptyList(), pageable, 0))

        val result = districtService.getAllDistricts(emptyList(), pageable)

        assertThat(result.totalElements).isZero()
        assertThat(result.content).isEmpty()

        verify(districtRepository).findAll(pageable)
    }

    // ------------------------------------------------------------------
    // getBySlug
    // ------------------------------------------------------------------

    @Test
    fun `getBySlug returns the mapped response`() {
        val id = UUID.randomUUID()
        val district =
            testDistrict(
                id = id,
                slug = "bab-el-oued",
                translations =
                    setOf(
                        testDistrictTranslation(
                            districtId = id,
                            name = "Bab El Oued",
                            language = testLanguage(code = "fr"),
                        ),
                        testDistrictTranslation(
                            districtId = id,
                            name = "Bab El Oued",
                            language = testLanguage(code = "en"),
                        ),
                        testDistrictTranslation(
                            districtId = id,
                            name = "باب الوادي",
                            language = testLanguage(code = "ar"),
                        ),
                    ),
            )

        `when`(districtRepository.findDistrictBySlug("bab-el-oued")).thenReturn(district)

        val result = districtService.getBySlug(emptyList(), "bab-el-oued")

        assertThat(result.slug).isEqualTo("bab-el-oued")
        assertThat(result.names.map { it.language?.code })
            .containsExactlyInAnyOrder("ar", "en", "fr")

        verify(districtRepository).findDistrictBySlug("bab-el-oued")
    }

    @Test
    fun `getBySlug returns an empty response when no district matches`() {
        `when`(districtRepository.findDistrictBySlug("unknown")).thenReturn(null)

        val result = districtService.getBySlug(emptyList(), "unknown")

        assertThat(result.slug).isNull()
        assertThat(result.names).isEmpty()

        verify(districtRepository).findDistrictBySlug("unknown")
    }

    @Test
    fun `getBySlug normalizes the given languages`() {
        val id = UUID.randomUUID()
        val district =
            testDistrict(
                id = id,
                slug = "bab-el-oued",
                translations =
                    setOf(
                        testDistrictTranslation(
                            districtId = id,
                            name = "Bab El Oued",
                            language = testLanguage(code = "fr"),
                        ),
                        testDistrictTranslation(
                            districtId = id,
                            name = "Bab El Oued",
                            language = testLanguage(code = "en"),
                        ),
                        testDistrictTranslation(
                            districtId = id,
                            name = "باب الوادي",
                            language = testLanguage(code = "ar"),
                        ),
                    ),
            )

        `when`(districtRepository.findDistrictBySlug("bab-el-oued")).thenReturn(district)

        val result =
            districtService.getBySlug(listOf(" FR ", "en,AR", "fr"), "bab-el-oued")

        assertThat(result.names.map { it.language?.code })
            .containsExactlyInAnyOrder("fr", "en", "ar")

        verify(districtRepository).findDistrictBySlug("bab-el-oued")
    }

    @Test
    fun `getBySlug falls back to default languages when none are provided`() {
        val id = UUID.randomUUID()
        val district =
            testDistrict(
                id = id,
                slug = "bab-el-oued",
                translations =
                    setOf(
                        testDistrictTranslation(
                            districtId = id,
                            name = "Bab El Oued",
                            language = testLanguage(code = "fr"),
                        ),
                        testDistrictTranslation(
                            districtId = id,
                            name = "Bab El Oued",
                            language = testLanguage(code = "en"),
                        ),
                        testDistrictTranslation(
                            districtId = id,
                            name = "باب الوادي",
                            language = testLanguage(code = "ar"),
                        ),
                    ),
            )

        `when`(districtRepository.findDistrictBySlug("bab-el-oued")).thenReturn(district)

        val result = districtService.getBySlug(emptyList(), "bab-el-oued")

        assertThat(result.names.map { it.language?.code })
            .containsExactlyInAnyOrder("ar", "en", "fr")

        verify(districtRepository).findDistrictBySlug("bab-el-oued")
    }

    @Test
    fun `getBySlug falls back to default languages when every entry is invalid`() {
        val id = UUID.randomUUID()
        val district =
            testDistrict(
                id = id,
                slug = "bab-el-oued",
                translations =
                    setOf(
                        testDistrictTranslation(
                            districtId = id,
                            name = "Bab El Oued",
                            language = testLanguage(code = "fr"),
                        ),
                        testDistrictTranslation(
                            districtId = id,
                            name = "Bab El Oued",
                            language = testLanguage(code = "en"),
                        ),
                        testDistrictTranslation(
                            districtId = id,
                            name = "باب الوادي",
                            language = testLanguage(code = "ar"),
                        ),
                    ),
            )

        `when`(districtRepository.findDistrictBySlug("bab-el-oued")).thenReturn(district)

        val result =
            districtService.getBySlug(listOf("1", "!!", "", "e"), "bab-el-oued")

        assertThat(result.names.map { it.language?.code })
            .containsExactlyInAnyOrder("ar", "en", "fr")

        verify(districtRepository).findDistrictBySlug("bab-el-oued")
    }
}
