/*
 * Copyright 2026 Hamza Gattal
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.dzprovinceapi.province.api

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.assertj.MockMvcTester

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ProvinceControllerTest {
    @Autowired
    private lateinit var mockMvcTester: MockMvcTester

    // ------------------------------------------------------------------
    // getAllProvinces
    // ------------------------------------------------------------------

    @Test
    fun `getProvinces should return 200 with page`() {
        val result =
            mockMvcTester
                .get()
                .uri("/api/1.0/provinces")
                .param("lang", "en", "fr")

        assertThat(result)
            .hasStatusOk()
            .bodyJson()
            .hasPathSatisfying("empty") { it.assertThat().isEqualTo(false) }
            .hasPathSatisfying("first") { it.assertThat().isEqualTo(true) }
            .hasPathSatisfying("last") { it.assertThat().isEqualTo(false) }
            .hasPathSatisfying("number") { it.assertThat().isEqualTo(0) }
            .hasPathSatisfying("number-of-elements") { it.assertThat().isEqualTo(10) }
            .hasPathSatisfying("size") { it.assertThat().isEqualTo(10) }
            .hasPathSatisfying("total-elements") { it.assertThat().isEqualTo(31) }
            .hasPathSatisfying("total-pages") { it.assertThat().isEqualTo(4) }
            .hasPathSatisfying("content") { it.assertThat().isNotEmpty }
    }

    @Test
    fun `getProvinces without lang should use empty list`() {
        val result =
            mockMvcTester
                .get()
                .uri("/api/1.0/provinces")

        assertThat(result)
            .hasStatusOk()
            .bodyJson()
            .hasPathSatisfying("empty") { it.assertThat().isEqualTo(false) }
            .hasPathSatisfying("first") { it.assertThat().isEqualTo(true) }
            .hasPathSatisfying("last") { it.assertThat().isEqualTo(false) }
            .hasPathSatisfying("number") { it.assertThat().isEqualTo(0) }
            .hasPathSatisfying("number-of-elements") { it.assertThat().isEqualTo(10) }
            .hasPathSatisfying("size") { it.assertThat().isEqualTo(10) }
            .hasPathSatisfying("total-elements") { it.assertThat().isEqualTo(31) }
            .hasPathSatisfying("total-pages") { it.assertThat().isEqualTo(4) }
            .hasPathSatisfying("content") { it.assertThat().isNotEmpty }
    }

    @Test
    fun `getProvinces without version segment should return 404`() {
        val result =
            mockMvcTester
                .get()
                .uri("/api/provinces")

        assertThat(result)
            .hasStatus(404)
    }

    // ------------------------------------------------------------------
    // getProvinceBySlug
    // ------------------------------------------------------------------

    @Test
    fun `getProvinceBySlug should return 200`() {
        val result =
            mockMvcTester
                .get()
                .uri("/api/1.0/provinces/slug/algiers")
                .param("lang", "en")

        assertThat(result)
            .hasStatusOk()
            .bodyJson()
            .hasPathSatisfying("$.slug") { it.assertThat().isEqualTo("algiers") }
    }

    @Test
    fun `getProvinceBySlug without lang should pass an empty list to the service`() {
        val result =
            mockMvcTester
                .get()
                .uri("/api/1.0/provinces/slug/algiers")

        assertThat(result)
            .hasStatusOk()
            .bodyJson()
            .hasPathSatisfying("$.slug") { it.assertThat().isEqualTo("algiers") }
    }

    @Test
    fun `getProvinceBySlug when not found should return 404`() {
        val result =
            mockMvcTester
                .get()
                .uri("/api/1.0/provinces/slug/unknown")
                .param("lang", "en")

        assertThat(result)
            .hasStatus(404)
    }

    @Test
    fun `getProvinceBySlug without version segment should return 404`() {
        val result =
            mockMvcTester
                .get()
                .uri("/api/provinces/slug/algiers")

        assertThat(result)
            .hasStatus(404)
    }

    // ------------------------------------------------------------------
    // getProvinceByCode
    // ------------------------------------------------------------------

    @Test
    fun `getProvinceByCode should return 200`() {
        val result =
            mockMvcTester
                .get()
                .uri("/api/1.0/provinces/code/16")
                .param("lang", "fr")

        assertThat(result)
            .hasStatusOk()
            .bodyJson()
            .hasPathSatisfying("$.code") { it.assertThat().isEqualTo("16") }
    }

    @Test
    fun `getProvinceByCode without lang should pass an empty list to the service`() {
        val result =
            mockMvcTester
                .get()
                .uri("/api/1.0/provinces/code/16")

        assertThat(result)
            .hasStatusOk()
            .bodyJson()
            .hasPathSatisfying("$.code") { it.assertThat().isEqualTo("16") }
    }

    @Test
    fun `getProvinceByCode when not found should return 404`() {
        val result =
            mockMvcTester
                .get()
                .uri("/api/1.0/provinces/code/99")
                .param("lang", "en")

        assertThat(result)
            .hasStatus(404)
    }

    @Test
    fun `getProvinceByCode without version segment should return 404`() {
        val result =
            mockMvcTester
                .get()
                .uri("/api/provinces/code/16")

        assertThat(result)
            .hasStatus(404)
    }
}
