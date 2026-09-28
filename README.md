# snippet-plugins

Gradle Convention Plugins para la suite de microservicios de **Snippet Playground** (Ingeniería de Sistemas 2026).

## Plugins Disponibles

| Plugin ID | Descripción |
|---|---|
| `ingsis.snippet.quality` | Configuración de calidad: Spotless (ktlint 1.3.1), Detekt (reglas calibradas) y JaCoCo (cobertura mínima 80%). |
| `ingsis.snippet.spring` | Configuración base para servicios Spring Boot con Kotlin y JVM 21 toolchain (aplica `ingsis.snippet.quality`). |
| `ingsis.snippet.publish` | Configuración de publicación hacia GitHub Packages y Maven Local. |

## Uso en Microservicios

En el archivo `settings.gradle.kts` del microservicio:

```kotlin
pluginManagement {
    repositories {
        mavenLocal()
        gradlePluginPortal()
        mavenCentral()
    }
}
```

En el archivo `build.gradle.kts` del microservicio:

```kotlin
plugins {
    id("ingsis.snippet.spring") version "0.1.0"
}
```

## Publicación Local

Para compilar y publicar en la máquina local (`~/.m2/repository`):

```bash
./gradlew publishToMavenLocal
```
