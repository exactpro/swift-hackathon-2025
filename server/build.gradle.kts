plugins {
    application
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
    implementation(platform("org.springframework:spring-framework-bom:6.2.3"))
    implementation("com.fasterxml.jackson.core:jackson-databind:2.19.2")
    implementation("io.projectreactor.netty:reactor-netty:1.2.6")
    implementation("org.apache.logging.log4j:log4j-api:2.24.3")
    implementation("org.springframework:spring-context")
    implementation("org.springframework:spring-webflux")

    runtimeOnly("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.19.2")
    runtimeOnly("org.apache.logging.log4j:log4j-core:2.24.3")

    testImplementation(platform("org.junit:junit-bom:5.12.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks {
    test {
        useJUnitPlatform()
    }

    getByName<JavaExec>("run") {
        jvmArgs("-Djava.net.preferIPv4Stack=true")
    }
}