package com.example.dzprovinceapi.province.api.dto

import com.example.dzprovinceapi.province.service.ProvinceService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
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
        @RequestParam(name = "lang", defaultValue = "") languages: List<String>,
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
        @PathVariable code: String,
    ): ResponseEntity<ProvinceResponse> {
        val response = provinceService.getByCode(languages, code)
        return ResponseEntity.status(HttpStatus.OK).body(response)
    }
}
