package com.sik.notification

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationChannelGroup
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.PackageManager.NameNotFoundException
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.text.TextUtils
import android.widget.RemoteViews
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat


object SIKNotificationUtils {

    const val DEFAULT_REQUEST_CODE = 100

    private var appIcon: Int = 0

    /**
     * 通知删除接收器ACTION
     */
    private const val ACTION_SIK_NOTIFICATION_DELETED_RECEIVER =
        "ACTION_SIK_NOTIFICATION_DELETED_RECEIVER"

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
     * @param config 通知通道配置
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
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                            if (notificationManager?.getNotificationChannelGroup(config.groupId) != null) {
                                group = config.groupId
                            }
                        }
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
                    if (config.groupId == null) {
                        existingChannel.group = null
                    }
                    notificationManager?.createNotificationChannel(existingChannel)
                }
                notificationIdMap[config::class.java.simpleName] = 0
            }
        }
    }

    /**
     * 创建或更新通知渠道分组，仅适用于 Android 9.0 及以上版本
     * @param config 通知通道分组配置
     */
    fun <T : SIKNotificationChannelGroupConfig> createOrUpdateNotificationChannelGroup(config: T) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            if (notificationManager == null) {
                throw NullPointerException("请先调用SIKNotificationUtils.init(context:Context)进行初始化")
            } else {
                val existingChannelGroup =
                    notificationManager?.getNotificationChannelGroup(config.channelGroupId)
                if (existingChannelGroup == null) {
                    val channelGroup = NotificationChannelGroup(
                        config.channelGroupId, config.channelGroupName
                    ).apply {
                        description = config.channelGroupDescription
                    }
                    notificationManager?.createNotificationChannelGroup(channelGroup)
                } else {
                    // 可以更新的属性
                    existingChannelGroup.description = config.channelGroupDescription
                    notificationManager?.createNotificationChannelGroup(existingChannelGroup)
                }
                notificationIdMap[config::class.java.simpleName] = 0
            }
        }
    }


    /**
     * 给通知通道分组
     * @param channelConfig 通知通道配置
     * @param channelGroupConfig 通知通道分组配置 为空则移出分组
     *
     */
    fun <T : SIKNotificationChannelConfig, M : SIKNotificationChannelGroupConfig> setChannelGroup(
        channelConfig: T, channelGroupConfig: M?
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            setChannelGroup(channelConfig.channelId, channelGroupConfig?.channelGroupId)
        }
    }

    /**
     * 给通知通道分组
     * @param channelId 通知通道id
     * @param channelGroupId 通知通道分组id 为空则移出分组
     */
    fun setChannelGroup(channelId: String?, channelGroupId: String?): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            if (notificationManager == null) {
                throw NullPointerException("请先调用SIKNotificationUtils.init(context:Context)进行初始化")
            }
            try {
                notificationManager?.getNotificationChannel(channelId)?.let {
                    it.group = channelGroupId
                    notificationManager?.createNotificationChannel(it)
                }
            } catch (e: Exception) {
                return false
            }
        }
        return true
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
     * 删除通知通道分组
     * @param config 通知通道分组配置
     */
    fun <T : SIKNotificationChannelGroupConfig> deleteNotificationChannelGroup(config: T) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            deleteNotificationChannel(config.channelGroupId)
        }
    }

    /**
     * 删除通知通道分组
     * @param channelId 通道id
     */
    fun deleteNotificationChannelGroup(channelGroupId: String?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (notificationManager == null) {
                throw NullPointerException("请先调用SIKNotificationUtils.init(context:Context)进行初始化")
            }
            notificationManager?.deleteNotificationChannelGroup(channelGroupId)
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

    /**
     * 检查应用的通知是否被启用
     *
     * @param context 应用上下文
     * @return 如果通知被启用则返回 true，否则返回 false
     */
    fun Context.isNotificationEnabled(): Boolean {
        val notificationManager =
            this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // 对于 Android O 及以上版本
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = notificationManager.importance
            return importance != NotificationManager.IMPORTANCE_NONE
        }

        // 对于 Android O 以下版本
        val enabledListeners =
            Settings.Secure.getString(this.contentResolver, "enabled_notification_listeners")
        val packageName = this.packageName
        return !TextUtils.isEmpty(enabledListeners) && enabledListeners.contains(packageName)
    }

    /**
     * 跳转到应用的通知设置界面
     *
     * @param context 应用上下文
     */
    fun Context.openNotificationSettings() {
        val intent = Intent()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // 对于 Android O 及以上版本
            intent.action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, this.packageName)
        } else {
            // 对于 Android O 以下版本
            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            intent.addCategory(Intent.CATEGORY_DEFAULT)
            intent.data = Uri.parse("package:" + this.packageName)
        }
        this.startActivity(intent)
    }

    /**
     * 显示通知
     * @param context 上下文
     * @param title 通知标题
     * @param content 通知内容
     * @param clickIntent 通知跳转意图
     * @param appointNotificationId 指定通知id
     * @param icon 通知图标资源 ID（可选）
     * @return Boolean 如果通知通道不存在则返回false
     */
    @SuppressLint("MissingPermission")
    fun <T : SIKNotificationChannelConfig> Context.showNotification(
        channelConfig: T,
        notificationConfig: SIKNotificationConfig,
        icon: Int = appIcon,
    ): Boolean {
        if (channelConfig.channelId.isEmpty()) {
            return false
        }
        if (notificationManager == null) {
            throw NullPointerException("请先调用SIKNotificationUtils.init(context:Context)进行初始化")
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (notificationManager?.getNotificationChannel(channelConfig.channelId) == null) {
                createOrUpdateNotificationChannel(channelConfig)
            }
        }

        val notificationId = if (notificationConfig.appointNotificationId == -1) {
            (notificationIdMap[channelConfig::class.java.simpleName] ?: 0) + 1
        } else {
            notificationConfig.appointNotificationId
        }
        notificationConfig.appointNotificationId = notificationId
        val deleteIntent = Intent(
            this,
            SIKNotificationDeleteReceiver::class.java
        )
        deleteIntent.setAction(ACTION_SIK_NOTIFICATION_DELETED_RECEIVER)
        val deletePendingIntent =
            PendingIntent.getBroadcast(this, 0, deleteIntent, PendingIntent.FLAG_IMMUTABLE)
        val clickPendingIntent = notificationConfig.clickIntent?.let { clickIntent ->
            clickIntent.putExtra(
                SIKNotificationParams.INTENT_KEY_NOTIFICATION_ID,
                notificationId
            )
            PendingIntent.getActivity(
                this,
                0,
                clickIntent,
                PendingIntent.FLAG_IMMUTABLE
            )
        }

        deleteIntent.putExtra(SIKNotificationParams.INTENT_KEY_NOTIFICATION_ID, notificationId)
        val builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationCompat.Builder(this, channelConfig.channelId)
        } else {
            NotificationCompat.Builder(this)
        }

        builder.setSmallIcon(icon)
            .apply {
                if (notificationConfig.isCustomView) {
                    val customContentView =
                        RemoteViews(
                            packageName,
                            notificationConfig.customContentView
                        )
                    val customBigContentView =
                        RemoteViews(
                            packageName,
                            notificationConfig.customBigContentView
                        )
                    val customHeadsUpContentView =
                        RemoteViews(
                            packageName,
                            notificationConfig.customHeadsUpContentView
                        )
                    this.setCustomContentView(customContentView)
                        .setCustomBigContentView(customBigContentView)
                        .setCustomHeadsUpContentView(customHeadsUpContentView)
                        .setContent(customContentView)
                        .setStyle(notificationConfig.style)
                } else {
                    this.setContentTitle(notificationConfig.title)
                        .setContentText(notificationConfig.content)
                    if (notificationConfig.isProgressNotification) {
                        this.setProgress(
                            notificationConfig.progressData.getMaxProgress(),
                            notificationConfig.progressData.getCurrentProgress(),
                            notificationConfig.progressData.getIndeterminate()
                        )
                    }
                }
                if (clickPendingIntent != null) {
                    this.setContentIntent(clickPendingIntent)
                }
            }
            .setDefaults(notificationConfig.default)
            .setPriority(notificationConfig.priority)
            .setOnlyAlertOnce(notificationConfig.onlyAlertOnce)
            .setOngoing(notificationConfig.onGoing)
            .setDeleteIntent(deletePendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(this)) {
            notify(notificationId, builder.build())
        }

        notificationIdMap[channelConfig::class.java.simpleName] = notificationId
        return true
    }

    /**
     * 关闭通知
     */
    fun cancelNotify(notificationConfig: SIKNotificationConfig) {
        if (notificationManager == null) {
            throw NullPointerException("请先调用SIKNotificationUtils.init(context:Context)进行初始化")
        }
        notificationManager?.cancel(notificationConfig.appointNotificationId)
    }
}
