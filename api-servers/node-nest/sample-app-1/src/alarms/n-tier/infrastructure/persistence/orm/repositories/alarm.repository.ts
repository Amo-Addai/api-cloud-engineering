import { Injectable } from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { Repository } from 'typeorm';

import { AlarmsRepository } from '../../../../application/ports/alarms.repository';
import { Alarm } from '../../../../domain/alarm';
import { AlarmEntity } from '../entities/alarm.entity';

@Injectable()
export class OrmAlarmRepository implements AlarmsRepository {
    constructor(
        @InjectRepository(AlarmEntity)
        private readonly alarmRepository: Repository<AlarmEntity>,
    ) {}

    async findAll(): Promise<Alarm[]> {
        return this.alarmRepository.find();
    }

    save: (Alarm) => Promise<Alarm> = (alarm: Alarm): Promise<Alarm> =>
        this.alarmRepository.save(alarm)
}
