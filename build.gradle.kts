plugins {
    kotlin("jvm") version "1.3.72" // Needed because that's what gradle uses?
    `java-gradle-plugin`
    `maven-publish`
}

group = "il.ac.technion.cs"
version = "1.0-SNAPSHOT"

repositories {
    maven("https://dl.bintray.com/kotlin/kotlin-eap")
    mavenCentral()
    //jcenter()
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven("https://soot-build.cs.upb.de/nexus/repository/soot-release/")
    maven("https://jitpack.io") {
        metadataSources {
            gradleMetadata()
            mavenPom()
        }
    }
}

gradlePlugin {
    plugins {
        create("reactivizeGradlePlugin") {
            id = "il.ac.technion.cs.reactivize-gradle-plugin"
            implementationClass = "il.ac.technion.cs.reactivize.gradle.ReactivizeGradlePlugin"
        }
    }
}

configurations.all {
    resolutionStrategy.cacheChangingModulesFor(1, TimeUnit.MINUTES)
}

dependencies {
    // implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))
    implementation("com.github.chaosite", "reactivize-core", "master-SNAPSHOT").apply {
        isChanging = true
    }
    implementation("org.soot-oss", "soot", "4.2.1")

    testImplementation("org.junit.jupiter", "junit-jupiter-api", "5.7.0")
    testRuntimeOnly("org.junit.jupiter", "junit-jupiter-engine", "5.7.0")

    testImplementation("org.jetbrains.kotlin", "kotlin-gradle-plugin", "1.3.72")
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}