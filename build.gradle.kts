import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version PluginVersion.KOTLIN
    id("org.jlleitschuh.gradle.ktlint") version PluginVersion.KTLINT
}

group = "com.bphenriques"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

allprojects {
    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.jlleitschuh.gradle.ktlint")

    dependencies {
        // Base dependencies
        implementation("org.jetbrains.kotlin:kotlin-reflect")
        implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
            jvmTarget = "11"
        }
    }

    // Setup All tests
    tasks.withType<Test> {
        testLogging {
            showStandardStreams = true
        }
        useJUnitPlatform()
    }
}
