# 早点睡 · 原生鸿蒙版

适配目标为手机上的 HarmonyOS 6.0 及以上（API 20+）。用 ArkTS 和 ArkUI 编写。设置时间后打开系统日历的新建日程页面，预填每天重复和开始时提醒。用户确认保存后，应用关闭时也由系统日历负责定时通知。提醒可能在锁屏时出现。应用内提供半屏样式预览。手机上的跨应用全局悬浮窗受系统权限限制。

## 使用 DevEco Studio 构建

1. 在支持 HarmonyOS 6.0 SDK（API 20）或更高版本的 DevEco Studio 中，新建 **Empty Ability** 手机项目，语言选 ArkTS，模型选 Stage。
2. 在项目配置中选用至少 API 20 的 SDK，最低兼容版本设为 HarmonyOS 6.0.0 (API 20)。在应用配置中将显示名设为「早点睡」，并使用你自己的唯一 Bundle Name。
3. 将本目录的 `entry/src/main/ets/pages/Index.ets` 覆盖到新项目的同名文件。保留模板生成的 Ability、页面路由、SDK 构建配置和资源。
4. 通过 DevEco Studio 配置调试签名，连接开启开发者模式的鸿蒙真机，点击 Run。对外发布时用自己的发布签名并按华为应用市场要求提交。Android APK 构建流程与此独立。

应用使用 Calendar Kit 的系统编辑器，由用户在日历界面确认。项目无需申请读取整个日历或后台代理提醒权限。提醒的编辑、删除和日历账号选择均由系统日历管理。若选择同步日历账号，日程的同步行为取决于该账号。

## 真机验收

- 选一个几分钟后的时间，打开日历编辑器，核对日期、时间、每天重复、活动开始时提醒。返回应用前保存。
- 退出应用，分别检查亮屏和锁屏时的系统日历通知。通常需在系统设置允许日历通知。
- 在日历中查看后续日期的重复日程，再修改整个系列、删除整个系列，确认后续通知按修改生效。
- 取消编辑时检查未创建事件。试用完成后，在日历中清理测试日程。
- 进入预览，检查半屏样式和「知道了」关闭按钮。

本项目当前提供可导入的 ArkTS 页面源码。HAP 构建、日历编辑器字段回填和真机通知行为待验证。

- [Calendar Kit API](https://developer.huawei.com/consumer/en/doc/harmonyos-references/js-apis-calendarmanager/)
- [受限开放权限](https://developer.huawei.com/consumer/cn/doc/harmonyos-guides/restricted-permissions/)
