package com.example.dzprovinceapi.province.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import org.hibernate.annotations.ColumnDefault
import java.util.UUID

@Entity
@Table(name = "province", schema = "public")
class Province {
    @Id
    var id: UUID? = null

    @Column(name = "code")
    var code: String = ""

    @Column(name = "slug")
    var slug: String = ""

    @OneToMany
    @JoinColumn(name = "province_id")
    var provinceTranslations: MutableSet<ProvinceTranslation> = mutableSetOf()
}
