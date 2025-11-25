group = project.parent!!.group
version = project.parent!!.version

dependencies {
    compileOnly(libs.paper)
    api(project(":core:common"))
}