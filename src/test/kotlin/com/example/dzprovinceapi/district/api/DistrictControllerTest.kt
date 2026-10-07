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
package com.example.dzprovinceapi.district.api

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
class DistrictControllerTest {
    @Autowired
    private lateinit var mockMvcTester: MockMvcTester

    // ------------------------------------------------------------------
    // getDistricts
    // ------------------------------------------------------------------

    @Test
    fun `getDistricts should return 200 with page`() {
        val result =
            mockMvcTester
                .get()
                .uri("/api/1.0/districts")
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
            .hasPathSatisfying("total-elements") { it.assertThat().isEqualTo(123) }
            .hasPathSatisfying("total-pages") { it.assertThat().isEqualTo(13) }
            .hasPathSatisfying("content") { it.assertThat().isNotEmpty }
    }

    @Test
    fun `getDistricts without lang should use empty list`() {
        val result =
            mockMvcTester
                .get()
                .uri("/api/1.0/districts")

        assertThat(result)
            .hasStatusOk()
            .bodyJson()
            .hasPathSatisfying("empty") { it.assertThat().isEqualTo(false) }
            .hasPathSatisfying("first") { it.assertThat().isEqualTo(true) }
            .hasPathSatisfying("last") { it.assertThat().isEqualTo(false) }
            .hasPathSatisfying("number") { it.assertThat().isEqualTo(0) }
            .hasPathSatisfying("number-of-elements") { it.assertThat().isEqualTo(10) }
            .hasPathSatisfying("size") { it.assertThat().isEqualTo(10) }
            .hasPathSatisfying("total-elements") { it.assertThat().isEqualTo(123) }
            .hasPathSatisfying("total-pages") { it.assertThat().isEqualTo(13) }
            .hasPathSatisfying("content") { it.assertThat().isNotEmpty }
    }

    @Test
    fun `getDistricts without version segment should return 404`() {
        val result =
            mockMvcTester
                .get()
                .uri("/api/districts")

        assertThat(result)
            .hasStatus(404)
    }

    // ------------------------------------------------------------------
    // getDistrictBySlug
    // ------------------------------------------------------------------

    @Test
    fun `getDistrictBySlug should return 200`() {
        val result =
            mockMvcTester
                .get()
                .uri("/api/1.0/districts/slug/bab-el-oued")
                .param("lang", "en")

        assertThat(result)
            .hasStatusOk()
            .bodyJson()
            .hasPathSatisfying("$.slug") { it.assertThat().isEqualTo("bab-el-oued") }
    }

    @Test
    fun `getDistrictBySlug without lang should pass an empty list to the service`() {
        val result =
            mockMvcTester
                .get()
                .uri("/api/1.0/districts/slug/bab-el-oued")

        assertThat(result)
            .hasStatusOk()
            .bodyJson()
            .hasPathSatisfying("$.slug") { it.assertThat().isEqualTo("bab-el-oued") }
    }

    @Test
    fun `getDistrictBySlug returns payload with requested language names`() {
        val result =
            mockMvcTester
                .get()
                .uri("/api/1.0/districts/slug/bab-el-oued")
                .param("lang", "en")

        assertThat(result)
            .hasStatusOk()
            .bodyJson()
            .hasPathSatisfying("$.slug") { it.assertThat().isEqualTo("bab-el-oued") }
            .hasPathSatisfying("$.names") { it.assertThat().isNotEmpty }
    }

    @Test
    fun `getDistrictBySlug when not found should return 404`() {
        val result =
            mockMvcTester
                .get()
                .uri("/api/1.0/districts/slug/unknown")
                .param("lang", "en")

        assertThat(result)
            .hasStatus(404)
    }

    @Test
    fun `getDistrictBySlug without version segment should return 404`() {
        val result =
            mockMvcTester
                .get()
                .uri("/api/districts/slug/bab-el-oued")

        assertThat(result)
            .hasStatus(404)
    }
}
