import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("java-library")
    id("idea")
    id("com.gradleup.shadow") version ("8.3.6")
    id("maven-publish")
}

group = "dev.millenialsoftwares.utils"
version = "2025-Q4"

subprojects {
    apply {
        plugin("java")
        plugin("java-library")
        plugin("maven-publish")
        plugin("com.gradleup.shadow")
    }

    tasks.withType<ShadowJar> {
        archiveClassifier = ""
        archiveVersion = ""
        destinationDirectory = file("$rootDir/output")
    }
}