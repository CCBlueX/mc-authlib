plugins {
    kotlin("jvm") version "1.6.0"
    java
}

group = "me.liuli.elixir"
version = "1.0"

repositories {
    mavenCentral()
    maven("https://papermc.io/repo/repository/maven-releases/")
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("com.mojang:authlib:1.5.25")
    implementation("com.beust:klaxon:5.5")
}