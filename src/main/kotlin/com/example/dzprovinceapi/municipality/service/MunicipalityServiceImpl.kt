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
package com.example.dzprovinceapi.municipality.service

import com.example.dzprovinceapi.municipality.api.dto.MunicipalityResponse
import com.example.dzprovinceapi.municipality.domain.MunicipalityRepository
import com.example.dzprovinceapi.municipality.mapping.toResponse
import com.example.dzprovinceapi.shared.error.MunicipalityCodeNotFoundException
import com.example.dzprovinceapi.shared.error.MunicipalitySlugNotFoundException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class MunicipalityServiceImpl(
    private val municipalityRepository: MunicipalityRepository,
) : MunicipalityService {
    @Transactional(readOnly = true)
    override fun getAllMunicipalities(
        languages: List<String>,
        pageable: Pageable,
    ): Page<MunicipalityResponse> {
        val municipalities = municipalityRepository.findAll(pageable)
        val response =
            municipalities.map {
                it.toResponse(languages.normalizeOrDefaults())
            }

        return response
    }

    @Transactional(readOnly = true)
    override fun getByCode(
        languages: List<String>,
        code: Short,
    ): MunicipalityResponse {
        val municipality =
            municipalityRepository
                .findMunicipalityByCode(code)
                ?: throw MunicipalityCodeNotFoundException(code)
        val response =
            municipality
                .toResponse(languages.normalizeOrDefaults())

        return response
    }

    @Transactional(readOnly = true)
    override fun getBySlug(
        languages: List<String>,
        slug: String,
    ): MunicipalityResponse {
        val municipality =
            municipalityRepository
                .findMunicipalityBySlug(slug)
                ?: throw MunicipalitySlugNotFoundException(slug)
        val response =
            municipality
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
