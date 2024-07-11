package com.sik.notification_sample

import android.app.Application
import com.sik.notification.SIKNotificationUtils

class App:Application() {
    override fun onCreate() {
        super.onCreate()
        SIKNotificationUtils.init(this)
    }
}