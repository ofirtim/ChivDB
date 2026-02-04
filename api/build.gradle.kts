group = project.parent!!.group
version = project.parent!!.version

dependencies {
    runtimeOnly(libs.lombok)
    annotationProcessor(libs.lombok)
}