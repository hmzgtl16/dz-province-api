package com.example.dzprovinceapi.province.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class LanguageResponse(
    val code: String,
    val name: String,
)
