package com.sik.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * 通知被关闭的通知
 */
class SIKNotificationDeleteReceiver : BroadcastReceiver() {
    companion object {
        private var notificationDeleteListener: SIKNotificationDeleteListener? = null

        /**
         * 设置监听器
         */
        @JvmStatic
        fun setNotificationDeleteListener(listener: SIKNotificationDeleteListener) {
            notificationDeleteListener = listener
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        notificationDeleteListener?.notificationDeleted(context, intent)
    }
}