plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
    // Hibernate instancia las entidades por reflexión: plugin.jpa genera el constructor sin
    // argumentos y allOpen deja las clases abiertas para los proxies de carga perezosa.
    kotlin("plugin.jpa")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}

apply(plugin = "ingsis.snippet.quality")

kotlin {
    jvmToolchain(21)
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}
