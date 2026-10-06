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
package com.example.dzprovinceapi.province.api

import com.example.dzprovinceapi.province.api.dto.ProvinceResponse
import com.example.dzprovinceapi.province.service.ProvinceService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/{version}/provinces", version = "1.0")
class ProvinceController(
    private val provinceService: ProvinceService,
) {
    @GetMapping
    fun getProvinces(
        @RequestParam(name = "lang", required = false) languages: List<String> = emptyList(),
        @PageableDefault pageable: Pageable,
    ): ResponseEntity<Page<ProvinceResponse>> {
        val response = provinceService.getAllProvinces(languages, pageable)
        return ResponseEntity.status(HttpStatus.OK).body(response)
    }

    @GetMapping("/slug/{slug}")
    fun getProvinceBySlug(
        @RequestParam(name = "lang", defaultValue = "") languages: List<String>,
        @PathVariable slug: String,
    ): ResponseEntity<ProvinceResponse> {
        val response = provinceService.getBySlug(languages, slug)
        return ResponseEntity.status(HttpStatus.OK).body(response)
    }

    @GetMapping("/code/{code}")
    fun getProvinceByCode(
        @RequestParam(name = "lang", defaultValue = "") languages: List<String>,
        @PathVariable code: Short,
    ): ResponseEntity<ProvinceResponse> {
        val response = provinceService.getByCode(languages, code)
        return ResponseEntity.status(HttpStatus.OK).body(response)
    }
}
