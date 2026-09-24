package com.example.dzprovinceapi.province.service

import com.example.dzprovinceapi.province.api.dto.ProvinceResponse
import com.example.dzprovinceapi.province.domain.medel.Province
import com.example.dzprovinceapi.province.domain.repository.ProvinceRepository
import com.example.dzprovinceapi.province.mapping.toResponse
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class ProvinceServiceImpl(
    private val provinceRepository: ProvinceRepository,
) : ProvinceService {
    override fun list(pageable: Pageable): Page<ProvinceResponse> =
        provinceRepository.findAll(pageable).map(Province::toResponse)

    override fun getById(id: Short): ProvinceResponse =
        provinceRepository.findProvinceById(id)?.toResponse()
            ?: throw RuntimeException("Province not found")
}
