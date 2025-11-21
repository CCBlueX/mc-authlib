import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.api.tasks.testing.Test
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.gradle.authentication.http.BasicAuthentication
import org.gradle.jvm.tasks.Jar
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.jvm.JvmTargetValidationMode
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

plugins {
    kotlin("jvm") version "2.2.21"
    `java-library`
    id("org.jetbrains.dokka") version "2.1.0"
    id("org.jetbrains.dokka-javadoc") version "2.1.0"
    `maven-publish`
}

group = "net.ccbluex"
version = "1.5.0"

val projectDisplayName = "MC AuthLib"
val projectDescription = "MC AuthLib is a library designed to make the integration of different Minecraft account types easier."
val licenseName = "GNU General Public License v3.0"
val licenseUrl = "https://www.gnu.org/licenses/gpl-3.0.en.html"
val authorName = "ccbluex"
val projectUrl = "https://github.com/CCBlueX/mc-authlib"

repositories {
    mavenCentral()
    mavenLocal()
    maven {
        name = "Fabric"
        url = uri("https://maven.fabricmc.net/")
    }
    maven {
        name = "minecraft"
        url = uri("https://libraries.minecraft.net/")
        content {
            includeGroup("com.mojang")
        }
    }
}

dependencies {
    dokkaPlugin("org.jetbrains.dokka:kotlin-as-java-plugin:2.1.0")

    implementation("com.google.code.gson:gson:2.11.0")
    implementation("org.apache.logging.log4j:log4j-core:2.24.1")
    implementation("org.apache.logging.log4j:log4j-api:2.24.1")
    implementation("org.apache.logging.log4j:log4j-slf4j-impl:2.24.1")
    implementation("org.slf4j:slf4j-api:2.0.16")
    implementation("com.mojang:authlib:6.0.58")
    implementation("com.thealtening.api:api:4.1.0") {
        exclude(group = "com.google.code.gson", module = "gson")
    }

    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(21)
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
    withSourcesJar()
    withJavadocJar()
}

sourceSets {
    named("main") {
        java.srcDir("src/main/java")
        kotlin.srcDir("src/main/kotlin")
    }
    named("test") {
        java.srcDir("src/test/java")
        kotlin.srcDir("src/test/kotlin")
    }
}

dokka {
    moduleName.set(projectDisplayName)
    
    dokkaPublications.html {
        suppressInheritedMembers.set(true)
    }
    
    dokkaSourceSets.main {
        sourceLink {
            localDirectory.set(file("src/main/kotlin"))
            remoteUrl("$projectUrl/tree/main/src/main/kotlin")
            remoteLineSuffix.set("#L")
        }
    }
}

tasks.withType<Jar>().configureEach {
    manifest {
        attributes(
            "Implementation-Title" to projectDisplayName,
            "Implementation-Version" to project.version,
            "Implementation-Vendor" to authorName,
            "License" to licenseName,
            "License-Url" to licenseUrl
        )
    }

    from("LICENSE") {
        into("META-INF/")
    }
}

tasks.named<Jar>("javadocJar") {
    dependsOn(tasks.named("dokkaGeneratePublicationJavadoc"))
    from(tasks.named("dokkaGeneratePublicationJavadoc"))
    archiveClassifier.set("javadoc")
}

tasks.withType<JavaCompile>().configureEach {
    options.release.set(21)
}

tasks.withType<KotlinJvmCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_21)
        jvmTargetValidationMode.set(JvmTargetValidationMode.WARNING)
    }
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
    testLogging {
        events = setOf(TestLogEvent.PASSED, TestLogEvent.SKIPPED, TestLogEvent.FAILED)
    }
}

publishing {
    publications {
        create<MavenPublication>("mcAuthlib") {
            from(components["java"])
            artifactId = project.name

            pom {
                name.set(projectDisplayName)
                description.set(projectDescription)
                url.set(projectUrl)

                licenses {
                    license {
                        name.set(licenseName)
                        url.set(licenseUrl)
                    }
                }

                developers {
                    developer {
                        id.set("ccbluex")
                        name.set(authorName)
                    }
                }

                scm {
                    connection.set("scm:git:git://github.com/CCBlueX/mc-authlib.git")
                    developerConnection.set("scm:git:ssh://github.com:CCBlueX/mc-authlib.git")
                    url.set(projectUrl)
                }
            }
        }
    }

    repositories {
        maven {
            name = "ccbluex-maven"
            url = uri("https://maven.ccbluex.net/releases")

            val mavenUsernameProvider = providers.environmentVariable("MAVEN_TOKEN_NAME")
                .orElse(providers.gradleProperty("mavenUsername"))
                .orElse("ccbluex")
            val mavenPasswordProvider = providers.environmentVariable("MAVEN_TOKEN_SECRET")
                .orElse(providers.gradleProperty("mavenPassword"))

            credentials {
                username = mavenUsernameProvider.orNull ?: "ccbluex"
                password = mavenPasswordProvider.orNull
            }

            authentication {
                create<BasicAuthentication>("basic")
            }
        }
    }
}
