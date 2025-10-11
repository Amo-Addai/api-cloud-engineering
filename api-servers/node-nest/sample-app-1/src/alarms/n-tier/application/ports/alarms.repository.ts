import { Alarm } from '../../domain/alarm'

export abstract class AlarmsRepository {
    abstract findAll(): Promise<Alarm[]>;
    abstract save(alarm: Alarm): Promise<Alarm>;
}

export abstract class CreateAlarmsRepository {
    abstract save(alarm: Alarm): Promise<Alarm>;
}