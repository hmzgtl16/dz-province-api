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
plugins {
    alias(libs.plugins.graalvm.native)
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.spotless)
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependencyManagement)
}

group = "com.example"
version = "0.0.1-SNAPSHOT"
description = "dz-province-api"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.flyway.postgresql)
    implementation(libs.kotlin.reflect)
    implementation(libs.spring.boot.starter.data.jdbc)
    implementation(libs.spring.boot.starter.flyway)
    implementation(libs.spring.boot.starter.kotlinx.serialization.json)
    implementation(libs.spring.boot.starter.webmvc)
    developmentOnly(libs.spring.boot.docker.compose)
    runtimeOnly(libs.postgresql)
    testImplementation(libs.spring.boot.starter.data.jdbc.test)
    testImplementation(libs.spring.boot.starter.flyway.test)
    testImplementation(libs.spring.boot.starter.kotlinx.serialization.json.test)
    testImplementation(libs.spring.boot.starter.webmvc.test)
    testImplementation(libs.spring.boot.testcontainers)
    testImplementation(libs.kotlin.test.junit5)
    testImplementation(libs.testcontainers.junit.jupiter)
    testImplementation(libs.testcontainers.postgresql)
    testRuntimeOnly(libs.h2database)
    testRuntimeOnly(libs.junit.platform.launcher)
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict", "-Xannotation-default-target=param-property")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

spotless {
    kotlin {
        ktlint(libs.versions.ktlint.get())
        licenseHeaderFile(rootDir.resolve("spotless/license-header.kt"))
    }
    kotlinGradle {
        ktlint(libs.versions.ktlint.get())
        licenseHeaderFile(rootDir.resolve("spotless/license-header.kts"), "(^(?![\\/ ]\\*).*$)")
    }
    yaml {
        target("**/*.yaml")
        targetExclude("spotless/**")
        prettier(libs.versions.prettier.get())
        licenseHeaderFile(rootDir.resolve("spotless/license-header.yaml"), "^[^#]")
    }
    sql {
        target("**/*.sql")
        targetExclude("spotless/**")
        prettier(libs.versions.prettier.get())
        licenseHeaderFile(rootDir.resolve("spotless/license-header.sql"), "^[^-]")
    }
}
