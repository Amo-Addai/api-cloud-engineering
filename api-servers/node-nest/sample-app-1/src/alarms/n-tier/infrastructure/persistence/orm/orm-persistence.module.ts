import { Module } from '@nestjs/common';
import { TypeOrmModule } from '@nestjs/typeorm';
import { MongooseModule } from '@nestjs/mongoose';

import { AlarmsRepository } from '../../../application/ports/alarms.repository';
import { AlarmEntity } from './entities/alarm.entity';
import { AlarmItemEntity } from './entities/alarm-item.entity';
import { OrmAlarmRepository } from './repositories/alarm.repository';
import {
    MaterializedAlarmView,
    MaterializedAlarmViewSchema,
} from './schemas/materialized-alarm-view.schema';

@Module({
    imports: [
        TypeOrmModule.forFeature([AlarmEntity, AlarmItemEntity]),
        MongooseModule.forFeature([
            { name: MaterializedAlarmView.name, schema: MaterializedAlarmViewSchema },
        ])
    ],
    providers: [
        {
            provide: AlarmsRepository,
            useClass: OrmAlarmRepository,
        },
        {
            provide: CreateAlarmsRepository,
            useClass: OrmCreateAlarmRepository,
        },
        {
            provide: FindAlarmsRepository,
            useClass: OrmFindAlarmRepository,
        },
        {
            provide: UpsertMaterializedAlarmsRepository,
            useClass: OrmUpsertMaterializedAlarmRepository,
        },
    ],
    exports: [
        AlarmsRepository,
        CreateAlarmsRepository,
        FindAlarmsRepository,
        UpsertMaterializedAlarmsRepository,
    ],
})
export class OrmAlarmPersistenceModule {}
