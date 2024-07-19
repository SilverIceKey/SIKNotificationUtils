package com.sik.notification

import android.content.Intent
import androidx.core.app.NotificationCompat

/**
 * 通知配置
 */
data class SIKNotificationConfig(
    val title: String = "",
    val content: String = "",
    val clickIntent: Intent? = null,
    var appointNotificationId: Int = -1,
    val default: Int = NotificationCompat.DEFAULT_ALL,
    val priority: Int = NotificationCompat.PRIORITY_DEFAULT,
    val isProgressNotification: Boolean = false,
    val progressData: ProgressData = ProgressData(),
    val isCustomView: Boolean = false,
    val onGoing: Boolean = isProgressNotification || isCustomView,
    val onlyAlertOnce: Boolean = isProgressNotification || isCustomView,
    val customContentView: Int = -1,
    val customBigContentView: Int = -1,
    val customHeadsUpContentView: Int = -1,
    val style: NotificationCompat.Style = NotificationCompat.DecoratedCustomViewStyle(),
)

/**
 * 进度条数据
 */
data class ProgressData(
    private var max: Int = 0,
    private var progress: Int = 0,
    private var indeterminate: Boolean = true
) {
    /**
     * 消失
     */
    fun dismiss() {
        max = 0
        progress = 0
        indeterminate = false
    }

    /**
     * 是否需要消失
     */
    fun isDismiss(): Boolean {
        return max == 0 && progress == 0 && !indeterminate
    }

    /**
     * 获取最大值
     */
    fun getMaxProgress(): Int {
        return max
    }

    /**
     * 获取当前进度
     */
    fun getCurrentProgress(): Int {
        return progress
    }

    /**
     * 获取是否显示
     */
    fun getIndeterminate(): Boolean {
        return indeterminate
    }

    /**
     * 设置最大值
     */
    fun setMaxProgress(max: Int) {
        this.max = max
        indeterminate = false
    }

    /**
     * 设置进度
     */
    fun setProgress(progress: Int) {
        this.progress = progress
    }

}