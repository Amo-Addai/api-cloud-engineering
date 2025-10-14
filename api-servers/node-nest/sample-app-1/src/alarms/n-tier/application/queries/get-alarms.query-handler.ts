import { IQueryHandler, QueryHandler } from '@nestjs/cqrs';
import { Alarm } from '../../domain/alarm';
import { GetAlarmsQuery } from './get-alarms.query';
import { AlarmsRepository } from '../ports/alarms.repository';
import { FindAlarmsRepository } from '../ports/find-alarms.repository';

@QueryHandler(GetAlarmsQuery)
export class GetAlarmsQueryHandler implements IQueryHandler<GetAlarmsQuery, Alarm[]> {

    constructor(
        private readonly alarmsRepository: AlarmsRepository,
        private readonly findAlarmsRepository: FindAlarmsRepository,
    ) {}

    async execute(query: GetAlarmsQuery): Promise<Alarm[]> {
        return this.alarmsRepository.findAll();
    }

    async execute_(query: GetAlarmsQuery): Promise<AlarmReadModel[]> {
        return this.findAlarmsRepository.findAll();
    }

}
