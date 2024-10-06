plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)

    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt.android)
}

android {
    namespace = "net.pettip.app.navi"
    compileSdk = 34

    defaultConfig {
        applicationId = "net.pettip.app.navi"
        minSdk = 27
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
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
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.8"
    }
    packaging {
        resources {
            excludes += "/META-INF/DEPENDENCIES"
            excludes += "/META-INF/NOTICE"
            excludes += "/META-INF/LICENSE"
            excludes += "/META-INF/LICENSE.txt"
            excludes += "/META-INF/NOTICE.txt"
        }
    }
}

dependencies {

    implementation(project(":domain"))
    implementation(project(":data"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material)
    implementation(libs.play.services.location)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation(libs.androidx.core.splash)
    implementation(libs.coil.compose)
    implementation("com.google.accompanist:accompanist-systemuicontroller:0.32.0")

    // hilt
    implementation(libs.hilt.navigation)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // ----------- Camera2 implementation -----------
    implementation(libs.camera.core)
    implementation(libs.camera.camera2)
    implementation(libs.camera.lifecycle)
    implementation(libs.camera.video)
    implementation(libs.camera.view)
    implementation(libs.camera.extensions)
    // ----------- Camera2 implementation -----------

    // ----------- retrofit2 + okhttp3 -----------
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)
    // ----------- retrofit2 + okhttp3 -----------

    //카카오 SDK
    implementation("com.kakao.sdk:v2-all:2.15.0")

    //네이버 SDK
    implementation("com.navercorp.nid:oauth-jdk8:5.6.0") // 로그인
    implementation("com.naver.maps:map-sdk:3.18.0") // 맵
    implementation("io.github.fornewid:naver-map-compose:1.7.0")
    implementation("io.github.fornewid:naver-map-location:21.0.2")

    //구글 SDK
    implementation("com.google.android.gms:play-services-auth:21.2.0")
    implementation(platform("com.google.firebase:firebase-bom:33.1.1"))
    implementation("com.google.firebase:firebase-analytics-ktx")

    implementation("androidx.credentials:credentials:1.2.2")
    implementation("androidx.credentials:credentials-play-services-auth:1.2.2")
    implementation("com.google.android.libraries.identity.googleid:googleid:1.1.0")

    implementation("com.google.api-client:google-api-client:1.32.1")
    implementation("com.google.api-client:google-api-client-android:1.32.1")
}