# SIKNotificationUtils

[![](https://jitpack.io/v/SilverIceKey/SIKNotificationUtils.svg)](https://jitpack.io/#SilverIceKey/SIKNotificationUtils)

`SIKNotificationUtils` 是一个用于 Android 的通知工具库，旨在简化和增强通知的创建和管理。通过该工具库，开发者可以轻松地在应用中实现各种类型的通知，并提供了一些实用的功能，如快速创建通知、处理通知权限等。

## 特性

- **简单易用**：通过简单的 API 快速创建和显示通知。
- **通知渠道**：支持 Android 8.0 及以上版本的通知渠道管理。
- **权限处理**：自动处理 Android 13 及以上版本的通知权限请求。
- **高度可定制**：支持自定义通知样式和行为。

## 安装

在你的 `build.gradle` 文件中添加以下依赖项：

```groovy
dependencies {
    implementation("com.github.silvericekey:SIKNotificationUtils:1.0.0")
}
```

## 使用方法

### 1. 初始化

在AndroidManifest中声明权限

```xml
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
```



在你的应用程序中初始化 `SIKNotificationUtils`：

```kotlin
import com.sik.notificationutils.SIKNotificationUtils

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        SIKNotificationUtils.init(this,配置的通道类)
    }
}
```

新增通知通道配置继承自SIKNotificationChannelConfig用于配置通道或者使用默认通道

### 2. 请求通知权限（Android 13 及以上）

在需要的地方检查或者请求通知权限：

```kotlin
if (SIKNotificationUtils.isNotificationPermissionGranted(this)) {
    SIKNotificationUtils.showNotification(this, "测试通知", "测试通知内容")
} else {
    SIKNotificationUtils.requestNotificationPermission(this)
}
```

通知请求返回：

```kotlin
override fun onRequestPermissionsResult(
    requestCode: Int,
    permissions: Array<out String>,
    grantResults: IntArray
) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    if (requestCode == SIKNotificationUtils.DEFAULT_REQUEST_CODE) {
        if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
            SIKNotificationUtils.showNotification(this, "测试通知", "测试通知内容")
        }
    }
}
```

### 3. 显示通知

使用 `SIKNotificationUtils` 创建并显示通知：

```kotlin
SIKNotificationUtils.showNotification(this, "测试通知", "测试通知内容")
```

## 示例项目

可以参考示例项目来了解如何使用 `SIKNotificationUtils`。请访问 [示例项目](./app/src/main/java/com/sik/notification_sample/MainActivity.kt)。

## 贡献

欢迎贡献代码！请阅读 [CONTRIBUTING.md](./CONTRIBUTING.md) 了解详细的贡献流程。

## 许可证

`SIKNotificationUtils` 遵循 MIT 许可证。有关详细信息，请参阅 [LICENSE](./LICENSE)。
