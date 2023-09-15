pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        maven { url = uri("https://developer.huawei.com/repo/") }
    }
}
dependencyResolutionManagement {

    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://developer.huawei.com/repo/") }
    }
}


rootProject.name = "AccountKitHMS"
include(":app")
 