plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.googleservicesandfirebase"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.googleservicesandfirebase"
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
        buildFeatures {
            viewBinding = true
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
    buildFeatures {
        viewBinding = true
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

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    //Firebase
//    implementation (platform("com.google.firebase:firebase-bom:32.3.1"))
//    implementation ("com.google.firebase:firebase-analytics-ktx")
//    implementation ("com.google.firebase:firebase-auth-ktx")
//  implementation("com.google.android.gms:play-services-auth:21.0.0")
//    implementation("com.google.firebase:firebase-database-ktx:20.3.1")

    //Picasso
    implementation ("com.squareup.picasso:picasso:2.8")




    //mvvm
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")


    //maps
    implementation ("com.google.android.gms:play-services-maps:18.2.0")
    //maps-utils
    implementation ("com.google.maps.android:android-maps-utils:3.5.3")
    //location
    implementation ("com.google.android.gms:play-services-location:18.0.0")

    //retrofit
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.okhttp3:okhttp:4.11.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")

    //material
    implementation("com.google.android.material:material:1.3.0-alpha01")



    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}