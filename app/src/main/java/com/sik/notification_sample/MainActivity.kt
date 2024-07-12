package com.sik.notification_sample

import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.sik.notification.SIKNotificationUtils
import com.sik.sikcore.device.VibratorUtils

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
        val sendNotify = findViewById<Button>(R.id.send_notify)
        if (!SIKNotificationUtils.isNotificationPermissionGranted(this)) {
            SIKNotificationUtils.requestNotificationPermission(this)
        }
        addNotifyChannel.setOnClickListener {
            VibratorUtils.vibrate(300L)
            SIKNotificationUtils.createOrUpdateNotificationChannel(NotificationChannelConfig())
        }
        deleteNotifyChannel.setOnClickListener {
            SIKNotificationUtils.deleteNotificationChannel(NotificationChannelConfig())
        }
        sendNotify.setOnClickListener {
            if (SIKNotificationUtils.isNotificationPermissionGranted(this)) {
                SIKNotificationUtils.showNotification(
                    this,
                    NotificationChannelConfig(),
                    "测试通知",
                    "测试通知内容"
                )
            } else {
                SIKNotificationUtils.requestNotificationPermission(this)
            }
        }
    }
}