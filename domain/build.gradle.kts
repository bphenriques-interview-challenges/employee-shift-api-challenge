import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    id("org.springframework.boot") version PluginVersion.SPRING_BOOT
    id("io.spring.dependency-management") version PluginVersion.SPRING_DEPENDENCY_MANAGEMENT

    kotlin("plugin.spring") version PluginVersion.KOTLIN
}

// Weird? Yes, but expected: https://github.com/gradle/kotlin-dsl-samples/issues/843
val implementation by configurations
val testImplementation by configurations
dependencies {
    implementation("org.springframework.boot:spring-boot-starter")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${AppVersion.COROUTINES}")

    // Test Dependencies
    testImplementation(project(":common-test"))
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine") // exclude junit4
        exclude(module = "mockito-core")
    }
    testImplementation("io.mockk:mockk:${AppVersion.MOCKK}")
}

// See https://github.com/gradle/kotlin-dsl-samples/issues/393
tasks.getByName<BootJar>("bootJar") {
    enabled = false
}

tasks.getByName<Jar>("jar") {
    enabled = true
}
