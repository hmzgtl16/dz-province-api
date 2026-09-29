package com.example.dzprovinceapi.province.domain

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface ProvinceRepository : JpaRepository<Province, UUID> {
    fun findProvinceByCode(code: String): Province?

    fun findProvinceBySlug(slug: String): Province?

    @Query(
        "SELECT p FROM Province p JOIN ProvinceTranslation pt ON pt.id.provinceId = p.id JOIN Language l ON l.id = pt.id.languageId WHERE l.code IN :languages",
    )
    fun findProvincesByLanguages(
        languages: List<String>,
        pageable: Pageable,
    ): Page<Province>
}
