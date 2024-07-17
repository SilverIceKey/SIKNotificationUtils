package com.sik.notification_sample

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.sik.notification.SIKNotificationDeleteReceiver
import com.sik.notification.SIKNotificationParams
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
        SIKNotificationDeleteReceiver.setNotificationDeleteListener { context, intent ->
            Toast.makeText(
                this,
                "通知被删除,通知id:${
                    intent?.getIntExtra(
                        SIKNotificationParams.INTENT_KEY_NOTIFICATION_ID,
                        0
                    )
                }",
                Toast.LENGTH_SHORT
            ).show()
        }
        sendNotify.setOnClickListener {
            if (SIKNotificationUtils.isNotificationPermissionGranted(this)) {
                showNotification(
                    NotificationChannelConfig.config,
                    "测试通知",
                    "测试通知内容",
                    Intent(this, ClickActivity::class.java)
                )
            } else {
                SIKNotificationUtils.requestNotificationPermission(this)
            }
        }
    }
}