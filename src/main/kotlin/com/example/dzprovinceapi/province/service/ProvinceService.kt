package com.example.dzprovinceapi.province.service

import com.example.dzprovinceapi.language.domain.Language
import com.example.dzprovinceapi.province.api.dto.ProvinceResponse
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface ProvinceService {
    fun getAllProvinces(pageable: Pageable): Page<ProvinceResponse>

    fun getAllProvincesByLanguage(
        language: Language,
        pageable: Pageable,
    ): Page<ProvinceResponse>

    fun getById(id: Short): ProvinceResponse
}
