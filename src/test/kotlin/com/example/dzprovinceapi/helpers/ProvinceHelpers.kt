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
        this.code = code
        this.slug = slug
        this.provinceTranslations = translations.toMutableSet()
    }

fun testTranslation(
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
