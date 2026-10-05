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
package com.example.dzprovinceapi.province.mapping

import com.example.dzprovinceapi.language.mapping.toResponse
import com.example.dzprovinceapi.province.api.dto.ProvinceNameResponse
import com.example.dzprovinceapi.province.api.dto.ProvinceResponse
import com.example.dzprovinceapi.province.domain.Province
import com.example.dzprovinceapi.province.domain.ProvinceTranslation

fun Province?.toResponse(languages: List<String>): ProvinceResponse =
    ProvinceResponse(
        code = this?.code,
        slug = this?.slug,
        names =
            this
                ?.provinceTranslations
                ?.filter { languages.contains(it.language?.code) }
                ?.map { it.toResponse() } ?: emptyList(),
    )

fun ProvinceTranslation.toResponse(): ProvinceNameResponse =
    ProvinceNameResponse(
        name = name,
        language = language.toResponse(),
    )
