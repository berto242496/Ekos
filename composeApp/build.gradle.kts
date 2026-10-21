import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

fun readLocalEnv(key: String): String =
    rootProject.file(".env.local").takeIf { it.isFile }?.useLines { lines ->
        lines.firstOrNull { it.startsWith("$key=") }?.substringAfter('=')?.trim()
            ?.trim('"', '\'')
            .orEmpty()
    }.orEmpty()

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "17"
            }
        }
    }
    
    // Only configure iOS targets if running on macOS with Xcode available
    val isMac = System.getProperty("os.name").contains("Mac", ignoreCase = true)
    if (isMac) {
        listOf(
            iosX64(),
            iosArm64(),
            iosSimulatorArm64()
        ).forEach { iosTarget ->
            iosTarget.binaries.framework {
                baseName = "ComposeApp"
                isStatic = true
            }
        }
    }
    
    jvm("desktop")
    
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.ui)
                implementation(compose.components.resources)
                implementation(compose.components.uiToolingPreview)
                implementation(compose.materialIconsExtended)
                
                // Coroutines & Lifecycle
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.lifecycle.viewmodel.compose)
                implementation(libs.navigation.compose)
            }
        }
        
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(libs.kotlinx.coroutines.test)
            }
        }

        val androidMain by getting {
            dependencies {
                implementation(libs.androidx.activity.compose)
                implementation("androidx.appcompat:appcompat:1.7.0")
                implementation("androidx.core:core-ktx:1.13.1")
                implementation(libs.androidx.credentials)
                implementation(libs.androidx.credentials.play.services.auth)
                implementation(libs.googleid)
            }
        }
        
        val desktopMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
            }
        }
    }
}

android {
    namespace = "com.ecosdolugar"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.ecosdolugar"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"
        resValue(
            "string",
            "google_web_client_id",
            providers.provider { readLocalEnv("GOOGLE_WEB_CLIENT_ID") }
                .filter { it.isNotBlank() }
                .orElse(providers.gradleProperty("GOOGLE_WEB_CLIENT_ID"))
                .orElse(providers.environmentVariable("GOOGLE_WEB_CLIENT_ID"))
                .get()
        )
        val supabaseUrl = providers.gradleProperty("SUPABASE_URL")
            .orElse(providers.environmentVariable("SUPABASE_URL"))
            .orElse(readLocalEnv("VITE_SUPABASE_URL"))
            .get()
        val supabaseKey = providers.gradleProperty("SUPABASE_PUBLISHABLE_KEY")
            .orElse(providers.environmentVariable("SUPABASE_PUBLISHABLE_KEY"))
            .orElse(readLocalEnv("VITE_SUPABASE_ANON_KEY"))
            .get()
        resValue("string", "supabase_url", supabaseUrl)
        resValue("string", "supabase_publishable_key", supabaseKey)
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
