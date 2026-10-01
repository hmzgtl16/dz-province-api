package com.example.dzprovinceapi.province.domain

import com.example.dzprovinceapi.shared.AbstractDataJdbcTest
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.groups.Tuple
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class ProvinceRepositoryTest : AbstractDataJdbcTest() {
    @Autowired
    private lateinit var provinceRepository: ProvinceRepository

    // ------------------------------------------------------------------
    // findProvinceByCode
    // ------------------------------------------------------------------

    @Test
    fun findProvinceByCode_returnsProvince_whenCodeExists() {
        val result = provinceRepository.findProvinceByCode("16")

        assertThat(result).isNotNull
        assertThat(result!!.code).isEqualTo("16")
        assertThat(result.slug).isEqualTo("algiers")
    }

    @Test
    fun findProvinceByCode_returnsNull_whenCodeAbsent() {
        val result = provinceRepository.findProvinceByCode("99")

        assertThat(result).isNull()
    }

    @Test
    fun findProvinceByCode_returnsExactMatch_whenMultipleExist() {
        val result = provinceRepository.findProvinceByCode("31")

        assertThat(result).isNotNull
        assertThat(result!!.slug).isEqualTo("oran")
    }

    // ------------------------------------------------------------------
    // findProvinceBySlug
    // ------------------------------------------------------------------

    @Test
    fun findProvinceBySlug_returnsProvince_whenSlugExists() {
        val result = provinceRepository.findProvinceBySlug("algiers")

        assertThat(result).isNotNull
        assertThat(result!!.code).isEqualTo("16")
    }

    @Test
    fun findProvinceBySlug_returnsNull_whenSlugAbsent() {
        val result = provinceRepository.findProvinceBySlug("abcde")

        assertThat(result).isNull()
    }

    @Test
    fun findProvinceBySlug_isCaseSensitive() {
        assertThat(provinceRepository.findProvinceBySlug("ALGIERS")).isNull()
        assertThat(provinceRepository.findProvinceBySlug("Algiers")).isNull()
        assertThat(provinceRepository.findProvinceBySlug("algiers")).isNotNull
    }

    // ------------------------------------------------------------------
    // Translations
    // ------------------------------------------------------------------

    @Test
    fun findProvinceByCode_loadsTranslations() {
        val result = provinceRepository.findProvinceByCode("16")

        assertThat(result!!.provinceTranslations)
            .extracting(ProvinceTranslation::name)
            .containsExactlyInAnyOrder(Tuple("Alger"), Tuple("الجزائر"), Tuple("Algiers"))
    }
}
