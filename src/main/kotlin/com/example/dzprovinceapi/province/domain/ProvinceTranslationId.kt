package com.example.dzprovinceapi.province.domain

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import org.hibernate.Hibernate
import java.io.Serializable
import java.util.Objects
import java.util.UUID

@Embeddable
class ProvinceTranslationId : Serializable {
    @Column(name = "province_id", nullable = false)
    var provinceId: UUID? = null

    @Column(name = "language_id", nullable = false)
    var languageId: UUID? = null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false

        other as ProvinceTranslationId

        return provinceId == other.provinceId &&
            languageId == other.languageId
    }

    override fun hashCode(): Int = Objects.hash(provinceId, languageId)

    companion object {
        private const val serialVersionUID = 0L
    }
}
