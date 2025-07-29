plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false // Add Compose plugin
    id("com.google.devtools.ksp") version "2.2.0-2.0.2" apply false
}