package com.example.dzprovinceapi.province.mapping

import com.example.dzprovinceapi.language.mapping.toResponse
import com.example.dzprovinceapi.province.api.dto.ProvinceNameResponse
import com.example.dzprovinceapi.province.api.dto.ProvinceResponse
import com.example.dzprovinceapi.province.domain.Province
import com.example.dzprovinceapi.province.domain.ProvinceTranslation

fun Province?.toResponse(): ProvinceResponse =
    ProvinceResponse(
        code = this?.code,
        slug = this?.slug,
        names = this?.provinceTranslations?.map(ProvinceTranslation::toResponse) ?: emptyList(),
    )

fun ProvinceTranslation.toResponse(): ProvinceNameResponse =
    ProvinceNameResponse(
        name = name,
        language = language.toResponse(),
    )
