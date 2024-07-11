package com.sik.notification

import android.app.NotificationManager
import androidx.core.app.NotificationCompat

class SIKNotificationDefaultChannelConfig : SIKNotificationChannelConfig(
    enableLights = true,
    enableVibration = true,
    importance = NotificationManager.IMPORTANCE_HIGH,
    lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC,
    soundUri = null // 默认不设置声音
)
