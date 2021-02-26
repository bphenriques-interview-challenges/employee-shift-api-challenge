import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
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
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactive:${AppVersion.COROUTINES}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:${AppVersion.COROUTINES}")

    // Persistence
    implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
    implementation("io.r2dbc:r2dbc-postgresql")
    implementation("org.postgresql:postgresql")
    implementation("org.flywaydb:flyway-core")

    // Test Dependencies
    testImplementation(project(":common-test"))
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine") // exclude junit4
        exclude(module = "mockito-core")
    }
    testImplementation("io.projectreactor:reactor-test")
}

// Setup Integration Tests Target
sourceSets {
    create("integrationTest") {
        withConvention(KotlinSourceSet::class) {
            kotlin.srcDir("src/integrationTest/kotlin")
            resources.srcDir("src/integrationTest/resources")
            compileClasspath += sourceSets["main"].output + configurations["testRuntimeClasspath"]
            runtimeClasspath += output + compileClasspath + sourceSets["test"].runtimeClasspath
        }
    }
}
task<Test>("integrationTest") {
    description = "Runs the integration tests"
    group = "verification"
    testClassesDirs = sourceSets["integrationTest"].output.classesDirs
    classpath = sourceSets["integrationTest"].runtimeClasspath
}

// See https://github.com/gradle/kotlin-dsl-samples/issues/393
tasks.getByName<BootJar>("bootJar") {
    enabled = false
}

tasks.getByName<Jar>("jar") {
    enabled = true
}
