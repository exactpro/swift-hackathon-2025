plugins {
    id("base")
    id("com.github.node-gradle.node") version "7.1.0" apply false
}

group = "com.exactpro.blockchain"
version = "1.0-SNAPSHOT"

tasks.named("build") {
    dependsOn(":server:build")
    dependsOn(":gui:build")
}