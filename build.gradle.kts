import io.papermc.paperweight.userdev.ReobfArtifactConfiguration

plugins {
    id("java-library")
    id("maven-publish")
    id("io.papermc.paperweight.userdev") version "1.7.3"
}

group = "org.breezora"
version = "1.0.0"
description = "A fast and easy to use scoreboard library"

val currentJavaVersion = 22

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    paperweight.paperDevBundle("1.21.1-R0.1-SNAPSHOT")

    // Dependencies that are already provided by another source
    compileOnly("org.jetbrains:annotations:24.1.0")
}

// Since we don't care about supporting older versions and spigot, we don't have to reobfuscate our code
paperweight.reobfArtifactConfiguration = ReobfArtifactConfiguration.MOJANG_PRODUCTION

tasks {
    java {
        withSourcesJar()
        withJavadocJar()
    }

    compileJava {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(currentJavaVersion)
    }

    javadoc {
        options {
            encoding = Charsets.UTF_8.name()
            memberLevel = JavadocMemberLevel.PUBLIC
        }
    }

    register<Javadoc>("alljavadoc") {
        applyJavaDocsOptions(options)

        setDestinationDir(file("${layout.buildDirectory.get()}/docs/javadoc"))
        val projects = project.rootProject.allprojects.filter { p -> !p.name.contains("example") }
        setSource(projects.map { p -> p.sourceSets.main.get().allJava.filter { project -> project.name != "module-info.java" } })
        classpath = files(projects.map { p -> p.sourceSets.main.get().compileClasspath })
    }
}

publishing {
    repositories {
        maven("https://repo.breezora.net/intern") {
            name = "breezoraRepositoryIntern"
            credentials {
                username = System.getenv("MAVEN_NAME") ?: project.findProperty("breezoraRepositoryInternUsername") as String?
                password = System.getenv("MAVEN_SECRET") ?: project.findProperty("breezoraRepositoryInternPassword") as String?
            }
        }
    }
    publications {
        create<MavenPublication>("maven") {
            groupId = project.group as String
            version = project.version as String
            artifactId = project.name
            from(components["java"])
        }
    }
}

fun applyJavaDocsOptions(options: MinimalJavadocOptions) {
    val javaDocOptions = options as StandardJavadocDocletOptions
    javaDocOptions.links(
        "https://javadoc.io/doc/org.jetbrains/annotations/latest/",
        "https://docs.oracle.com/en/java/javase/${java.toolchain.languageVersion.get().asInt()}/docs/api/"
    )
}

// Test comment
