import io.papermc.paperweight.userdev.ReobfArtifactConfiguration
import xyz.jpenilla.resourcefactory.bukkit.BukkitPluginYaml

plugins {
    id("java-library")
    id("io.papermc.paperweight.userdev") version "1.7.2"
    id("xyz.jpenilla.run-paper") version "2.3.0"
    id("xyz.jpenilla.resource-factory-bukkit-convention") version "1.1.1"
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
    paperweight.paperDevBundle("1.21-R0.1-SNAPSHOT")
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
}

bukkitPluginYaml {
    main = "$group.${rootProject.name}.CelestialPlugin"
    load = BukkitPluginYaml.PluginLoadOrder.STARTUP
    authors = listOf("Merry")
    apiVersion = "1.21"
}
