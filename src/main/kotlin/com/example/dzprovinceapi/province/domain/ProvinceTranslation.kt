package com.example.dzprovinceapi.province.domain

import com.example.dzprovinceapi.language.domain.Language
import jakarta.persistence.Column
import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.MapsId
import jakarta.persistence.Table
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction

@Entity
@Table(name = "province_translation", schema = "public")
class ProvinceTranslation {
    @EmbeddedId
    var id: ProvinceTranslationId? = null

    @MapsId("provinceId")
    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "province_id")
    var province: Province? = null

    @MapsId("languageId")
    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "language_id")
    var language: Language? = null

    @Column(name = "name")
    var name: String = ""
}
