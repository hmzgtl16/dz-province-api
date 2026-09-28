package com.example.dzprovinceapi.province.mapping

import com.example.dzprovinceapi.province.api.dto.ProvinceNameResponse
import com.example.dzprovinceapi.province.api.dto.ProvinceResponse
import com.example.dzprovinceapi.province.domain.medel.Province
import com.example.dzprovinceapi.province.domain.medel.ProvinceTranslation

fun Province.toResponse(lang: String?): ProvinceResponse =
    ProvinceResponse(
        id = 1,
        code = id.toString(),
        slug = slug,
        names =
            translations
                .filter { it.language.code == lang || lang == null }
                .map(ProvinceTranslation::toResponse),
    )

fun ProvinceTranslation.toResponse(): ProvinceNameResponse =
    ProvinceNameResponse(
        name = name,
        language = language.code,
    )
