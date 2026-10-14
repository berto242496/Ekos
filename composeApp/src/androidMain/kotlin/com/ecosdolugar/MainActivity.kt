package com.ecosdolugar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.ecosdolugar.auth.AndroidGoogleAuthService
import com.ecosdolugar.data.SupabaseRoutesRepository

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val routesRepository = SupabaseRoutesRepository(
            projectUrl = getString(R.string.supabase_url),
            publishableKey = getString(R.string.supabase_publishable_key)
        )
        setContent {
            App(
                googleAuthService = AndroidGoogleAuthService(this, routesRepository),
                routeDataSource = routesRepository
            )
        }
    }
}
