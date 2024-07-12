package com.sik.notification_sample

import android.app.Application
import com.sik.notification.SIKNotificationUtils
import com.sik.sikcore.SIKCore

class App:Application() {
    override fun onCreate() {
        super.onCreate()
        SIKNotificationUtils.init(this)
        SIKCore.init(this)
    }
}