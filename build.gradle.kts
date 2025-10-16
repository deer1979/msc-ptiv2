plugins {
    kotlin("jvm") version "1.8.22" apply false
    id("com.android.application") version "8.0.2" apply false
}

buildscript {
    repositories {
        google()
        mavenCentral()
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}
