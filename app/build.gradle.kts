plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "fr.radio"
    compileSdk = 34

    defaultConfig {
        applicationId = "fr.radio"
        minSdk = 33
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation ("androidx.fragment:fragment:1.8.5")
    implementation("com.google.code.gson:gson:2.8.9")
    implementation(files("libs/spotify-app-remote-release-0.8.0.aar"))
    implementation("com.spotify.android:auth:1.2.3")
    implementation ("androidx.work:work-runtime:2.8.1")
}
