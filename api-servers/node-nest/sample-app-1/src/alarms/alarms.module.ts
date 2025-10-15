import { Module } from '@nestjs/common';
import { AlarmsService } from './alarms.service';
import { AlarmsController } from './alarms.controller';
import { AlarmController } from '../presenters/http/alarm.controller';
import { AlarmFactory } from '../domain/factories/alarm.factory';
import { AlarmCreatedEventHandler } from './event-handlers/alarm-created.event-handler';
import { CascadingAlarmsSaga } from './n-tier/application/sagas/cascading-alarms.saga';
import { NotifyFacilitySupervisorCommandHandler } from './n-tier/application/commands/notify-facility-supervisor.command-handler';

@Module({
  controllers: [
    AlarmsController,
    AlarmController,
  ],
  providers: [
    AlarmsService,
    AlarmFactory,
    CreateAlarmCommandHandler,
    GetAlarmsQueryHandler,
    AlarmCreatedEventHandler,
    AcknowledgeAlarmCommandHandler,
    AlarmAcknowledgedEventHandler,
    CascadingAlarmsSaga,
    NotifyFacilitySupervisorCommandHandler,
  ],
})
export class AlarmsModule {
  static withInfrastructure(infrastructureModule: Type | DynamicModule) {
    return {
      module: AlarmsModule,
      imports: [infrastructureModule],
    }
  }
}
