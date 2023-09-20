plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.huawei.agconnect")
}

android {
    namespace = "com.example.accountkithms"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.accountkithms"
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            storeFile = file ("accountkey.jks")
            keyAlias = "keyAccount"
            keyPassword = "123456"
            storePassword = "123456"
            enableV1Signing = true
            enableV2Signing = true
        }
    }


    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
                    isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            packagingOptions{
                doNotStrip ("*/arm64-v8a/libucs-credential.so")
                doNotStrip ("*/armeabi-v7a/libucs-credential.so")
                // If the CPU architecture is x86, add the following configuration:
                doNotStrip ("*/x86/libucs-credential.so")
                doNotStrip ("*/x86_64/libucs-credential.so")
                exclude ("lib/x86/libucs-credential.so")
                // Exclude the SO file on which the x86_64 platform depends.
                exclude ("lib/x86_64/libucs-credential.so")
            }
        }
        debug {
            signingConfig = signingConfigs.getByName("release")
                        true
                        packagingOptions{
                            doNotStrip ("*/arm64-v8a/libucs-credential.so")
                            doNotStrip ("*/armeabi-v7a/libucs-credential.so")
                            // If the CPU architecture is x86, add the following configuration:
                            doNotStrip ("*/x86/libucs-credential.so")
                            doNotStrip ("*/x86_64/libucs-credential.so")
                        }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    viewBinding{
        enable = true
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    implementation ("com.huawei.agconnect:agconnect-core:1.9.1.300")
    implementation ("com.huawei.hms:hwid:6.7.0.300")
    implementation ("com.huawei.hms:ads-lite:13.4.66.300")
    implementation ("com.huawei.hms:location:6.12.0.300")
}