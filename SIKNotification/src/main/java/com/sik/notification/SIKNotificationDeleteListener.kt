package com.sik.notification

import android.content.Context
import android.content.Intent

/**
 * 通知删除监听
 */
fun interface SIKNotificationDeleteListener {
    fun notificationDeleted(context: Context?, intent: Intent?)
}