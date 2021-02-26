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
    implementation(project(":domain"))
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    // Spring Boot Dependencies
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${AppVersion.COROUTINES}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactive:${AppVersion.COROUTINES}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:${AppVersion.COROUTINES}")

    // Telemetry
    implementation("io.micrometer:micrometer-registry-prometheus")

    // Test Dependencies
    testImplementation(project(":common-test"))
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine") // exclude junit4
        exclude(module = "mockito-core")
    }
    testImplementation("io.projectreactor:reactor-test")
    testImplementation("io.mockk:mockk:${AppVersion.MOCKK}")
    testImplementation("com.ninja-squad:springmockk:${AppVersion.SPRING_MOCKK}")
}

// Setup acceptance Tests Target
sourceSets {
    create("acceptanceTest") {
        withConvention(KotlinSourceSet::class) {
            kotlin.srcDir("src/acceptanceTest/kotlin")
            resources.srcDir("src/acceptanceTest/resources")
            compileClasspath += sourceSets["main"].output + configurations["testRuntimeClasspath"]
            runtimeClasspath += output + compileClasspath + sourceSets["test"].runtimeClasspath
        }
    }
}
task<Test>("acceptanceTest") {
    description = "Runs the acceptance tests"
    group = "verification"
    testClassesDirs = sourceSets["acceptanceTest"].output.classesDirs
    classpath = sourceSets["acceptanceTest"].runtimeClasspath
}

tasks.getByName<BootJar>("bootJar") {
    this.archiveFileName.set("employee-shift-api.jar")
}
