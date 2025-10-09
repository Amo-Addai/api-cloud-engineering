import { Module } from '@nestjs/common';
import { TypeOrmModule } from '@nestjs/typeorm';

import { AlarmsRepository } from '../../../application/ports/alarms.repository';
import { AlarmEntity } from './entities/alarm.entity';
import { OrmAlarmRepository } from './repositories/alarm.repository';

@Module({
    imports: [TypeOrmModule.forFeature([AlarmEntity])],
    providers: [
        {
            provide: AlarmsRepository,
            useClass: OrmAlarmRepository,
        },
    ],
    exports: [AlarmsRepository],
})
export class OrmAlarmPersistenceModule {}
