import { common } from '@kit.AbilityKit';
import { BusinessError } from '@kit.BasicServicesKit';
import { calendarManager } from '@kit.CalendarKit';

@Entry
@Component
struct Index {
  @State private hour: number = 23;
  @State private minute: number = 0;
  @State private saving: boolean = false;
  @State private status: string = '';
  @State private preview: boolean = false;

  private selectedTime(): string {
    const hour = this.hour.toString().padStart(2, '0');
    const minute = this.minute.toString().padStart(2, '0');
    return `${hour}:${minute}`;
  }

  private async openCalendarEditor(): Promise<void> {
    if (this.saving) {
      return;
    }
    this.saving = true;
    this.status = '';
    try {
      const now = new Date();
      const next = new Date(now.getTime());
      next.setHours(this.hour, this.minute, 0, 0);
      if (next.getTime() <= now.getTime()) {
        next.setDate(next.getDate() + 1);
      }

      const event: calendarManager.Event = {
        title: '早点睡 · 该睡觉了',
        type: calendarManager.EventType.NORMAL,
        startTime: next.getTime(),
        endTime: next.getTime() + 10 * 60 * 1000,
        reminderTime: [0],
        description: '先放下手机，早点休息。'
      };
      const context = this.getUIContext().getHostContext() as common.UIAbilityContext;
      const manager = calendarManager.getCalendarManager(context);
      const eventId = await manager.editEvent(event);
      this.status = eventId > 0
        ? '已保存到系统日历。修改或关闭提醒，请在日历里编辑整个重复日程。'
        : '未保存日程。';
    } catch (error) {
      const code = (error as BusinessError).code;
      this.status = code ? `无法保存到系统日历（错误 ${code}）。` : '无法保存到系统日历。';
    } finally {
      this.saving = false;
    }
  }

  build() {
    Stack({ alignContent: Alignment.Center }) {
      Scroll() {
        Column({ space: 16 }) {
          Text('早点睡')
            .fontSize(36)
            .fontWeight(FontWeight.Bold)
            .fontColor('#202640')
          Text('提醒时间 ' + this.selectedTime())
            .fontSize(19)
            .fontColor('#202640')
          TimePicker({ selected: new Date(2000, 0, 1, this.hour, this.minute) })
            .useMilitaryTime(true)
            .height(144)
            .onChange((time: TimePickerResult) => {
              this.hour = time.hour;
              this.minute = time.minute;
            })
          Button('在系统日历中添加每日提醒')
            .width('100%')
            .backgroundColor('#46528D')
            .enabled(!this.saving)
            .onClick(() => {
              this.openCalendarEditor();
            })
          Text('打开日历后，将重复设为「每天」，并确认在活动开始时提醒。已有提醒可在日历里编辑。')
            .fontColor('#414965')
          Text('鸿蒙版由系统日历推送通知，锁屏时也可能收到。')
            .fontColor('#414965')
          if (this.status.length > 0) {
            Text(this.status)
              .fontColor('#202640')
          }
          Button('预览应用内半屏样式')
            .width('100%')
            .backgroundColor('#46528D')
            .onClick(() => {
              this.preview = true;
            })
        }
        .width('100%')
        .padding(24)
      }
      .width('100%')
      .height('100%')
      .hitTestBehavior(this.preview ? HitTestMode.None : HitTestMode.Default)

      if (this.preview) {
        Column({ space: 12 }) {
          Text('该睡觉了')
            .fontSize(30)
            .fontWeight(FontWeight.Bold)
            .fontColor(Color.White)
          Text('先放下手机，早点休息。')
            .fontColor('#D6DAFA')
          Button('知道了')
            .onClick(() => {
              this.preview = false;
            })
        }
        .width('94%')
        .height('53%')
        .justifyContent(FlexAlign.Center)
        .backgroundColor('#202640')
        .borderRadius(24)
      }
    }
    .width('100%')
    .height('100%')
    .backgroundColor('#F2F3FB')
  }
}
