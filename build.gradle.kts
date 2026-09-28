plugins {
    `kotlin-dsl`
    `maven-publish`
}

group = "ingsis.snippet"
version = "0.1.0"

repositories {
    gradlePluginPortal()
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:2.0.21")
    implementation("org.jetbrains.kotlin:kotlin-allopen:2.0.21")
    implementation("com.diffplug.spotless:spotless-plugin-gradle:6.25.0")
    implementation("io.gitlab.arturbosch.detekt:detekt-gradle-plugin:1.23.7")
    implementation("org.springframework.boot:spring-boot-gradle-plugin:3.3.3")
    implementation("io.spring.gradle:dependency-management-plugin:1.1.6")
}

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            val targetRepo = System.getenv("GITHUB_REPOSITORY") ?: "IngsisMac/snippet-plugins"
            url = uri("https://maven.pkg.github.com/$targetRepo")
            credentials {
                username = System.getenv("GITHUB_ACTOR") ?: "git"
                password = System.getenv("GITHUB_TOKEN") ?: ""
            }
        }
    }
}
