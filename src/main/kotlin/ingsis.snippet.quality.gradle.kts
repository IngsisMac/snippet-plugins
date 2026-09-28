plugins {
    id("com.diffplug.spotless")
    id("io.gitlab.arturbosch.detekt")
    jacoco
}

configure<com.diffplug.gradle.spotless.SpotlessExtension> {
    kotlin {
        target("**/*.kt")
        targetExclude("**/build/**")
        ktlint("1.3.1")
    }
    kotlinGradle {
        target("**/*.gradle.kts")
        ktlint("1.3.1")
    }
}

configure<io.gitlab.arturbosch.detekt.extensions.DetektExtension> {
    toolVersion = "1.23.7"
    buildUponDefaultConfig = true
    allRules = false
    val configFile = file("$rootDir/config/detekt/detekt.yml")
    if (configFile.exists()) {
        config.setFrom(files(configFile))
    } else {
        val fallbackDir = layout.buildDirectory.dir("detekt-config").get().asFile
        val fallbackConfig = File(fallbackDir, "detekt.yml")
        if (!fallbackConfig.exists()) {
            fallbackDir.mkdirs()
            javaClass.classLoader.getResourceAsStream("default-detekt.yml")?.use { input ->
                fallbackConfig.outputStream().use { output -> input.copyTo(output) }
            }
        }
        if (fallbackConfig.exists()) {
            config.setFrom(files(fallbackConfig))
        }
    }
}

dependencies {
    "detekt"("io.gitlab.arturbosch.detekt:detekt-cli:1.23.7")
}

configurations.matching { it.name.startsWith("detekt") }.configureEach {
    resolutionStrategy.eachDependency {
        if (requested.group == "org.jetbrains.kotlin") {
            useVersion("2.0.10")
        }
    }
}

jacoco {
    toolVersion = "0.8.12"
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
    finalizedBy("jacocoTestReport", "jacocoTestCoverageVerification")
}

tasks.withType<JacocoReport>().configureEach {
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
}

tasks.withType<JacocoCoverageVerification>().configureEach {
    violationRules {
        rule {
            element = "BUNDLE"
            limit {
                counter = "LINE"
                value = "COVEREDRATIO"
                minimum = "0.80".toBigDecimal()
            }
        }
    }
}

tasks.register("installGitHooks") {
    group = "build setup"
    description = "Installs Git pre-commit hook enforcing spotless and check"
    doLast {
        val gitDir = file("$rootDir/.git")
        if (!gitDir.exists()) {
            println("ℹ️ No .git directory found at $rootDir. Skipping git hook installation.")
            return@doLast
        }
        val hooksDir = file("$rootDir/.git/hooks")
        if (!hooksDir.exists()) {
            hooksDir.mkdirs()
        }
        val preCommitFile = File(hooksDir, "pre-commit")
        val hookContent =
            """
            #!/bin/sh
            echo "==> [IngSIS Quality] Running pre-commit verification..."
            ./gradlew spotlessApply
            ./gradlew check
            if [ ${'$'}? -ne 0 ]; then
                echo "❌ Pre-commit quality checks failed! Please fix errors before committing."
                exit 1
            fi
            echo "✅ Pre-commit quality checks passed!"
            """.trimIndent() + "\n"

        preCommitFile.writeText(hookContent)
        preCommitFile.setExecutable(true, false)
        println("✅ Successfully installed pre-commit hook at ${preCommitFile.absolutePath}")
    }
}

tasks.register("installQualityConfig") {
    group = "build setup"
    description = "Installs default .editorconfig and detekt.yml into repository if absent"
    doLast {
        val editorConfig = file("$rootDir/.editorconfig")
        if (!editorConfig.exists()) {
            javaClass.classLoader.getResourceAsStream("default.editorconfig")?.use { input ->
                editorConfig.outputStream().use { output -> input.copyTo(output) }
            }
            println("✅ Generated .editorconfig from plugin defaults")
        }

        val detektConfig = file("$rootDir/config/detekt/detekt.yml")
        if (!detektConfig.exists()) {
            detektConfig.parentFile.mkdirs()
            javaClass.classLoader.getResourceAsStream("default-detekt.yml")?.use { input ->
                detektConfig.outputStream().use { output -> input.copyTo(output) }
            }
            println("✅ Generated config/detekt/detekt.yml from plugin defaults")
        }
    }
}

