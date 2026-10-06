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
import com.example.dzprovinceapi.province.domain.Province
import com.example.dzprovinceapi.province.domain.ProvinceRepository
import com.example.dzprovinceapi.province.mapping.toResponse
import com.example.dzprovinceapi.shared.error.ProvinceCodeNotFoundException
import com.example.dzprovinceapi.shared.error.ProvinceSlugNotFoundException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProvinceServiceImpl(
    private val provinceRepository: ProvinceRepository,
) : ProvinceService {
    @Transactional(readOnly = true)
    override fun getAllProvinces(
        languages: List<String>,
        pageable: Pageable,
    ): Page<ProvinceResponse> {
        val provinces = provinceRepository.findAll(pageable)
        val response =
            provinces.map {
                it.toResponse(languages.normalizeOrDefaults())
            }

        return response
    }

    @Transactional(readOnly = true)
    override fun getBySlug(
        languages: List<String>,
        slug: String,
    ): ProvinceResponse {
        val province =
            provinceRepository
                .findProvinceBySlug(slug)
                ?: throw ProvinceSlugNotFoundException(slug)
        val response =
            province
                .toResponse(languages.normalizeOrDefaults())

        return response
    }

    @Transactional(readOnly = true)
    override fun getByCode(
        languages: List<String>,
        code: Short,
    ): ProvinceResponse {
        val province =
            provinceRepository
                .findProvinceByCode(code)
                ?: throw ProvinceCodeNotFoundException(code)
        val response =
            province
                .toResponse(languages.normalizeOrDefaults())

        return response
    }

    private fun List<String>.normalizeOrDefaults(): List<String> =
        asSequence()
            .flatMap { it.split(',') }
            .map { it.trim().lowercase() }
            .filter { it.length == 2 && it.all(Char::isLetter) }
            .distinct()
            .toList()
            .ifEmpty { DEFAULT_LANGUAGES }

    companion object {
        private val DEFAULT_LANGUAGES = listOf("ar", "en", "fr")
    }
}
