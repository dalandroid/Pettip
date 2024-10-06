plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)

    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt.android)
}

android {
    namespace = "net.pettip.app.data"
    compileSdk = 34

    defaultConfig {
        minSdk = 27

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation(project(":domain"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // ----------- retrofit2 + okhttp3 -----------
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)
    // ----------- retrofit2 + okhttp3 -----------


    // hilt
    implementation(libs.hilt.navigation)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    //구글 SDK
    implementation("androidx.credentials:credentials:1.2.2")
    implementation("androidx.credentials:credentials-play-services-auth:1.2.2")
    implementation("com.google.android.libraries.identity.googleid:googleid:1.1.0")

    implementation("com.google.api-client:google-api-client:1.32.1")
    implementation("com.google.api-client:google-api-client-android:1.32.1")

    //카카오 SDK
    implementation("com.kakao.sdk:v2-all:2.15.0")

    //네이버 SDK
    implementation("com.navercorp.nid:oauth-jdk8:5.6.0")
}