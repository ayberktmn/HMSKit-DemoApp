// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript{
    repositories {
        // Add the Maven address.
        google()
        jcenter()
        maven { url = uri("https://developer.huawei.com/repo/") }
    }
    dependencies {
        classpath ("com.android.tools.build:gradle:4.1.3")
        classpath ("com.huawei.agconnect:agcp:1.9.1.300")
        classpath ("org.jetbrains.kotlin:kotlin-gradle-plugin:1.8.0")

        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

allprojects {
    repositories {
        // Add the Maven address.
        google()
        jcenter()
        maven { url = uri("https://developer.huawei.com/repo/") }
    }
}

plugins {
    id("com.android.application") version "8.1.1" apply false
    id("org.jetbrains.kotlin.android") version "1.9.0" apply false
}