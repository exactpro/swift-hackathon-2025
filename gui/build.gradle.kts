import com.github.gradle.node.npm.task.NpmTask

plugins {
    base
    id("com.github.node-gradle.node") version "7.1.0"
}

node {
    version = "24.4.1"
    npmVersion = "10.8.1"
    download = true
    workDir = file("${project.projectDir}/.gradle/nodejs")
    npmWorkDir = file("${project.projectDir}/.gradle/npm")
}

tasks {
    val npmCiTask = register<NpmTask>("npmCi") {
        args = listOf("ci")
    }

    val npmBuildTask = register<NpmTask>("npmBuild") {
        args = listOf("run", "build")
        dependsOn(npmCiTask)
    }

    val runTask = register<NpmTask>("run") {
        args = listOf("run", "preview")
        dependsOn(npmCiTask)
    }

    val zipTask = register<Zip>("zip") {
        dependsOn(npmBuildTask)

        from(project.layout.projectDirectory.dir("dist")) {
            into("/")
        }

        archiveFileName.set("ui-dist.zip")

        destinationDirectory.set(project.layout.buildDirectory.dir("distributions"))
    }

    build {
        dependsOn(npmBuildTask)
        dependsOn(zipTask)
    }

    clean {
        doLast {
            delete(
                "${project.projectDir}/dist",
                "${project.projectDir}/node_modules",
                "${project.projectDir}/.gradle"
            )
        }
    }
}
