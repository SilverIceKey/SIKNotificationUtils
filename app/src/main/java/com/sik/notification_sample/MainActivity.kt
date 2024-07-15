package com.sik.notification_sample

import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.sik.notification.SIKNotificationUtils
import com.sik.notification.SIKNotificationUtils.showNotification

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val addNotifyChannel = findViewById<Button>(R.id.add_notify_channel)
        val deleteNotifyChannel = findViewById<Button>(R.id.delete_notify_channel)
        val addNotifyChannelGroup = findViewById<Button>(R.id.add_notify_channel_group)
        val deleteNotifyChannelGroup = findViewById<Button>(R.id.delete_notify_channel_group)
        val addChannelToGroup = findViewById<Button>(R.id.add_channel_to_group)
        val removeChannelFromGroup = findViewById<Button>(R.id.remove_channel_from_group)
        val sendNotify = findViewById<Button>(R.id.send_notify)
        if (!SIKNotificationUtils.isNotificationPermissionGranted(this)) {
            SIKNotificationUtils.requestNotificationPermission(this)
        }
        addNotifyChannel.setOnClickListener {
            SIKNotificationUtils.createOrUpdateNotificationChannel(NotificationChannelConfig.config)
        }
        deleteNotifyChannel.setOnClickListener {
            SIKNotificationUtils.deleteNotificationChannel(NotificationChannelConfig.config)
        }
        addNotifyChannelGroup.setOnClickListener {
            SIKNotificationUtils.createOrUpdateNotificationChannelGroup(
                NotificationChannelGroupConfig.config
            )
        }
        deleteNotifyChannelGroup.setOnClickListener {
            SIKNotificationUtils.deleteNotificationChannelGroup(NotificationChannelGroupConfig.config)
        }
        addChannelToGroup.setOnClickListener {
            SIKNotificationUtils.setChannelGroup(
                NotificationChannelConfig.config,
                NotificationChannelGroupConfig.config
            )
        }
        removeChannelFromGroup.setOnClickListener {
            SIKNotificationUtils.setChannelGroup(NotificationChannelConfig.config, null)
        }
        sendNotify.setOnClickListener {
            if (SIKNotificationUtils.isNotificationPermissionGranted(this)) {
                SIKNotificationUtils.showNotification(
                    this,
                    NotificationChannelConfig.config,
                    "测试通知",
                    "测试通知内容"
                )
            } else {
                SIKNotificationUtils.requestNotificationPermission(this)
            }
        }
    }
}