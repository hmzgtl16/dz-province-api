package com.example.dzprovinceapi.province.api.dto

import kotlinx.serialization.Serializable

@Serializable
data class ProvinceNameResponse(
    val name: String,
    val language: String,
)
