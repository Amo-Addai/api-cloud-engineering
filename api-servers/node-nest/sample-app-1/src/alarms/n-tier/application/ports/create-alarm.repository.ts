import { Alarm } from '../../domain/alarm'

export abstract class CreateAlarmsRepository {
    abstract save(alarm: Alarm): Promise<Alarm>;
}
