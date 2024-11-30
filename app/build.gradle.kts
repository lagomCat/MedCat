plugins {
    alias(libs.plugins.android.application)

}

android {
    namespace = "com.bayandin.medicamentstateregister"
    compileSdk = 35


    defaultConfig {
        applicationId = "com.bayandin.medicamentstateregister"
        minSdk = 30
        targetSdk = 35
        versionCode = 6
        versionName = "2.3"

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
}

dependencies {

    implementation(libs.work.runtime.ktx)
    implementation(libs.excel.streaming.reader)
    implementation(libs.woodstox.core)
    implementation(libs.stax.api)
    implementation(libs.woodstox.core)
    implementation(libs.excel.streaming.reader)
    implementation(libs.jsoup)
    implementation(libs.room.runtime)
    annotationProcessor(libs.room.compiler)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation(libs.poi)       // Для .xls файлов
    implementation(libs.poi.ooxml) // Для .xlsx файлов
    implementation(libs.okhttp)
}