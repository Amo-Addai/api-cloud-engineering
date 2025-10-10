import { Module } from '@nestjs/common';
import { TypeOrmModule } from '@nestjs/typeorm';

import { AlarmsRepository } from '../../../application/ports/alarms.repository';
import { AlarmEntity } from './entities/alarm.entity';
import { AlarmItemEntity } from './entities/alarm-item.entity';
import { OrmAlarmRepository } from './repositories/alarm.repository';

@Module({
    imports: [TypeOrmModule.forFeature([AlarmEntity, AlarmItemEntity])],
    providers: [
        {
            provide: AlarmsRepository,
            useClass: OrmAlarmRepository,
        },
    ],
    exports: [AlarmsRepository],
})
export class OrmAlarmPersistenceModule {}
