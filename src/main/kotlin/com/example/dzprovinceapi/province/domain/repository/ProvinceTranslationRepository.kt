package com.example.dzprovinceapi.province.domain.repository

import com.example.dzprovinceapi.province.domain.medel.ProvinceTranslation
import com.example.dzprovinceapi.province.domain.medel.ProvinceTranslationId
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ProvinceTranslationRepository : CrudRepository<ProvinceTranslation, ProvinceTranslationId>
