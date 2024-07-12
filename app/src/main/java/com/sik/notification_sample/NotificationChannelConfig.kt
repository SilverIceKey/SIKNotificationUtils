package com.sik.notification_sample

import com.sik.notification.SIKNotificationChannelConfig

class NotificationChannelConfig : SIKNotificationChannelConfig(
    channelId = "测试通知通道Id2",
    channelName = "测试通知通道名称1",
    channelDescription = "测试通知通道描述1"
) {
    companion object {
        val config by lazy {
            NotificationChannelConfig()
        }
    }
}