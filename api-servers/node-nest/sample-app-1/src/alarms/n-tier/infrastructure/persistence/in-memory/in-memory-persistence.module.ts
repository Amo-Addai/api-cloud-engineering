import { Module } from '@nestjs/common';

import { AlarmsRepository } from '../../../application/ports/alarms.repository';
import { AlarmEntity } from './entities/alarm.entity';
import { InMemoryAlarmRepository } from './repositories/alarm.repository';

@Module({
    imports: [],
    providers: [
        InMemoryAlarmRepository,
        {
            provide: AlarmsRepository,
            useClass: InMemoryAlarmRepository,
        },
        {
            provide: CreateAlarmsRepository,
            useClass: InMemoryAlarmRepository,
        },
        {
            provide: FindlarmsRepository,
            useClass: InMemoryAlarmRepository,
        },
        {
            provide: UpsertMaterializedAlarmsRepository,
            useClass: InMemoryAlarmRepository,
        },
    ],
    exports: [
        AlarmsRepository,
        CreateAlarmsRepository,
        FindlarmsRepository,
        UpsertMaterializedAlarmsRepository,
    ],
})
export class InMemoryAlarmPersistenceModule {}
