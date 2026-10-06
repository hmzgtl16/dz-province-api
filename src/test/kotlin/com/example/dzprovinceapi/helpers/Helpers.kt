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
package com.example.dzprovinceapi.helpers

import com.example.dzprovinceapi.language.domain.Language
import com.example.dzprovinceapi.province.domain.Province
import com.example.dzprovinceapi.province.domain.ProvinceTranslation
import com.example.dzprovinceapi.province.domain.ProvinceTranslationId
import java.util.UUID

fun testProvince(
    id: UUID = UUID.randomUUID(),
    code: String,
    slug: String,
    translations: Set<ProvinceTranslation> = emptySet(),
): Province =
    Province().apply {
        this.id = id
        this.code = code.toShort()
        this.slug = slug
        this.provinceTranslations = translations.toMutableSet()
    }

fun testTranslation(
    provinceId: UUID,
    name: String,
    language: Language,
): ProvinceTranslation =
    ProvinceTranslation().apply {
        id =
            ProvinceTranslationId().apply {
                this.provinceId = provinceId
                this.languageId = language.id
            }
        this.language = language
        this.name = name
    }

fun testLanguage(
    id: UUID = UUID.randomUUID(),
    code: String,
    name: String? = null,
): Language =
    Language().apply {
        this.id = id
        this.code = code
        this.name =
            when (code) {
                "ar" -> "العربية"
                "fr" -> "Français"
                "en" -> "English"
                else -> name ?: "Unknown"
            }
    }
