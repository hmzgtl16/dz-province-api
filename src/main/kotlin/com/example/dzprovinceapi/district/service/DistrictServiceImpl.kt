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
package com.example.dzprovinceapi.district.service

import com.example.dzprovinceapi.district.api.dto.DistrictResponse
import com.example.dzprovinceapi.district.domain.DistrictRepository
import com.example.dzprovinceapi.district.mapping.toResponse
import com.example.dzprovinceapi.shared.error.DistrictSlugNotFoundException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DistrictServiceImpl(
    private val districtRepository: DistrictRepository,
) : DistrictService {
    @Transactional(readOnly = true)
    override fun getAllDistricts(
        languages: List<String>,
        pageable: Pageable,
    ): Page<DistrictResponse> {
        val districts = districtRepository.findAll(pageable)
        val response =
            districts.map {
                it.toResponse(languages.normalizeOrDefaults())
            }

        return response
    }

    override fun getBySlug(
        languages: List<String>,
        slug: String,
    ): DistrictResponse {
        val district =
            districtRepository.findDistrictBySlug(slug)
                ?: throw DistrictSlugNotFoundException(slug)
        val response =
            district
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
