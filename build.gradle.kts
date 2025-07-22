plugins {
    id("base")
    id("com.github.node-gradle.node") version "5.0.0" apply false
}

group = "com.exactpro.blockchain"
version = "1.0-SNAPSHOT"

project(":gui") {
    apply(plugin = "base")
    apply(plugin = "com.github.node-gradle.node")

    configure<com.github.gradle.node.NodeExtension> {
        version.set("24.4.1")
        npmVersion.set("10.8.1")
        download.set(true)
        workDir.set(file("${project.projectDir}/.gradle/nodejs"))
        npmWorkDir.set(file("${project.projectDir}/.gradle/npm"))
    }

    val npmInstallTask = tasks.named("npmInstall")

    val npmBuildTask = tasks.register<com.github.gradle.node.npm.task.NpmTask>("npmBuild") {
        args.set(listOf("run", "build"))
        dependsOn(npmInstallTask)
    }

    val npmStartTask = tasks.register<com.github.gradle.node.npm.task.NpmTask>("npmStart") {
        args.set(listOf("run", "preview"))
        dependsOn(npmInstallTask)
    }

    tasks.named("build") {
        dependsOn(npmBuildTask)
    }

    tasks.named("clean") {
        doLast {
            delete(
                "${project.projectDir}/dist",
                "${project.projectDir}/node_modules",
                "${project.projectDir}/.gradle"
            )
        }
    }
}

tasks.named("build") {
    dependsOn(":server:build")
    dependsOn(":gui:build")
}