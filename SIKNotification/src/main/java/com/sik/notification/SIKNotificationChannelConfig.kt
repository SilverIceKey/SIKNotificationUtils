package com.sik.notification

import android.app.NotificationManager
import android.media.RingtoneManager
import android.net.Uri
import androidx.core.app.NotificationCompat


/**
 * 抽象类用于配置通知渠道
 *
 * @property channelId 通知渠道 ID（可选）
 * @property channelName 通知渠道名称（可选）
 * @property channelDescription 通知渠道描述（可选）
 * @property enableLights 是否启用指示灯
 * @property enableVibration 是否启用震动
 * @property importance 通知重要性，取值范围为 [NotificationManager.IMPORTANCE_NONE], [NotificationManager.IMPORTANCE_MIN], [NotificationManager.IMPORTANCE_LOW], [NotificationManager.IMPORTANCE_DEFAULT], [NotificationManager.IMPORTANCE_HIGH], [NotificationManager.IMPORTANCE_MAX]
 * @property lockscreenVisibility 是否在锁定屏幕上显示通知，取值范围为 [NotificationCompat.VISIBILITY_SECRET], [NotificationCompat.VISIBILITY_PRIVATE], [NotificationCompat.VISIBILITY_PUBLIC]
 * @property soundUri 通知声音的 Uri（可选）
 * @property showBadge 是否桌面图标显示角标（可选）
 */
abstract class SIKNotificationChannelConfig(
    val channelId: String = "DefaultChannelId",
    val channelName: String = "DefaultChannel",
    val channelDescription: String = "DefaultChannel",
    val enableLights: Boolean = true,
    val enableVibration: Boolean = true,
    val importance: Int = NotificationManager.IMPORTANCE_DEFAULT,
    val lockscreenVisibility: Int = NotificationCompat.VISIBILITY_PRIVATE,
    val soundUri: Uri? = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION), // 默认通知声音
    val showBadge: Boolean = true,
    val vibrationPattern: LongArray = LongArray(1).apply {
        this[0] = 300
    },
    val groupId: String? = null
) {
    companion object {
        /**
         * 默认通知通道配置
         */
        val defaultChannelConfig by lazy {
            SIKNotificationDefaultChannelConfig()
        }

        /**
         * 默认通知通道分组配置
         */
        val defaultChannelGroupConfig by lazy {
            SIKNotificationDefaultChannelGroupConfig()
        }

        /**
         * 懒加载的具体子类实例
         */
        inline fun <reified T : SIKNotificationChannelConfig> instance(): T {
            return T::class.java.getDeclaredConstructor().newInstance()
        }
    }
}
