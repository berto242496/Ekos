package com.ecosdolugar.auth

import android.app.Activity
import java.lang.ref.WeakReference

/**
 * Credential Manager needs an Activity to show its UI, but the auth service lives in the
 * ViewModel and survives activity recreation, so it looks the current activity up here.
 */
object CurrentActivity {
    private var reference: WeakReference<Activity>? = null

    fun attach(activity: Activity) {
        reference = WeakReference(activity)
    }

    fun detach(activity: Activity) {
        if (reference?.get() === activity) reference = null
    }

    fun get(): Activity? = reference?.get()
}
