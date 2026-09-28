plugins {
    `maven-publish`
}

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            val targetRepo = System.getenv("GITHUB_REPOSITORY") ?: "IngsisMac/snippet"
            url = uri("https://maven.pkg.github.com/$targetRepo")
            credentials {
                username = System.getenv("GITHUB_ACTOR") ?: "git"
                password = System.getenv("GITHUB_TOKEN") ?: ""
            }
        }
    }
}
