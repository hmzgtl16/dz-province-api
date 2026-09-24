package com.example.dzprovinceapi.province.api.dto

import com.example.dzprovinceapi.province.service.ProvinceService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort.Direction
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/{version}/provinces", version = "1.0")
class ProvinceController(
    private val provinceService: ProvinceService,
) {
    @GetMapping
    fun getProvinces(
        @PageableDefault pageable: Pageable,
    ): ResponseEntity<Page<ProvinceResponse>> {
        val response = provinceService.getAllProvinces(pageable)
        return ResponseEntity.status(HttpStatus.OK).body(response)
    }

    @GetMapping("/{id}")
    fun getProvinceById(
        @PathVariable id: Short,
    ): ResponseEntity<ProvinceResponse> {
        val response = provinceService.getById(id)
        return ResponseEntity.status(HttpStatus.OK).body(response)
    }
}
