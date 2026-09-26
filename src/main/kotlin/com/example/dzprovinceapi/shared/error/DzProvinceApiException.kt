package com.example.dzprovinceapi.shared.error

sealed class DzProvinceApiException(
    override val message: String,
) : RuntimeException()

class ProvinceNotFoundException(
    code: Short,
) : DzProvinceApiException(message = "No province exists with code $code.")
