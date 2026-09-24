package com.example.dzprovinceapi.province.mapping

import com.example.dzprovinceapi.language.domain.Language
import com.example.dzprovinceapi.province.api.dto.LanguageResponse
import com.example.dzprovinceapi.province.api.dto.ProvinceNameResponse
import com.example.dzprovinceapi.province.api.dto.ProvinceResponse
import com.example.dzprovinceapi.province.domain.medel.Province
import com.example.dzprovinceapi.province.domain.medel.ProvinceTranslation

fun Province.toResponse(): ProvinceResponse =
    ProvinceResponse(
        id = id,
        code = id.toString(),
        slug = slug,
        names = translations.map(ProvinceTranslation::toResponse),
    )

fun ProvinceTranslation.toResponse(): ProvinceNameResponse =
    ProvinceNameResponse(
        name = name,
        language = language.toResponse(),
    )

fun Language.toResponse(): LanguageResponse =
    LanguageResponse(
        code = code,
        name = name,
    )
