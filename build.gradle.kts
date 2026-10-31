plugins {
    java
}

group = "me.endclose"
version = "1.0.0"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    // Если Gradle не находит эту версию, замени на актуальную с https://papermc.io/downloads/paper
    compileOnly("io.papermc.paper:paper-api:26.3-R0.1-SNAPSHOT")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(25))
}

tasks.processResources {
    filteringCharset = "UTF-8"
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}
