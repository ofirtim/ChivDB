
plugins {
    `kotlin-dsl`
    java
}

group = "dev.millenialsoftwares.utils"
version = "2025-Q4"

repositories {
    maven {
        //temporarily used until officially changing repos and migrating to the new maven server for Millennial.
        name = "ApartiumNexus"
        url = uri("https://nexus.apartium.net/repository/maven-public")
    }
}