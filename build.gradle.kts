plugins {
    id("java")
    id("application")
}

group = "com.onliferp"
version = "1.0"

repositories {
    mavenCentral()
}

application {
    applicationDefaultJvmArgs = listOf("-Duser.timezone=Europe/Paris")
}

dependencies {
    // https://mvnrepository.com/artifact/net.dv8tion/JDA
    implementation("net.dv8tion:JDA:5.0.0") {
        exclude(module="opus-java")
    }

    // https://mvnrepository.com/artifact/com.fasterxml.jackson.core
    implementation("com.fasterxml.jackson.core:jackson-databind:2.17.2")
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.17.2")

    // https://mvnrepository.com/artifact/io.github.stepio.jgforms/jgforms
    implementation("io.github.stepio.jgforms:jgforms:1.0.1")

    // https://mvnrepository.com/artifact/io.github.cdimascio/dotenv-java
    implementation("io.github.cdimascio:dotenv-java:3.0.1")

    // https://mvnrepository.com/artifact/org.javatuples/javatuples
    implementation("org.javatuples:javatuples:1.2")

    implementation("net.fellbaum:jemoji:1.3.4")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}