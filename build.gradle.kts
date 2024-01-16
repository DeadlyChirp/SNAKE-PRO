plugins {
    id("java")
    id("application")
}

group = "tangNdam"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // Your other dependencies here
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    implementation("javazoom:jlayer:1.0.1")
}

application {
    // Correctly specify the main class with the package name
    mainClass.set("tangNdam.slither.Main")
}

tasks.test {
    useJUnitPlatform()
}
