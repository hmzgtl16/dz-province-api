package com.example.dzprovinceapi.shared.converter

import com.example.dzprovinceapi.language.domain.Language
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.WritingConverter

@WritingConverter
class LanguageWritingConverter : Converter<Language, String> {
    override fun convert(source: Language): String = source.code
}
