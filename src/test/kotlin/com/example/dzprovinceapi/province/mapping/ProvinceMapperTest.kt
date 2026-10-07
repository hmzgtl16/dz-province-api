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
package com.example.dzprovinceapi.province.mapping

import com.example.dzprovinceapi.helpers.testLanguage
import com.example.dzprovinceapi.helpers.testProvince
import com.example.dzprovinceapi.helpers.testProvinceTranslation
import com.example.dzprovinceapi.province.domain.Province
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.UUID
import kotlin.collections.emptySet

class ProvinceMapperTest {
    @Test
    fun `null province maps to empty response`() {
        val result = (null as Province?).toResponse(listOf("fr"))

        assertThat(result.code).isNull()
        assertThat(result.slug).isNull()
        assertThat(result.names).isEmpty()
    }

    @Test
    fun `province with no translations maps to empty names`() {
        val province =
            testProvince(
                code = 16,
                slug = "algiers",
                translations = emptySet(),
            )

        val result = province.toResponse(listOf("fr"))

        assertThat(result.code).isEqualTo("16")
        assertThat(result.slug).isEqualTo("algiers")
        assertThat(result.names).isEmpty()
    }

    @Test
    fun `only translations matching requested languages are included`() {
        val id = UUID.randomUUID()
        val province =
            testProvince(
                id = id,
                code = 16,
                slug = "algiers",
                translations =
                    setOf(
                        testProvinceTranslation(id, "Alger", testLanguage(code = "fr")),
                        testProvinceTranslation(id, "Algiers", testLanguage(code = "en")),
                        testProvinceTranslation(id, "الجزائر", testLanguage(code = "ar")),
                    ),
            )

        val result = province.toResponse(listOf("fr", "ar"))

        assertThat(result.names).hasSize(2)
        assertThat(result.names.map { it.language?.code })
            .containsExactlyInAnyOrder("fr", "ar")
        assertThat(result.names.map { it.name })
            .containsExactlyInAnyOrder("Alger", "الجزائر")
    }

    @Test
    fun `languages with no matching translation yields empty names`() {
        val id = UUID.randomUUID()
        val province =
            testProvince(
                id = id,
                code = 16,
                slug = "algiers",
                translations =
                    setOf(
                        testProvinceTranslation(UUID.randomUUID(), "Alger", testLanguage(code = "fr")),
                    ),
            )

        val result = province.toResponse(listOf("de", "es"))

        assertThat(result.names).isEmpty()
    }

    @Test
    fun `empty languages list yields empty names`() {
        val id = UUID.randomUUID()
        val province =
            testProvince(
                id = id,
                code = 16,
                slug = "algiers",
                translations =
                    setOf(
                        testProvinceTranslation(id, "Alger", testLanguage(code = "fr")),
                        testProvinceTranslation(id, "Algiers", testLanguage(code = "en")),
                    ),
            )

        val result = province.toResponse(emptyList())

        assertThat(result.names).isEmpty()
    }

    @Test
    fun `language matching is case sensitive`() {
        val id = UUID.randomUUID()
        val province =
            testProvince(
                id = id,
                code = 16,
                slug = "algiers",
                translations = setOf(testProvinceTranslation(id, "Alger", testLanguage(code = "fr"))),
            )

        val result = province.toResponse(listOf("FR", "Fr", "fR"))

        assertThat(result.names).isEmpty()
    }

    @Test
    fun `duplicate language codes do not duplicate results`() {
        val id = UUID.randomUUID()
        val province =
            testProvince(
                id = id,
                code = 16,
                slug = "algiers",
                translations = setOf(testProvinceTranslation(id, "Alger", testLanguage(code = "fr"))),
            )

        val result = province.toResponse(listOf("fr", "fr", "fr"))

        assertThat(result.names).hasSize(1)
        assertThat(result.names.single().name).isEqualTo("Alger")
    }

    @Test
    fun `all translations returned when all languages requested`() {
        val id = UUID.randomUUID()
        val province =
            testProvince(
                id = id,
                code = 16,
                slug = "algiers",
                translations =
                    setOf(
                        testProvinceTranslation(id, "الجزائر", testLanguage(code = "ar")),
                        testProvinceTranslation(id, "Alger", testLanguage(code = "fr")),
                        testProvinceTranslation(id, "Algiers", testLanguage(code = "en")),
                    ),
            )

        val result = province.toResponse(listOf("ar", "fr", "en"))

        assertThat(result.names).hasSize(3)
        assertThat(result.names.associate { it.language?.code to it.name })
            .containsExactlyInAnyOrderEntriesOf(
                mapOf(
                    "ar" to "الجزائر",
                    "fr" to "Alger",
                    "en" to "Algiers",
                ),
            )
    }
}
