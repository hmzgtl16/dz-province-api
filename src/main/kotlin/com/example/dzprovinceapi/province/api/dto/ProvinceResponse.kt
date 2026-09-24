package com.example.dzprovinceapi.province.api.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProvinceResponse(
    val id: Long,
    val code: String,
    val slug: String,
    val name: String,
    @SerialName("resolved_language")
    val resolvedLanguage: String,
    @SerialName("municipality_count")
    val municipalityCount: Int,
)
