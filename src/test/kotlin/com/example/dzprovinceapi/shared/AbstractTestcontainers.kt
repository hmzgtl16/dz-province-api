package com.example.dzprovinceapi.shared

import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.postgresql.PostgreSQLContainer

@Testcontainers
@ActiveProfiles("test")
abstract class AbstractTestcontainers {
    companion object {
        @Container
        @JvmStatic
        val container: PostgreSQLContainer =
            PostgreSQLContainer("postgres:18.6-alpine")
                .withDatabaseName("dz_province_test")
                .withUsername("dz_province_user")
                .withPassword("dz_province_password")
                .also(PostgreSQLContainer::start)

        @DynamicPropertySource
        @JvmStatic
        fun registerDatasourceProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", container::getJdbcUrl)
            registry.add("spring.datasource.username", container::getUsername)
            registry.add("spring.datasource.password", container::getPassword)
        }
    }
}
