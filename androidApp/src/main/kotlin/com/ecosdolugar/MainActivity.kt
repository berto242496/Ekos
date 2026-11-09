package com.ecosdolugar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.ecosdolugar.auth.AndroidGoogleAuthService
import com.ecosdolugar.auth.CurrentActivity
import com.ecosdolugar.data.SupabaseRoutesRepository

class MainActivity : ComponentActivity() {
    private val appViewModel: AppViewModel by viewModels {
        viewModelFactory {
            initializer {
                // The ViewModel outlives the activity, so it must only hold the application context.
                val routesRepository = SupabaseRoutesRepository(
                    projectUrl = getString(R.string.supabase_url),
                    publishableKey = getString(R.string.supabase_publishable_key)
                )
                AppViewModel(AndroidGoogleAuthService(applicationContext, routesRepository), routesRepository)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        CurrentActivity.attach(this)
        setContent { App(appViewModel) }
    }

    override fun onDestroy() {
        CurrentActivity.detach(this)
        super.onDestroy()
    }
}
