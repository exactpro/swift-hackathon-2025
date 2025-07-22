plugins {
    base
}

group = "com.exactpro.blockchain"
version = "1.0-SNAPSHOT"

tasks.build {
    dependsOn(":server:build")
    dependsOn(":gui:build")
}