package com.sik.notification_sample

import com.sik.notification.SIKNotificationChannelConfig

class NotificationChannelConfig : SIKNotificationChannelConfig(
    channelId = "测试通知通道Id3",
    channelName = "测试通知通道名称2",
    channelDescription = "测试通知通道描述2",
    groupId = "测试通知通道分组Id1"
) {
    companion object {
        val config by lazy {
            NotificationChannelConfig()
        }
    }
}