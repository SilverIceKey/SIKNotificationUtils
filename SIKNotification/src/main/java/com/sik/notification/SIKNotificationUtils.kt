package com.sik.notification

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.content.pm.PackageManager.NameNotFoundException
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat

object SIKNotificationUtils {

    const val DEFAULT_REQUEST_CODE = 100

    private var appIcon: Int = 0
    private var channelId: String = ""
    private var channelName: String = ""
    private var channelDescription: String = ""

    /**
     * 初始化通知工具库
     * @param context 上下文
     * @param channelConfig 通知渠道配置
     */
    fun init(
        context: Context,
        channelConfig: SIKNotificationChannelConfig = SIKNotificationChannelConfig.defaultChannelConfig
    ) {
        this.channelId = channelConfig.channelId ?: context.packageName
        this.channelName = channelConfig.channelName ?: getAppName(context)
        this.channelDescription =
            channelConfig.channelDescription ?: "Channel for ${getAppName(context)} notifications"
        createOrUpdateNotificationChannel(
            context,
            channelConfig.enableLights,
            channelConfig.enableVibration,
            channelConfig.importance,
            channelConfig.lockscreenVisibility,
            channelConfig.soundUri
        )
        appIcon = getApplicationIcon(context)
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
    private fun createOrUpdateNotificationChannel(
        context: Context,
        enableLights: Boolean,
        enableVibration: Boolean,
        importance: Int,
        lockscreenVisibility: Int,
        soundUri: Uri?
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager: NotificationManager =
                context.getSystemService(NotificationManager::class.java)

            val existingChannel = notificationManager.getNotificationChannel(channelId)

            if (existingChannel == null) {
                val channel = NotificationChannel(channelId, channelName, importance).apply {
                    description = channelDescription
                    enableLights(enableLights)
                    enableVibration(enableVibration)
                    this.lockscreenVisibility = lockscreenVisibility
                    soundUri?.let {
                        val audioAttributes = AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build()
                        setSound(it, audioAttributes)
                    }
                }
                notificationManager.createNotificationChannel(channel)
            } else {
                // 可以更新的属性
                existingChannel.enableLights(enableLights)
                existingChannel.enableVibration(enableVibration)
                existingChannel.setSound(soundUri, AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build())

                notificationManager.createNotificationChannel(existingChannel)
            }
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
                    activity,
                    permission
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
                context,
                Manifest.permission.POST_NOTIFICATIONS
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
     */
    @SuppressLint("MissingPermission")
    fun showNotification(
        context: Context,
        title: String,
        content: String,
        icon: Int = appIcon
    ) {
        val notificationId = 1

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(icon) // 使用应用的默认图标或传入的图标
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(context)) {
            notify(notificationId, builder.build())
        }
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
