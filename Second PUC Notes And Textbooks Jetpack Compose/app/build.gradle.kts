plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose) // Add Compose plugin
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.puc.pyp"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.puc.pyp"
        minSdk = 24
        targetSdk = 36
        versionCode = 37
        versionName = "3.7"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    buildFeatures {
        dataBinding = true // Keep for XML-based activity
        compose = true // Enable Compose
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "2.2.0" // Match Kotlin version
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11)
        }
    }
    androidResources {
        localeFilters.clear()
        localeFilters += mutableSetOf("en", "kn", "hi", "te", "ta", "mr", "ml", "ur", "ar", "fr", "tcy")
    }
    bundle {
        language {
            enableSplit = false
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat) // Keep for XML activity
    implementation(libs.material) // Keep for XML activity
    implementation(libs.androidx.activity) // Keep for XML activity
    implementation(libs.androidx.constraintlayout) // Keep for XML activity
    implementation(libs.android.pdf.viewer)
    implementation(libs.glide)
    implementation(libs.okhttp)
    ksp(libs.glide.ksp)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.work.runtime.ktx)

    // Compose dependencies
    implementation(libs.androidx.activity.compose) // For Compose activities
    implementation(platform(libs.androidx.compose.bom)) // Use BOM for consistent versions
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview) // For previewing composables
    implementation(libs.androidx.material3) // Material3 for Compose
    debugImplementation(libs.androidx.ui.tooling) // For debugging Compose layouts
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.material3)

    implementation(libs.accompanist.pager)
    implementation(libs.glide.compose)
    
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}