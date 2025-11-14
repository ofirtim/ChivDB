group = project.parent!!.group
version = project.parent!!.version

dependencies {
    api(project(":core"))
    implementation(libs.jooQ)
    runtimeOnly(libs.postgreSQL)
    runtimeOnly(libs.mySQL)
    runtimeOnly(libs.sqLite)
}