package com.example.dzprovinceapi.language.domain

import com.example.dzprovinceapi.province.domain.ProvinceTranslation
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import org.hibernate.annotations.ColumnDefault
import java.util.UUID

@Entity
@Table(name = "language", schema = "public")
class Language {
    @Id
    var id: UUID? = null

    @Column(name = "code")
    var code: String = ""

    @Column(name = "name")
    var name: String = ""

    @OneToMany
    @JoinColumn(name = "language_id")
    var provinceTranslations: MutableSet<ProvinceTranslation> = mutableSetOf()
}
