package com.example.dzprovinceapi.province.api.dto

import com.example.dzprovinceapi.language.api.dto.LanguageResponse
import kotlinx.serialization.Serializable

@Serializable
data class ProvinceNameResponse(
    val name: String?,
    val language: LanguageResponse?,
)
