buildscript{
    repositories{
        google()
        mavenCentral()
    }
    dependencies {
        classpath(libs.androidx.androidx.navigation.safeargs.gradle.plugin)
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
}