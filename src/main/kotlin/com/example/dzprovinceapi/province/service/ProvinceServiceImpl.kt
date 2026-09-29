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
import com.example.dzprovinceapi.province.domain.repository.ProvinceTranslationRepository
import com.example.dzprovinceapi.province.mapping.toResponse
import com.example.dzprovinceapi.shared.error.ProvinceNotFoundException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class ProvinceServiceImpl(
    private val provinceRepository: ProvinceRepository,
    private val provinceTranslationRepository: ProvinceTranslationRepository,
) : ProvinceService {
    @Transactional
    override fun getAllProvinces(
        lang: List<String>,
        pageable: Pageable,
    ): Page<ProvinceResponse> =
        provinceRepository
            .findProvincesByLanguages(lang, pageable)
            .map(Province::toResponse)

    override fun getBySlug(
        lang: String?,
        slug: String,
    ): ProvinceResponse =
        provinceRepository.findProvinceBySlug(slug)?.toResponse()
            ?: throw ProvinceNotFoundException(1)
}
