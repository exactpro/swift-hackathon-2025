plugins {
    application
    id("com.gradleup.shadow") version "8.3.8"
}

repositories {
    mavenCentral()
}

java {
    // Spring 6.0+ needs JVM runtime version 17 or newer.
    targetCompatibility = JavaVersion.VERSION_17
    sourceCompatibility = targetCompatibility
}

dependencies {
    implementation(platform("io.projectreactor:reactor-bom:2024.0.8"))
    implementation(platform("org.apache.logging.log4j:log4j-bom:2.25.1"))
    implementation(platform("org.springframework.data:spring-data-bom:2024.0.8"))
    implementation(platform("org.springframework:spring-framework-bom:6.2.9"))
    implementation("com.fasterxml.jackson.core:jackson-databind:2.19.2")
    implementation("jakarta.xml.bind:jakarta.xml.bind-api:4.0.2")
    implementation("io.projectreactor.kafka:reactor-kafka")
    implementation("io.projectreactor.netty:reactor-netty")
    implementation("org.apache.logging.log4j:log4j-api")
    implementation("org.apache.logging.log4j:log4j-core")
    implementation("org.flywaydb:flyway-core:11.10.4")
    implementation("org.springframework.data:spring-data-r2dbc")
    implementation("org.springframework:spring-context")
    implementation("org.springframework:spring-webflux")

    runtimeOnly("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.19.2")
    runtimeOnly("org.apache.logging.log4j:log4j-slf4j2-impl")
    runtimeOnly("org.flywaydb:flyway-database-postgresql:11.10.3")
    runtimeOnly("org.glassfish.jaxb:jaxb-runtime:4.0.5")
    runtimeOnly("org.postgresql:r2dbc-postgresql:1.0.7.RELEASE")
    runtimeOnly("org.postgresql:postgresql:42.7.7")

    testImplementation(platform("org.junit:junit-bom:5.12.0"))
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testImplementation("org.junit.platform:junit-platform-launcher")
    testImplementation("org.junit.platform:junit-platform-engine")
    testImplementation("org.springframework:spring-test")

    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

application {
    mainClass.set("com.exactpro.blockchain.Application")
}

tasks {
    test {
        useJUnitPlatform() // Looks like Gradle incorrectly determines platform version (because of bom?) :-(
    }

    getByName<JavaExec>("run") {
        jvmArgs("-Djava.net.preferIPv4Stack=true")
    }

    shadowJar {
        mergeServiceFiles()
    }
}