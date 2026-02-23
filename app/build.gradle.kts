plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "mediaplayer.patryk.lms"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "mediaplayer.patryk.lms"
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
        viewBinding = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    debugImplementation("com.facebook.flipper:flipper:0.201.0")
    debugImplementation("com.facebook.soloader:soloader:0.10.5")

    releaseImplementation("com.facebook.flipper:flipper-noop:0.201.0")

    val room_version = "2.8.4"

    implementation("androidx.room:room-runtime:$room_version")
    implementation("com.google.android.gms:play-services-auth:20.7.0")
}