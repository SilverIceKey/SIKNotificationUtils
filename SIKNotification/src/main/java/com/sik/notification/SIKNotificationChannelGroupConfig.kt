package com.sik.notification


/**
 * 抽象类用于配置通知渠道
 *
 * @property channelGroupId 通知渠道分组 ID（可选）
 * @property channelGroupName 通知渠道分组名称（可选）
 * @property channelGroupDescription 通知渠道分组描述（可选）
 */
abstract class SIKNotificationChannelGroupConfig(
    val channelGroupId: String = "DefaultChannelGroupId",
    val channelGroupName: String = "DefaultChannelGroup",
    val channelGroupDescription: String = "DefaultChannelGroup",
) {
    companion object {
        /**
         * 默认通知通道分组配置
         */
        val defaultChannelGroupConfig by lazy {
            SIKNotificationDefaultChannelConfig()
        }

        /**
         * 懒加载的具体子类实例
         */
        inline fun <reified T : SIKNotificationChannelGroupConfig> instance(): T {
            return T::class.java.getDeclaredConstructor().newInstance()
        }
    }
}
