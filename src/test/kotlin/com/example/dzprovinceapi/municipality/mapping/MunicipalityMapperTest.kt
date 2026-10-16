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
package com.example.dzprovinceapi.municipality.mapping

import com.example.dzprovinceapi.helpers.testLanguage
import com.example.dzprovinceapi.helpers.testMunicipality
import com.example.dzprovinceapi.helpers.testMunicipalityTranslation
import com.example.dzprovinceapi.municipality.domain.Municipality
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.UUID

class MunicipalityMapperTest {
    @Test
    fun `null municipality maps to empty response`() {
        val result = (null as Municipality?).toResponse(listOf("fr"))

        assertThat(result.code).isNull()
        assertThat(result.slug).isNull()
        assertThat(result.names).isEmpty()
    }

    @Test
    fun `municipality with no translations maps to empty names`() {
        val municipality =
            testMunicipality(
                code = 1601,
                slug = "alger-centre",
                translations = emptySet(),
            )

        val result = municipality.toResponse(listOf("fr"))

        assertThat(result.code).isEqualTo("1601")
        assertThat(result.slug).isEqualTo("alger-centre")
        assertThat(result.names).isEmpty()
    }

    @Test
    fun `municipality code is formatted to 4 digits with leading zeros`() {
        val municipality =
            testMunicipality(
                code = 101,
                slug = "adrar",
                translations = emptySet(),
            )

        val result = municipality.toResponse(listOf("fr"))

        assertThat(result.code).isEqualTo("0101")
    }

    @Test
    fun `only translations matching requested languages are included`() {
        val id = UUID.randomUUID()
        val municipality =
            testMunicipality(
                id = id,
                code = 1601,
                slug = "alger-centre",
                translations =
                    setOf(
                        testMunicipalityTranslation(id, "Alger Centre", testLanguage(code = "fr")),
                        testMunicipalityTranslation(id, "Algiers Center", testLanguage(code = "en")),
                        testMunicipalityTranslation(id, "الجزائر الوسطى", testLanguage(code = "ar")),
                    ),
            )

        val result = municipality.toResponse(listOf("fr", "ar"))

        assertThat(result.names).hasSize(2)
        assertThat(result.names.map { it.language?.code })
            .containsExactlyInAnyOrder("fr", "ar")
        assertThat(result.names.map { it.name })
            .containsExactlyInAnyOrder("Alger Centre", "الجزائر الوسطى")
    }

    @Test
    fun `languages with no matching translation yields empty names`() {
        val id = UUID.randomUUID()
        val municipality =
            testMunicipality(
                id = id,
                code = 1601,
                slug = "alger-centre",
                translations =
                    setOf(
                        testMunicipalityTranslation(UUID.randomUUID(), "Alger Centre", testLanguage(code = "fr")),
                    ),
            )

        val result = municipality.toResponse(listOf("de", "es"))

        assertThat(result.names).isEmpty()
    }

    @Test
    fun `empty languages list yields empty names`() {
        val id = UUID.randomUUID()
        val municipality =
            testMunicipality(
                id = id,
                code = 1601,
                slug = "alger-centre",
                translations =
                    setOf(
                        testMunicipalityTranslation(id, "Alger Centre", testLanguage(code = "fr")),
                        testMunicipalityTranslation(id, "Algiers Center", testLanguage(code = "en")),
                    ),
            )

        val result = municipality.toResponse(emptyList())

        assertThat(result.names).isEmpty()
    }

    @Test
    fun `language matching is case sensitive`() {
        val id = UUID.randomUUID()
        val municipality =
            testMunicipality(
                id = id,
                code = 1601,
                slug = "alger-centre",
                translations = setOf(testMunicipalityTranslation(id, "Alger Centre", testLanguage(code = "fr"))),
            )

        val result = municipality.toResponse(listOf("FR", "Fr", "fR"))

        assertThat(result.names).isEmpty()
    }

    @Test
    fun `duplicate language codes do not duplicate results`() {
        val id = UUID.randomUUID()
        val municipality =
            testMunicipality(
                id = id,
                code = 1601,
                slug = "alger-centre",
                translations = setOf(testMunicipalityTranslation(id, "Alger Centre", testLanguage(code = "fr"))),
            )

        val result = municipality.toResponse(listOf("fr", "fr", "fr"))

        assertThat(result.names).hasSize(1)
        assertThat(result.names.single().name).isEqualTo("Alger Centre")
    }

    @Test
    fun `all translations returned when all languages requested`() {
        val id = UUID.randomUUID()
        val municipality =
            testMunicipality(
                id = id,
                code = 1601,
                slug = "alger-centre",
                translations =
                    setOf(
                        testMunicipalityTranslation(id, "الجزائر الوسطى", testLanguage(code = "ar")),
                        testMunicipalityTranslation(id, "Alger Centre", testLanguage(code = "fr")),
                        testMunicipalityTranslation(id, "Algiers Center", testLanguage(code = "en")),
                    ),
            )

        val result = municipality.toResponse(listOf("ar", "fr", "en"))

        assertThat(result.names).hasSize(3)
        assertThat(result.names.associate { it.language?.code to it.name })
            .containsExactlyInAnyOrderEntriesOf(
                mapOf(
                    "ar" to "الجزائر الوسطى",
                    "fr" to "Alger Centre",
                    "en" to "Algiers Center",
                ),
            )
    }
}
