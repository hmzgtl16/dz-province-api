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
import com.example.dzprovinceapi.helpers.testTranslation
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
                code = "16",
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
                code = "16",
                slug = "algiers",
                translations =
                    setOf(
                        testTranslation(id, "Alger", testLanguage(code = "fr")),
                        testTranslation(id, "Algiers", testLanguage(code = "en")),
                        testTranslation(id, "الجزائر", testLanguage(code = "ar")),
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
                code = "16",
                slug = "algiers",
                translations =
                    setOf(
                        testTranslation(UUID.randomUUID(), "Alger", testLanguage(code = "fr")),
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
                code = "16",
                slug = "algiers",
                translations =
                    setOf(
                        testTranslation(id, "Alger", testLanguage(code = "fr")),
                        testTranslation(id, "Algiers", testLanguage(code = "en")),
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
                code = "16",
                slug = "algiers",
                translations = setOf(testTranslation(id, "Alger", testLanguage(code = "fr"))),
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
                code = "16",
                slug = "algiers",
                translations = setOf(testTranslation(id, "Alger", testLanguage(code = "fr"))),
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
                code = "16",
                slug = "algiers",
                translations =
                    setOf(
                        testTranslation(id, "الجزائر", testLanguage(code = "ar")),
                        testTranslation(id, "Alger", testLanguage(code = "fr")),
                        testTranslation(id, "Algiers", testLanguage(code = "en")),
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

    @Test
    fun `code and slug are copied verbatim`() {
        val id = UUID.randomUUID()
        val province = testProvince(id = id, code = "31", slug = "oran")

        val result = province.toResponse(listOf("fr"))

        assertThat(result.code).isEqualTo("31")
        assertThat(result.slug).isEqualTo("oran")
    }

    @Test
    fun `requested languages list is not mutated`() {
        val languages = mutableListOf("fr", "en")
        val id = UUID.randomUUID()
        val province =
            testProvince(
                id = id,
                code = "16",
                slug = "algiers",
                translations =
                    setOf(
                        testTranslation(id, "Alger", testLanguage(code = "fr")),
                        testTranslation(id, "Algiers", testLanguage(code = "en")),
                    ),
            )

        province.toResponse(languages)

        assertThat(languages).containsExactly("fr", "en")
    }

    @Test
    fun `maps name and nested language`() {
        val translation = testTranslation(UUID.randomUUID(), "Alger", testLanguage(code = "fr"))

        val result = translation.toResponse()

        assertThat(result.name).isEqualTo("Alger")
        assertThat(result.language?.code).isEqualTo("fr")
    }

    @Test
    fun `preserves name with non-latin characters`() {
        val translation = testTranslation(UUID.randomUUID(), "الجزائر", testLanguage(code = "ar"))

        val result = translation.toResponse()

        assertThat(result.name).isEqualTo("الجزائر")
        assertThat(result.language?.code).isEqualTo("ar")
    }

    @Test
    fun `preserves blank name`() {
        val translation = testTranslation(UUID.randomUUID(), "", testLanguage(code = "fr"))

        val result = translation.toResponse()

        assertThat(result.name).isEmpty()
        assertThat(result.language?.code).isEqualTo("fr")
    }
}
