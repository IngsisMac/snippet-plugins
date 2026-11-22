# snippet-plugins

Gradle Convention Plugins para la suite de microservicios de **Snippet Playground** (Ingeniería de Sistemas 2026).

## Plugins Disponibles

| Plugin ID | Descripción |
|---|---|
| `ingsis.snippet.quality` | Configuración de calidad: Spotless (ktlint 1.3.1), Detekt (reglas calibradas) y JaCoCo (cobertura mínima 80%). |
| `ingsis.snippet.spring` | Configuración base para servicios Spring Boot con Kotlin y JVM 21 toolchain (aplica `ingsis.snippet.quality`). Incluye `plugin.spring` y `plugin.jpa` con `allOpen` para entidades: sin ellos Hibernate no puede instanciar las clases Kotlin (`No default constructor for entity`). |
| `ingsis.snippet.publish` | Configuración de publicación hacia GitHub Packages y Maven Local. |

## Uso en Microservicios

En el archivo `settings.gradle.kts` del microservicio:

```kotlin
pluginManagement {
    // La version no vive en el repo del servicio: por defecto se resuelve la ultima
    // publicada en GitHub Packages. Se pinnea con -PsnippetPluginsVersion=0.1.1 o con
    // la variable SNIPPET_PLUGINS_VERSION.
    val pinnedPluginsVersion: String? =
        providers.gradleProperty("snippetPluginsVersion").orNull
            ?: System.getenv("SNIPPET_PLUGINS_VERSION")

    repositories {
        // mavenLocal solo cuando hay version pinneada: con una version flotante un
        // artefacto viejo en ~/.m2 le ganaria para siempre al registry.
        if (pinnedPluginsVersion != null) {
            mavenLocal()
        }
        gradlePluginPortal()
        mavenCentral()
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/IngsisMac/snippet-plugins")
            credentials {
                username = providers.gradleProperty("gpr.user").orNull ?: System.getenv("GITHUB_ACTOR")
                password = providers.gradleProperty("gpr.key").orNull ?: System.getenv("GITHUB_TOKEN")
            }
        }
    }

    resolutionStrategy {
        eachPlugin {
            if (requested.id.id.startsWith("ingsis.snippet.")) {
                useVersion(pinnedPluginsVersion ?: "latest.release")
            }
        }
    }
}
```

En el archivo `build.gradle.kts` del microservicio, **sin version**:

```kotlin
plugins {
    id("ingsis.snippet.spring")
}
```

Para un build reproducible o para probar un plugin que todavia no se publico:

```bash
./gradlew build -PsnippetPluginsVersion=0.1.1
```

## Publicación Local

Para compilar y publicar en la máquina local (`~/.m2/repository`):

```bash
./gradlew publishToMavenLocal
```
