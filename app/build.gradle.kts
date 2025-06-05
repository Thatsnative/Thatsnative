import java.text.SimpleDateFormat
import java.util.Date

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    kotlin("plugin.serialization") version "1.9.22"
}

android {
    namespace = "com.example.epic"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.epic"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        // resources
        buildConfigField("String", "BASE_URL", "\"http://209.145.57.232:8002/\"")
        buildConfigField("Long", "BUILD_TIME", "${System.currentTimeMillis()}L")
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
        viewBinding = true
        buildConfig = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    applicationVariants.all {
        outputs.all {
            val date = SimpleDateFormat("yyyy-MM-dd_HH-mm").format(Date())
            val buildType = buildType.name
            val flavorName = if (flavorName.isNotEmpty()) "-$flavorName" else ""
            val versionName = versionName

            (this as com.android.build.gradle.internal.api.BaseVariantOutputImpl).outputFileName =
                "app${flavorName}-${buildType}-${versionName}-${date}.apk"
        }
    }
}

dependencies {
    implementation(libs.okhttp)
    implementation(libs.okhttp.dnsoverhttps)
    implementation(libs.androidx.localbroadcastmanager)
    implementation(libs.guava)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.paging)
    implementation(libs.androidx.paging.runtime)
    implementation(libs.androidx.work.runtime)
    annotationProcessor(libs.androidx.room.compiler)
    implementation(libs.timber)
    implementation(libs.dnsjava)
    implementation(libs.pcap4j.core)
    implementation(libs.pcap4j.pcap4j.packetfactory.static)


    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.material)
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.viewbindingpropertydelegate)
    implementation(libs.io.insert.koin.koin.android)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.constraint.layout)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.androidx.viewpager2)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.logging.interceptor)
    implementation(libs.okhttpprofiler)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.viewpager2)
    implementation(libs.lottie)
    implementation(libs.material.v1120)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

}