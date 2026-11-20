# 早点睡 · 原生鸿蒙版

适用于 HarmonyOS 6.0（API 20）及以上的鸿蒙手机。应用使用 ArkTS 和 ArkUI 构建。默认提醒时间为 23:00。

在应用里选好时间，点「在系统日历中添加每日提醒」。日历编辑器打开后，选择「每天」重复，确认在活动开始时提醒，再点保存。提醒由系统日历发送，可以在日历中修改或删除整个重复日程。锁屏时是否显示，取决于日历的通知设置。

应用首页可以预览半屏提醒样式。原生鸿蒙版的定时提醒走系统日历通知，半屏样式只在应用内显示。

## 在 DevEco Studio 中运行

1. 安装带有 HarmonyOS API 20 SDK 的 DevEco Studio 6.0 或兼容版本，直接打开仓库中的 `harmonyos` 文件夹并同步项目。
2. 连接已开启开发者模式和 USB 调试的鸿蒙手机。在 **File → Project Structure → Signing Configs** 中为 default 产品配置调试签名。若应用标识已被占用，将 `AppScope/app.json5` 中的 `bundleName` 改为自己的唯一标识，再配置签名。
3. 选择连接的手机和 `entry` 模块，点击 **Run** 构建并安装。也可运行 **Build → Build Hap(s)/APP(s) → Build Hap(s)** 构建 HAP。

签名证书及私钥请留在本地。仓库提供完整工程源码。HAP 构建和真机运行尚待在 DevEco Studio 环境验证。

## 真机验证

- 选择几分钟后的时间。打开系统日历编辑器，核对时间、每天重复和活动开始时提醒，然后保存。
- 退出应用，在亮屏和锁屏时分别测试提醒，并检查系统是否允许日历通知。
- 修改或删除整个重复日程，检查后续提醒能否按新设置触发或停止。
- 检查取消编辑后没有创建日程，测试完后清理测试日程。
- 打开半屏预览，点「知道了」确认能关闭。

[Calendar Kit 事件管理](https://developer.huawei.com/consumer/en/doc/HarmonyOS-Guides/calendarmanager-event-developer/)
