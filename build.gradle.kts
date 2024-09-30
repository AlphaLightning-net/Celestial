import io.papermc.paperweight.userdev.ReobfArtifactConfiguration

plugins {
    id("java-library")
    id("io.papermc.paperweight.userdev") version "1.7.3"
}

group = "net.alphalightning"
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
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(currentJavaVersion))
        }
        withSourcesJar()
        withJavadocJar()
    }

    compileJava {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(currentJavaVersion)
    }

    javadoc {
        options.encoding = Charsets.UTF_8.name()
    }
}
