package com.example.dzprovinceapi.shared.converter

import com.example.dzprovinceapi.language.domain.Language
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter

@ReadingConverter
class LanguageReadingConverter : Converter<String, Language> {
    override fun convert(source: String): Language = Language.entries.first { it.code == source }
}
