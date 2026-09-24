package com.example.dzprovinceapi.province.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class ProvinceResponse(
    val id: Short,
    val code: String,
    val slug: String,
    val names: List<ProvinceNameResponse>,
)
