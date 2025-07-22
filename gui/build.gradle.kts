import com.github.gradle.node.NodeExtension
import com.github.gradle.node.npm.task.NpmTask

plugins {
    id("base")
    id("com.github.node-gradle.node")
}

configure<NodeExtension> {
    version.set("24.4.1")
    npmVersion.set("10.8.1")
    download.set(true)
    workDir.set(file("${project.projectDir}/.gradle/nodejs"))
    npmWorkDir.set(file("${project.projectDir}/.gradle/npm"))
}

tasks {
    val npmInstallTask = named("npmInstall")

    val npmBuildTask = register<NpmTask>("npmBuild") {
        args.set(listOf("run", "build"))
        dependsOn(npmInstallTask)
    }

    val npmStartTask = register<NpmTask>("npmStart") {
        args.set(listOf("run", "preview"))
        dependsOn(npmInstallTask)
    }

    named("build") {
        dependsOn(npmBuildTask)
    }

    named("clean") {
        doLast {
            delete(
                "${project.projectDir}/dist",
                "${project.projectDir}/node_modules",
                "${project.projectDir}/.gradle"
            )
        }
    }
}
