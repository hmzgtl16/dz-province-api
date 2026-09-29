package com.example.dzprovinceapi.language.mapping

import com.example.dzprovinceapi.language.api.dto.LanguageResponse
import com.example.dzprovinceapi.language.domain.Language

fun Language?.toResponse(): LanguageResponse =
    LanguageResponse(
        code = this?.code,
        name = this?.name,
    )
