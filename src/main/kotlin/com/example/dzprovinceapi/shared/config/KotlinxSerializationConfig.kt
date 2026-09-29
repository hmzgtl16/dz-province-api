package com.example.dzprovinceapi.shared.config

import com.example.dzprovinceapi.shared.serializer.PageSerializer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.domain.Page

@Configuration
class KotlinxSerializationConfig {
    @Bean
    fun json(): Json =
        Json {
            serializersModule =
                SerializersModule {
                    contextual(Page::class) {
                        @Suppress("UNCHECKED_CAST")
                        (PageSerializer(it.first() as KSerializer<Any>))
                    }
                }
        }
}
