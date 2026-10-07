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
package com.example.dzprovinceapi.district.mapping

import com.example.dzprovinceapi.district.domain.District
import com.example.dzprovinceapi.helpers.testDistrict
import com.example.dzprovinceapi.helpers.testDistrictTranslation
import com.example.dzprovinceapi.helpers.testLanguage
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.UUID

class DistrictMapperTest {
    @Test
    fun `null District returns empty response`() {
        val result = (null as District?).toResponse(listOf("fr"))

        assertThat(result.slug).isNull()
        assertThat(result.names).isEmpty()
    }

    @Test
    fun `district with no translations returns empty names`() {
        val district =
            testDistrict(
                slug = "cheraga",
                translations = emptySet(),
            )

        val result = district.toResponse(listOf("fr"))

        assertThat(result.slug).isEqualTo("cheraga")
        assertThat(result.names).isEmpty()
    }

    @Test
    fun `only translations matching requested languages are included`() {
        val id = UUID.randomUUID()
        val district =
            testDistrict(
                id = id,
                slug = "cheraga",
                translations =
                    setOf(
                        testDistrictTranslation(id, "Cheraga", testLanguage(code = "fr")),
                        testDistrictTranslation(id, "Cheraga", testLanguage(code = "en")),
                        testDistrictTranslation(id, "الشراقة", testLanguage(code = "ar")),
                    ),
            )

        val result = district.toResponse(listOf("fr", "ar"))

        assertThat(result.names).hasSize(2)
        assertThat(result.names.map { it.language?.code })
            .containsExactlyInAnyOrder("fr", "ar")
        assertThat(result.names.map { it.name })
            .containsExactlyInAnyOrder("Cheraga", "الشراقة")
    }

    @Test
    fun `languages with no matching translation yields empty names`() {
        val id = UUID.randomUUID()
        val district =
            testDistrict(
                id = id,
                slug = "cheraga",
                translations =
                    setOf(
                        testDistrictTranslation(id, "Cheraga", testLanguage(code = "fr")),
                        testDistrictTranslation(id, "الشراقة", testLanguage(code = "ar")),
                    ),
            )

        val result = district.toResponse(listOf("en", "es"))

        assertThat(result.names).isEmpty()
    }

    @Test
    fun `empty languages list yields empty names`() {
        val id = UUID.randomUUID()
        val district =
            testDistrict(
                id = id,
                slug = "cheraga",
                translations =
                    setOf(
                        testDistrictTranslation(id, "Cheraga", testLanguage(code = "fr")),
                        testDistrictTranslation(id, "Cheraga", testLanguage(code = "en")),
                        testDistrictTranslation(id, "الشراقة", testLanguage(code = "ar")),
                    ),
            )

        val result = district.toResponse(emptyList())

        assertThat(result.names).isEmpty()
    }

    @Test
    fun `language matching is case sensitive`() {
        val id = UUID.randomUUID()
        val district =
            testDistrict(
                id = id,
                slug = "cheraga",
                translations =
                    setOf(
                        testDistrictTranslation(id, "Cheraga", testLanguage(code = "en")),
                        testDistrictTranslation(id, "Cheraga", testLanguage(code = "fr")),
                        testDistrictTranslation(id, "الشراقة", testLanguage(code = "ar")),
                    ),
            )

        val result = district.toResponse(listOf("FR", "Fr", "fR"))

        assertThat(result.names).isEmpty()
    }

    @Test
    fun `duplicate language codes do not duplicate results`() {
        val id = UUID.randomUUID()
        val district =
            testDistrict(
                id = id,
                slug = "cheraga",
                translations =
                    setOf(
                        testDistrictTranslation(id, "Cheraga", testLanguage(code = "fr")),
                        testDistrictTranslation(id, "Cheraga", testLanguage(code = "en")),
                        testDistrictTranslation(id, "الشراقة", testLanguage(code = "ar")),
                    ),
            )

        val result = district.toResponse(listOf("fr", "fr", "fr"))

        assertThat(result.names).hasSize(1)
        assertThat(result.names.single().name).isEqualTo("Cheraga")
    }

    @Test
    fun `all translations returned when all languages requested`() {
        val id = UUID.randomUUID()
        val district =
            testDistrict(
                id = id,
                slug = "cheraga",
                translations =
                    setOf(
                        testDistrictTranslation(id, "Cheraga", testLanguage(code = "fr")),
                        testDistrictTranslation(id, "Cheraga", testLanguage(code = "en")),
                        testDistrictTranslation(id, "الشراقة", testLanguage(code = "ar")),
                    ),
            )

        val result = district.toResponse(listOf("ar", "fr", "en"))

        assertThat(result.names).hasSize(3)
        assertThat(result.names.associate { it.language?.code to it.name })
            .containsExactlyInAnyOrderEntriesOf(
                mapOf(
                    "ar" to "الشراقة",
                    "fr" to "Cheraga",
                    "en" to "Cheraga",
                ),
            )
    }
}
