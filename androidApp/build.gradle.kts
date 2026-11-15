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

fun supabaseConfigValue(gradleKey: String, envKey: String, localKey: String): String =
    providers.gradleProperty(gradleKey)
        .orElse(providers.environmentVariable(envKey))
        .orElse(readLocalEnv(localKey))
        .get()

gradle.taskGraph.whenReady {
    val releaseRequested = allTasks.any { it.project == project && it.name.contains("Release") }
    if (releaseRequested) {
        val missing = listOf(
            Triple("SUPABASE_URL", "SUPABASE_URL", "VITE_SUPABASE_URL"),
            Triple("SUPABASE_PUBLISHABLE_KEY", "SUPABASE_PUBLISHABLE_KEY", "VITE_SUPABASE_ANON_KEY")
        ).filter { (gradleKey, envKey, localKey) ->
            supabaseConfigValue(gradleKey, envKey, localKey).isBlank()
        }.map { it.first }
        check(missing.isEmpty()) {
            "Faltan ${missing.joinToString()} para el build de release. Defínelos en .env.local, en gradle.properties o como variables de entorno."
        }
    }
}

android {
    namespace = "com.ecosdolugar"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.ecosdolugar"
        minSdk = 24
        targetSdk = 36
        versionCode = 4
        versionName = "1.0.0"
        resourceConfigurations += listOf("es", "gl", "en")
        resValue(
            "string",
            "google_web_client_id",
            providers.provider { readLocalEnv("GOOGLE_WEB_CLIENT_ID") }
                .filter { it.isNotBlank() }
                .orElse(providers.gradleProperty("GOOGLE_WEB_CLIENT_ID"))
                .orElse(providers.environmentVariable("GOOGLE_WEB_CLIENT_ID"))
                .orElse("")
                .get()
        )
        if (readLocalEnv("GOOGLE_WEB_CLIENT_ID").isBlank() &&
            providers.gradleProperty("GOOGLE_WEB_CLIENT_ID").orNull.isNullOrBlank() &&
            providers.environmentVariable("GOOGLE_WEB_CLIENT_ID").orNull.isNullOrBlank()
        ) {
            logger.warn("GOOGLE_WEB_CLIENT_ID no está definido en .env.local; el inicio de sesión con Google no funcionará.")
        }
        resValue("string", "supabase_url", supabaseConfigValue("SUPABASE_URL", "SUPABASE_URL", "VITE_SUPABASE_URL"))
        resValue(
            "string",
            "supabase_publishable_key",
            supabaseConfigValue("SUPABASE_PUBLISHABLE_KEY", "SUPABASE_PUBLISHABLE_KEY", "VITE_SUPABASE_ANON_KEY")
        )
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
