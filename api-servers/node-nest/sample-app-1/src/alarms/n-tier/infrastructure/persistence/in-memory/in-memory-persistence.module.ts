import { Module } from '@nestjs/common';

import { AlarmsRepository } from '../../../application/ports/alarms.repository';
import { AlarmEntity } from './entities/alarm.entity';
import { InMemoryAlarmRepository } from './repositories/alarm.repository';

@Module({
    imports: [],
    providers: [
        {
            provide: AlarmsRepository,
            useClass: InMemoryAlarmRepository,
        },
    ],
    exports: [AlarmsRepository],
})
export class InMemoryAlarmPersistenceModule {}
