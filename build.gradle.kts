import io.papermc.paperweight.userdev.ReobfArtifactConfiguration
import xyz.jpenilla.resourcefactory.bukkit.BukkitPluginYaml

plugins {
    id("java-library")
    id("io.papermc.paperweight.userdev") version "1.7.3"
    id("xyz.jpenilla.run-paper") version "2.3.1"
    id("xyz.jpenilla.resource-factory-bukkit-convention") version "1.2.0"
}

group = "net.alphalightning"
version = "1.0.0-SNAPSHOT"
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

    // Dependencies required for testing
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.1")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
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

    test {
        useJUnitPlatform()
        testLogging {
            events("passed", "skipped", "failed")
        }
    }

    compileTestJava {
        options.encoding = Charsets.UTF_8.name()
    }

    compileJava {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(currentJavaVersion)
    }

    javadoc {
        options.encoding = Charsets.UTF_8.name()
    }

    runServer {
        minecraftVersion("1.21.1")
    }
}

bukkitPluginYaml {
    main = "$group.${rootProject.name}.plugin.CelestialPlugin"
    load = BukkitPluginYaml.PluginLoadOrder.STARTUP
    authors = listOf("Merry")
    apiVersion = "1.21"
}
