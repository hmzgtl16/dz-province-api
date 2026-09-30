package com.example.dzprovinceapi.shared

import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.postgresql.PostgreSQLContainer

@Testcontainers
@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
abstract class AbstractDataJdbcTest {
    @Container
    val container: PostgreSQLContainer =
        PostgreSQLContainer("postgres:18.6-alpine")
            .withDatabaseName("dz_province_test")
            .withUsername("dz_province_user")
            .withPassword("dz_province_password")

    @DynamicPropertySource
    fun registerDatasourceProperties(registry: DynamicPropertyRegistry) {
        registry.add("spring.datasource.url", container::getJdbcUrl)
        registry.add("spring.datasource.username", container::getUsername)
        registry.add("spring.datasource.password", container::getPassword)
    }
}
