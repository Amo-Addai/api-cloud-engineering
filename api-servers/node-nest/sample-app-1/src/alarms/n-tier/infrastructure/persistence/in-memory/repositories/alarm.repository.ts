import { Injectable } from '@nestjs/common';

import { AlarmsRepository } from '../../../../application/ports/alarms.repository';
import { Alarm } from '../../../../domain/alarm';
import { AlarmEntity } from '../entities/alarm.entity';
import { AlarmMapper } from '../mappers/alarm.mapper';

@Injectable()
export class InMemoryAlarmRepository implements AlarmsRepository {

    private readonly alarms = new Map<string, AlarmEntity>();

    constructor(
        
    ) {}

    async findAll_(): Promise<AlarmEntity[]> {
        return Array.from(this.alarms.values());
    }

    async findAll(): Promise<Alarm[]> {
        const entities = Array.from(this.alarms.values());
        return entities.map(item => AlarmMapper.toDomain(item));
    }

    save_: (Alarm) => Promise<Alarm> = (alarm: Alarm): Promise<Alarm> => (
        this.alarms.set(alarm.id, AlarmMapper.toPersistence(alarm)),
        AlarmMapper.toDomain(this.alarms.get(alarm.id))
    )

    save: (Alarm) => Promise<Alarm> = 
        (
            alarm: Alarm,
            [persistenceModel, newEntity]: any[] = []
        ): Promise<Alarm> => (
            persistenceModel = AlarmMapper.toPersistence(alarm),
            this.alarms.set(persistenceModel.id, persistenceModel),
            newEntity = this.alarms.get(persistenceModel.id),
            AlarmMapper.toDomain(newEntity)
    )
}
