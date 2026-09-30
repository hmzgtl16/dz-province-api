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

import com.example.dzprovinceapi.language.domain.Language
import com.example.dzprovinceapi.province.domain.Province
import com.example.dzprovinceapi.province.domain.ProvinceTranslation
import com.example.dzprovinceapi.province.domain.ProvinceTranslationId
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.UUID

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
        val province = province(translations = emptySet())

        val result = province.toResponse(listOf("fr"))

        assertThat(result.code).isEqualTo("16")
        assertThat(result.slug).isEqualTo("algiers")
        assertThat(result.names).isEmpty()
    }

    @Test
    fun `only translations matching requested languages are included`() {
        val province =
            province(
                translations =
                    setOf(
                        translation("Alger", "fr"),
                        translation("Algiers", "en"),
                        translation("الجزائر", "ar"),
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
        val province =
            province(
                translations = setOf(translation("Alger", "fr")),
            )

        val result = province.toResponse(listOf("de", "es"))

        assertThat(result.names).isEmpty()
    }

    @Test
    fun `empty languages list yields empty names`() {
        val province =
            province(
                translations =
                    setOf(
                        translation("Alger", "fr"),
                        translation("Algiers", "en"),
                    ),
            )

        val result = province.toResponse(emptyList())

        assertThat(result.names).isEmpty()
    }

    @Test
    fun `language matching is case sensitive`() {
        val province =
            province(
                translations = setOf(translation("Alger", "fr")),
            )

        val result = province.toResponse(listOf("FR", "Fr", "fR"))

        assertThat(result.names).isEmpty()
    }

    @Test
    fun `duplicate language codes do not duplicate results`() {
        val province =
            province(
                translations = setOf(translation("Alger", "fr")),
            )

        val result = province.toResponse(listOf("fr", "fr", "fr"))

        assertThat(result.names).hasSize(1)
        assertThat(result.names.single().name).isEqualTo("Alger")
    }

    @Test
    fun `translation with null language is excluded`() {
        val province =
            province(
                translations =
                    setOf(
                        translation("Alger", language = null),
                        translation("Algiers", "en"),
                    ),
            )

        val result = province.toResponse(listOf("fr", "en"))

        assertThat(result.names).hasSize(1)
        assertThat(result.names.single().name).isEqualTo("Algiers")
        assertThat(
            result.names
                .single()
                .language
                ?.code,
        ).isEqualTo("en")
    }

    @Test
    fun `all translations returned when all languages requested`() {
        val province =
            province(
                translations =
                    setOf(
                        translation("الجزائر", "ar"),
                        translation("Alger", "fr"),
                        translation("Algiers", "en"),
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
        val province = province(code = "31", slug = "oran")

        val result = province.toResponse(listOf("fr"))

        assertThat(result.code).isEqualTo("31")
        assertThat(result.slug).isEqualTo("oran")
    }

    @Test
    fun `requested languages list is not mutated`() {
        val languages = mutableListOf("fr", "en")
        val province =
            province(
                translations =
                    setOf(
                        translation("Alger", "fr"),
                        translation("Algiers", "en"),
                    ),
            )

        province.toResponse(languages)

        assertThat(languages).containsExactly("fr", "en")
    }

    @Test
    fun `maps name and nested language`() {
        val translation = translation("Alger", "fr")

        val result = translation.toResponse()

        assertThat(result.name).isEqualTo("Alger")
        assertThat(result.language?.code).isEqualTo("fr")
    }

    @Test
    fun `preserves name with non-latin characters`() {
        val translation = translation("الجزائر", "ar")

        val result = translation.toResponse()

        assertThat(result.name).isEqualTo("الجزائر")
        assertThat(result.language?.code).isEqualTo("ar")
    }

    @Test
    fun `preserves blank name`() {
        val translation = translation("", "fr")

        val result = translation.toResponse()

        assertThat(result.name).isEmpty()
        assertThat(result.language?.code).isEqualTo("fr")
    }

    private fun province(
        code: String = "16",
        slug: String = "algiers",
        translations: Set<ProvinceTranslation> = emptySet(),
    ): Province =
        Province().apply {
            id = UUID.randomUUID()
            this.code = code
            this.slug = slug
            this.provinceTranslations = translations.toMutableSet()
        }

    private fun translation(
        name: String,
        languageCode: String? = "fr",
        language: Language? = languageCode?.let(::language),
    ): ProvinceTranslation =
        ProvinceTranslation().apply {
            id =
                ProvinceTranslationId().apply {
                    provinceId = UUID.randomUUID()
                    languageId = language?.id ?: UUID.randomUUID()
                }
            this.language = language
            this.name = name
        }

    private fun language(code: String): Language =
        Language().apply {
            id = UUID.randomUUID()
            this.code = code
            this.name = code.uppercase()
        }
}
