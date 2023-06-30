import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "pl.kurczyna"
version = "1.0-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

val spockVersion: String by project
val groovyVersion: String by project
val jacksonVersion: String by project

plugins {
    id("org.springframework.boot") version "3.1.1"
    id("io.spring.dependency-management") version "1.1.0"
    id("org.jlleitschuh.gradle.ktlint") version "11.4.2"
    id("com.adarshr.test-logger") version "3.2.0"
    kotlin("jvm") version "1.8.22"
    kotlin("plugin.spring") version "1.8.22"
    idea
    groovy
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

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")
    implementation("org.springframework.kafka:spring-kafka")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.spockframework:spock-core:$spockVersion")
    testImplementation("org.spockframework:spock-spring:$spockVersion")
    testImplementation("org.apache.groovy:groovy-all:$groovyVersion")
    itestImplementation("org.springframework.boot:spring-boot-starter-test")
    itestImplementation("org.spockframework:spock-core:$spockVersion")
    itestImplementation("org.spockframework:spock-spring:$spockVersion")
    itestImplementation("org.apache.groovy:groovy-all:$groovyVersion")
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
}
