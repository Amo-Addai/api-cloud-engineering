import { Injectable } from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { Repository } from 'typeorm';

import { AlarmsRepository } from '../../../../application/ports/alarms.repository';
import { Alarm } from '../../../../domain/alarm';
import { AlarmEntity } from '../entities/alarm.entity';
import { AlarmMapper } from '../mappers/alarm.mapper';

@Injectable()
export class OrmAlarmRepository implements AlarmsRepository {
    constructor(
        @InjectRepository(AlarmEntity)
        private readonly alarmRepository: Repository<AlarmEntity>,
    ) {}

    async findAll_(): Promise<Alarm[]> {
        return this.alarmRepository.find();
    }

    async findAll(): Promise<Alarm[]> {
        const entities = this.alarmRepository.find();
        return entities.map(item => AlarmMapper.toDomain(item));
    }

    save_: (Alarm) => Promise<Alarm> = (alarm: Alarm): Promise<Alarm> =>
        this.alarmRepository.save(alarm)

    save: (Alarm) => Promise<Alarm> = 
        async (
            alarm: Alarm,
            [persistenceModel, newEntity]: any[] = []
        ): Promise<Alarm> => (
            persistenceModel = AlarmMapper.toPersistence(alarm),
            newEntity = await this.alarmRepository.save(persistenceModel),
            AlarmMapper.toDomain(newEntity)
    )
}
