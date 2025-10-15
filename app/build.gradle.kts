plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin") version "2.0.1"
    id("kotlin-kapt")

}

android {
    namespace = "com.example.fruitapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.fruitapp"
        minSdk = 24
        targetSdk = 35
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

    implementation("androidx.navigation:navigation-compose:2.7.3")//將導引匯入

    implementation ("com.google.android.gms:play-services-maps:18.1.0")
    implementation("com.google.maps.android:maps-compose:2.11.4")//加入map

    implementation("com.google.android.libraries.places:places:3.3.0")//加入map中place
    implementation("com.google.accompanist:accompanist-permissions:0.32.0")//map中目前位置

    implementation("com.squareup.okhttp3:okhttp:4.10.0")//http請求程式庫
    implementation("com.google.android.gms:play-services-location:21.0.1")//取得使用者的GPS定位

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")

    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation(libs.androidx.exifinterface)//加入 text search



    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.activity:activity-ktx:1.8.2")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    // Retrofit with Scalar Converter
    implementation("com.squareup.retrofit2:converter-scalars:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.5.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")
    implementation("com.squareup.okhttp3:okhttp:4.11.0")

    implementation("androidx.camera:camera-camera2:1.3.0")
    implementation("androidx.camera:camera-lifecycle:1.3.0")
    implementation("androidx.camera:camera-view:1.3.0")

    // Jetpack Compose 支援 CameraView
    implementation("androidx.camera:camera-view:1.3.0")
    implementation("io.coil-kt:coil-compose:2.4.0")

    // Room 核心
    implementation("androidx.room:room-runtime:2.5.1")
    // Kotlin 擴充協程支援
    implementation ("androidx.room:room-ktx:2.5.1")
    // Annotation Processor
    kapt ("androidx.room:room-compiler:2.5.1")

    implementation("androidx.datastore:datastore-preferences:1.1.1")

    // 協程（若專案尚未加入）
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")
}