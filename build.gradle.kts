import io.papermc.paperweight.userdev.ReobfArtifactConfiguration

plugins {
    id("java-library")
    id("io.papermc.paperweight.userdev") version "1.7.3"
    id("maven-publish")
}

group = "net.alphalightning"
version = "1.0.4"
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

publishing {
    repositories {
        maven {
            url = uri("https://maven.pkg.github.com/AlphaLightning-net/Celestial")
            name = "GitHubPackages"
            credentials {
                username = project.findProperty("user") as String? ?: System.getenv("GITHUB_USERNAME")
                password = project.findProperty("token") as String? ?: System.getenv("GITHUB_TOKEN")
            }
        }
    }

    publications {
        register<MavenPublication>("gprRelease") {
            from(components["java"])
            groupId = project.group as String
            artifactId = project.name
            version = project.version as String
        }
    }
}

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

fun applyJavaDocsOptions(options: MinimalJavadocOptions) {
    val javaDocOptions = options as StandardJavadocDocletOptions
    javaDocOptions.links(
        "https://javadoc.io/doc/org.jetbrains/annotations/latest/",
        "https://docs.oracle.com/en/java/javase/${java.toolchain.languageVersion.get().asInt()}/docs/api/"
    )
}