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
package com.example.dzprovinceapi.district.api

import com.example.dzprovinceapi.district.api.dto.DistrictResponse
import com.example.dzprovinceapi.district.service.DistrictService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/{version}/districts", version = "1.0")
class DistrictController(
    private val districtService: DistrictService,
) {
    @GetMapping
    fun getDistricts(
        @RequestParam(name = "lang", defaultValue = "") languages: List<String>,
        @PageableDefault pageable: Pageable,
    ): ResponseEntity<Page<DistrictResponse>> {
        val response = districtService.getAllDistricts(languages, pageable)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/slug/{slug}")
    fun getDistrictBySlug(
        @RequestParam(name = "lang", defaultValue = "") languages: List<String>,
        @PathVariable slug: String,
    ): ResponseEntity<DistrictResponse> {
        val response = districtService.getBySlug(languages, slug)
        return ResponseEntity.ok(response)
    }
}
