import { Injectable } from '@nestjs/common';

import { AlarmsRepository } from '../../../../application/ports/alarms.repository';
import { Alarm } from '../../../../domain/alarm';
import { AlarmEntity } from '../entities/alarm.entity';
import { AlarmMapper } from '../mappers/alarm.mapper';
import { FindAlarmsRepository } from 'src/alarms/n-tier/application/ports/find-alarms.repository';
import { CreateAlarmsRepository } from 'src/alarms/n-tier/application/ports/create-alarm.repository';
import { UpsertMaterializedAlarmRepository } from 'src/alarms/n-tier/application/ports/upsert-materialized-alarm.respository';
import { AlarmReadModel } from 'src/alarms/n-tier/domain/read-models/alarm.read-model';

@Injectable()
export class InMemoryAlarmRepository implements
AlarmsRepository,
CreateAlarmsRepository,
FindAlarmsRepository,
UpsertMaterializedAlarmRepository
{

    private readonly alarms = new Map<string, AlarmEntity>();
    private readonly materializedAlarmViews = new Map<string, AlarmReadModel>();

    constructor(
    ) {}

    async findAll(): Promise<AlarmReadModel[]> {
        return Array.from(this.alarms.values());
    }

    async findAll_(): Promise<AlarmEntity[]> {
        return Array.from(this.alarms.values());
    }

    async findAll__(): Promise<Alarm[]> {
        const entities = Array.from(this.alarms.values());
        return entities.map(item => AlarmMapper.toDomain(item));
    }

    async findAll___(): Promise<AlarmReadModel[]> {
        return Array.from(this.materializedAlarmViews.values());
    }

    save_: (Alarm) => Promise<Alarm> = async (alarm: Alarm): Promise<Alarm> => (
        this.alarms.set(alarm.id, AlarmMapper.toPersistence(alarm)),
        AlarmMapper.toDomain(this.alarms.get(alarm.id))
    )

    save: (Alarm) => Promise<Alarm> = 
        async (
            alarm: Alarm,
            [persistenceModel, newEntity]: any[] = []
        ): Promise<Alarm> => (
            persistenceModel = AlarmMapper.toPersistence(alarm),
            this.alarms.set(persistenceModel.id, persistenceModel),
            newEntity = this.alarms.get(persistenceModel.id),
            AlarmMapper.toDomain(newEntity)
    )

    upsert(alarm: Pick<AlarmReadModel, 'id'> & Partial<AlarmReadModel>): Promise<void> {
        if (this.materializedAlarmViews.has(alarm.id)) {
            this.materializedAlarmViews.set(alarm.id, {
                ...this.materializedAlarmViews.get(alarm.id),
                ...alarm,
            });
            return;
        }
        this.materializedAlarmViews.set(alarm.id, alarm as AlarmReadModel);
    }
}
