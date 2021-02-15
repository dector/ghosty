import com.android.build.api.dsl.DefaultConfig

plugins {
    id("com.android.application")
    kotlin("android")
}

fun DefaultConfig.applyDeviceConfig() {
    val file = File("device.local.properties")
    if (!file.exists()) {
        logger.error("Create 'device.local.properties' with your device configuration")
        return
    }

    val props = file.readLines()

    buildConfigField("String", "DEVICE_IP", "\"${props[0]}\"")
    buildConfigField("String", "DEVICE_ID", "\"${props[1]}\"")
    buildConfigField("String", "DEVICE_LOCAL_KEY", "\"${props[2]}\"")
}

android {
    compileSdkVersion(30)

    defaultConfig {
        applicationId = "space.dector.ghosty"
        minSdkVersion(24)
        targetSdkVersion(30)
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        applyDeviceConfig()
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
        useIR = true
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = Version.compose
    }
}

dependencies {
    implementation("space.dector.tuyalib:library:0.1.0")

    implementation("androidx.core:core-ktx:1.3.2")
    implementation("androidx.appcompat:appcompat:1.2.0")

    implementation("com.google.android.material:material:1.2.1")

    implementation("androidx.compose.ui:ui:${Version.compose}")
    implementation("androidx.compose.material:material:${Version.compose}")
    implementation("androidx.compose.ui:ui-tooling:${Version.compose}")
    implementation("androidx.activity:activity-compose:1.3.0-alpha02")

    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.3.0-alpha06")

    testImplementation("junit:junit:4.+")

    androidTestImplementation("androidx.test.ext:junit:1.1.2")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.3.0")
}
