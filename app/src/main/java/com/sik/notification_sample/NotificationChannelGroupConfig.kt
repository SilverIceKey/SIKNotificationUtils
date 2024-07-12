package com.sik.notification_sample

import com.sik.notification.SIKNotificationChannelGroupConfig

class NotificationChannelGroupConfig : SIKNotificationChannelGroupConfig(
    channelGroupId = "测试通知通道分组Id3",
    channelGroupName = "测试通知通道名称分组2",
    channelGroupDescription = "测试通知通道描述分组2"
) {
    companion object {
        val config by lazy { NotificationChannelGroupConfig() }
    }
}