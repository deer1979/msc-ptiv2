plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
}

android {
    namespace = "com.medlog.mscptiv2"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.medlog.mscptiv2"
        minSdk = 21
        targetSdk = 34
        versionCode = 1
        versionName = "0.1"
    }

    compileOptions { sourceCompatibility = JavaVersion.VERSION_17; targetCompatibility = JavaVersion.VERSION_17 }
    kotlinOptions { jvmTarget = "17" }
}

dependencies {
    implementation("androidx.core:core-ktx:1.10.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.1")
    implementation("com.caverock:androidsvg:1.4") // for svg rendering if needed
}
