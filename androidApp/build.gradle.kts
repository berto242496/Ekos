import java.util.Properties

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeCompiler)
}

val keystoreProps = Properties().apply {
    rootProject.file("keystore.properties").takeIf { it.isFile }?.inputStream()?.use(::load)
}

fun readLocalEnv(key: String): String =
    rootProject.file(".env.local").takeIf { it.isFile }?.useLines { lines ->
        lines.firstOrNull { it.startsWith("$key=") }?.substringAfter('=')?.trim()
            ?.trim('"', '\'')
            .orEmpty()
    }.orEmpty()

android {
    namespace = "com.ecosdolugar"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.ecosdolugar"
        minSdk = 24
        targetSdk = 36
        versionCode = 3
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

    buildFeatures {
        resValues = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    signingConfigs {
        create("release") {
            fun signingValue(key: String, envName: String): String? =
                keystoreProps.getProperty(key)?.takeIf { it.isNotBlank() }
                    ?: providers.environmentVariable(envName).orNull?.takeIf { it.isNotBlank() }

            val storePath = signingValue("storeFile", "ECOS_KEYSTORE_FILE")
            if (storePath != null) {
                storeFile = rootProject.file(storePath)
                storePassword = signingValue("storePassword", "ECOS_KEYSTORE_PASSWORD")
                keyAlias = signingValue("keyAlias", "ECOS_KEY_ALIAS")
                keyPassword = signingValue("keyPassword", "ECOS_KEY_PASSWORD")
            }
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("release").takeIf { it.storeFile != null }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    lint {
        checkDependencies = true
    }
}

dependencies {
    implementation(project(":composeApp"))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.lifecycle.viewmodel.compose)
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)
    implementation(libs.billing)
}
