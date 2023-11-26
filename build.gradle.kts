import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "pl.kurczyna"
version = "1.0-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

val spockVersion: String by project
val groovyVersion: String by project
val jacksonVersion: String by project
val testcontainersVersion: String by project
val postgresqlVersion: String by project
val liquibaseVersion: String by project
val wiremockVersion: String by project
val springKafkaVersion: String by project
val springCloudGcpVersion: String by project
val springCloudVersion: String by project
val jakartaVersion: String by project
val greenmailVersion: String by project
val awsCloudVersion: String by project
val localstackVersion: String by project

plugins {
    id("org.springframework.boot") version "3.1.1" // Spring Boot
    id("io.spring.dependency-management") version "1.1.0" // Spring Dependency Management
    id("org.jlleitschuh.gradle.ktlint") version "11.4.2" // Kotlin linter https://pinterest.github.io/ktlint/1.0.0/
    id("com.adarshr.test-logger") version "3.2.0" // Plugin for printing beautiful logs on the console while running tests. https://github.com/radarsh/gradle-test-logger-plugin
    kotlin("jvm") version "1.8.22" // Kotlin plugin
    kotlin("plugin.spring") version "1.8.22" // Kotlin Sprign support
    idea // Intelij Idea plugin
    groovy // Groovy plugin (needed for Spock)
}

repositories {
    mavenCentral()
    maven { url = uri("https://repo.spring.io/milestone") }
    maven { url = uri("https://packages.confluent.io/maven") }
}

sourceSets {
    create("itest") {
        compileClasspath += sourceSets.main.get().output
        compileClasspath += sourceSets.test.get().output
        runtimeClasspath += sourceSets.main.get().output
        runtimeClasspath += sourceSets.test.get().output
    }
}

idea.module {
    testSourceDirs = testSourceDirs + project.sourceSets["itest"].allJava.srcDirs + project.sourceSets["test"].allJava.srcDirs
}

configurations["itestRuntimeOnly"].extendsFrom(configurations.runtimeOnly.get())

val itestImplementation: Configuration by configurations.getting {
    extendsFrom(configurations.implementation.get())
}

dependencyManagement {
    imports {
        mavenBom("com.google.cloud:spring-cloud-gcp-dependencies:$springCloudGcpVersion")
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:$springCloudVersion")
    }
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-mail")
    implementation(platform("io.awspring.cloud:spring-cloud-aws-dependencies:$awsCloudVersion"))
    implementation("io.awspring.cloud:spring-cloud-aws-starter-sqs")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")
    implementation("org.postgresql:postgresql:$postgresqlVersion")
    implementation("org.liquibase:liquibase-core:$liquibaseVersion")
    implementation("org.springframework.kafka:spring-kafka:$springKafkaVersion")
    implementation("com.google.cloud:spring-cloud-gcp-starter-storage")
    implementation("com.sun.mail:jakarta.mail:$jakartaVersion")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.spockframework:spock-core:$spockVersion")
    testImplementation("org.spockframework:spock-spring:$spockVersion")
    testImplementation("org.apache.groovy:groovy-all:$groovyVersion")
    itestImplementation("org.springframework.boot:spring-boot-starter-test")
    itestImplementation("org.spockframework:spock-core:$spockVersion")
    itestImplementation("org.spockframework:spock-spring:$spockVersion")
    itestImplementation("org.apache.groovy:groovy-all:$groovyVersion")
    itestImplementation("org.testcontainers:postgresql:$testcontainersVersion")
    itestImplementation("org.testcontainers:kafka:$testcontainersVersion")
    itestImplementation("com.github.tomakehurst:wiremock-jre8-standalone:$wiremockVersion")
    itestImplementation("org.testcontainers:testcontainers:$testcontainersVersion")
    itestImplementation("com.icegreen:greenmail:$greenmailVersion")
    itestImplementation("com.icegreen:greenmail-junit5:$greenmailVersion")
    itestImplementation("org.testcontainers:localstack:$localstackVersion")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

// ITests
val itest = task<Test>("itest") {
    description = "Runs integration tests."
    group = "verification"

    testClassesDirs = sourceSets["itest"].output.classesDirs
    classpath = sourceSets["itest"].runtimeClasspath

    shouldRunAfter("test")
}

tasks.build {
    dependsOn(tasks.check)
}

tasks.check {
    dependsOn(tasks.ktlintCheck)
    dependsOn(tasks.test)
    dependsOn(itest)
}

configure<org.jlleitschuh.gradle.ktlint.KtlintExtension> {
    outputToConsole.set(true)
    disabledRules.set(setOf("filename"))
}
