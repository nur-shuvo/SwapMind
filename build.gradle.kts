plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.dagger.hilt) apply false
}

buildscript {
    dependencies {
        classpath(libs.secrets.gradle.plugin)
        classpath("com.google.gms:google-services:4.4.0")
    }
}