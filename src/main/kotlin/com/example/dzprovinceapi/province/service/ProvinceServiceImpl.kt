/*
 * Copyright 2026 Hamza Gattal
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.dzprovinceapi.province.service

import com.example.dzprovinceapi.province.api.dto.ProvinceResponse
import com.example.dzprovinceapi.province.domain.medel.Province
import com.example.dzprovinceapi.province.domain.repository.ProvinceRepository
import com.example.dzprovinceapi.province.mapping.toResponse
import com.example.dzprovinceapi.shared.error.ProvinceNotFoundException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class ProvinceServiceImpl(
    private val provinceRepository: ProvinceRepository,
) : ProvinceService {
    override fun getAllProvinces(
        lang: String?,
        pageable: Pageable,
    ): Page<ProvinceResponse> =
        provinceRepository
            .findAll(pageable)
            .map { it.toResponse(lang) }

    override fun getBySlug(
        lang: String?,
        slug: String,
    ): ProvinceResponse =
        provinceRepository.findProvinceBySlug(slug)?.toResponse(lang)
            ?: throw ProvinceNotFoundException(1)
}
