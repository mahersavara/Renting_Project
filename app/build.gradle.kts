plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
//    alias(libs.google.services) // Add this line
    id ("com.google.gms.google-services")
}

android {
    namespace = "eriksu.commercial.rentingproject"
    compileSdk = 34

    defaultConfig {
        applicationId = "eriksu.commercial.rentingproject"
        minSdk = 24
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
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        buildConfig = true
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    kotlin {
        jvmToolchain(17)
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
    implementation(libs.firebase.storage.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)


    // Firebase


    // room

    implementation(libs.hilt)
    implementation(libs.navigation.hilt)
    implementation(libs.navigation) // needed for Room
    implementation(libs.room.ktx)
    implementation(libs.timber)


    //Retrofit ?  cần get data gì đâu nhỉ,


    // Koin uhuh, thử koin phát xem xD


    // Coil -> Load ảnh
    implementation(libs.coil.compose.v210)


    // Navigation

    // datastore
    implementation(libs.androidx.datastore.preferences)


    // Icon


    // dung ksp đi ( kotlin 1.9, project 1.7 không dùng được nhé)

    ksp(libs.hilt.compiler)
    ksp(libs.room.compiler)

    // Pager
    implementation (libs.accompanist.pager)
//Firebase

    implementation (libs.firebase.auth)
    implementation (libs.firebase.firestore)


    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)

    implementation (libs.firebase.firestore.ktx)

    // datetime dialog =))
    implementation (libs.compose.material.dialogs.core)
    implementation (libs.datetime)
    //  implementation "io.github.vanpra.compose-material-dialogs:color:${version}"
    // phonenumber lib
    implementation (libs.libphonenumber)


}



ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
}
