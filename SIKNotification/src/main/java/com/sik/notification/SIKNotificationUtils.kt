package com.sik.notification

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.content.pm.PackageManager.NameNotFoundException
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat

object SIKNotificationUtils {

    const val DEFAULT_REQUEST_CODE = 100

    private var appIcon: Int = 0

    /**
     * 通道管理器
     */
    private var notificationManager: NotificationManager? = null

    /**
     * 不同配置的通知id从1开始自增
     */
    private val notificationIdMap: HashMap<String, Int> = hashMapOf()

    /**
     * 初始化通知工具库
     * @param context 上下文
     */
    fun init(context: Context) {
        appIcon = getApplicationIcon(context)
        notificationManager = context.getSystemService(NotificationManager::class.java)
    }

    /**
     * 创建或更新通知渠道，仅适用于 Android 8.0 及以上版本
     * @param context 上下文
     * @param enableLights 是否启用指示灯
     * @param enableVibration 是否启用震动
     * @param importance 通知重要性
     * @param lockscreenVisibility 是否在锁定屏幕上显示
     * @param soundUri 通知声音的 Uri（可选）
     */
    fun <T : SIKNotificationChannelConfig> createOrUpdateNotificationChannel(config: T) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (notificationManager == null) {
                throw NullPointerException("请先调用SIKNotificationUtils.init(context:Context)进行初始化")
            } else {

                val existingChannel = notificationManager?.getNotificationChannel(config.channelId)
                if (existingChannel == null) {
                    val channel = NotificationChannel(
                        config.channelId, config.channelName, config.importance
                    ).apply {
                        description = config.channelDescription
                        enableLights(config.enableLights)
                        enableVibration(config.enableVibration)
                        vibrationPattern = config.vibrationPattern
                        setShowBadge(config.showBadge)
                        this.lockscreenVisibility = config.lockscreenVisibility
                        config.soundUri?.let {
                            setSound(it, Notification.AUDIO_ATTRIBUTES_DEFAULT)
                        }
                    }
                    notificationManager?.createNotificationChannel(channel)
                } else {
                    // 可以更新的属性
                    existingChannel.name = config.channelName
                    existingChannel.description = config.channelDescription
                    existingChannel.importance = config.importance
                    notificationManager?.createNotificationChannel(existingChannel)
                }
                notificationIdMap[config::class.java.simpleName] = 0
            }
        }
    }

    /**
     * 删除通知通道
     * @param config 通知通道配置
     */
    fun <T : SIKNotificationChannelConfig> deleteNotificationChannel(config: T) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            deleteNotificationChannel(config.channelId)
        }
    }

    /**
     * 删除通知通道
     * @param channelId 通道id
     */
    fun deleteNotificationChannel(channelId: String?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (notificationManager == null) {
                throw NullPointerException("请先调用SIKNotificationUtils.init(context:Context)进行初始化")
            }
            notificationManager?.deleteNotificationChannel(channelId)
        }
    }

    /**
     * 请求通知权限，仅适用于 Android 13 及以上版本
     * @param activity 活动
     * @param requestCode 请求码
     */
    fun requestNotificationPermission(activity: Activity, requestCode: Int = DEFAULT_REQUEST_CODE) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permission = Manifest.permission.POST_NOTIFICATIONS
            if (ContextCompat.checkSelfPermission(
                    activity, permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(activity, arrayOf(permission), requestCode)
            }
        }
    }

    /**
     * 检查是否已授予通知权限，仅适用于 Android 13 及以上版本
     * @param context 上下文
     * @return 是否已授予通知权限
     */
    fun isNotificationPermissionGranted(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true // 低于 Android 13 默认授予通知权限
        }
    }

    /**
     * 显示通知
     * @param context 上下文
     * @param title 通知标题
     * @param content 通知内容
     * @param icon 通知图标资源 ID（可选）
     * @return Boolean 如果通知通道不存在则返回false
     */
    @SuppressLint("MissingPermission")
    fun <T : SIKNotificationChannelConfig> showNotification(
        context: Context, config: T, title: String, content: String, icon: Int = appIcon
    ): Boolean {
        if (config.channelId.isNullOrEmpty()) {
            return false
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (notificationManager == null) {
                throw NullPointerException("请先调用SIKNotificationUtils.init(context:Context)进行初始化")
            }
            if (notificationManager?.getNotificationChannel(config.channelId) == null) {
                createOrUpdateNotificationChannel(config)
            }
        }

        val notificationId = (notificationIdMap[config::class.java.simpleName] ?: 0) + 1

        val builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationCompat.Builder(context, config.channelId)
        } else {
            NotificationCompat.Builder(context)
        }

        builder.setSmallIcon(icon)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            notify(notificationId, builder.build())
        }

        notificationIdMap[config::class.java.simpleName] = notificationId
        return true
    }

    /**
     * 获取应用的默认图标
     * @param context 上下文
     * @return 应用图标资源 ID
     */
    private fun getApplicationIcon(context: Context): Int {
        return try {
            val packageManager = context.packageManager
            val applicationInfo = packageManager.getApplicationInfo(context.packageName, 0)
            applicationInfo.icon
        } catch (e: NameNotFoundException) {
            e.printStackTrace()
            android.R.drawable.sym_def_app_icon // 返回一个默认图标
        }
    }

    /**
     * 获取应用名称
     * @param context 上下文
     * @return 应用名称
     */
    private fun getAppName(context: Context): String {
        return try {
            val packageManager = context.packageManager
            val applicationInfo = packageManager.getApplicationInfo(context.packageName, 0)
            val labelRes = applicationInfo.labelRes
            context.getString(labelRes)
        } catch (e: NameNotFoundException) {
            e.printStackTrace()
            "UnKnownApp"
        }
    }
}
