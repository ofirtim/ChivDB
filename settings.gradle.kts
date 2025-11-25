rootProject.name = "ChivDB"

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        maven {
            name = "apartium-plugins"
            url = uri("https://nexus.apartium.net/repository/gradle-public/")
        }

        maven {
            name = "papermc"
            url = uri("https://repo.papermc.io/repository/maven-public/")
        }
    }
}

include(
    "core:implementation",
    "core:common",
    "engines",
    "engines:jooq",
    "extensions:PaperConnector"
)