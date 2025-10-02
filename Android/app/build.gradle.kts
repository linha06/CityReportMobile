plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.linha.myreportcity"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.linha.myreportcity"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        viewBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    // view model
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    // paging
    implementation(libs.androidx.paging.runtime)
    // optional - Jetpack Compose integration
    implementation(libs.androidx.paging.compose)
    // paging room
    implementation(libs.androidx.room.paging)
    // live data
    implementation(libs.androidx.runtime.livedata)
    // navigation
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.navigation.runtime.ktx)
    // coil foto
    implementation(libs.coil.compose)
    // permission
    implementation(libs.accompanist.permissions)
    // pulltoRefresh
    implementation(libs.material3)
    implementation(libs.androidx.foundation)
    implementation(libs.androidx.material.icons.core)
    // google font
    implementation(libs.androidx.ui.text.google.fonts)
    // animasi
    implementation(libs.androidx.animation)
    // retrofit
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    // okhttp logging interceptor, for showing request&response on logcat
    implementation("com.squareup.okhttp3:logging-interceptor:4.10.0")
    // untuk chart
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
    // viewbinding
    implementation("androidx.compose.ui:ui-viewbinding")
}