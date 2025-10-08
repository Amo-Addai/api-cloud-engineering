import { Alarm } from '../../../../domain/alarm';
import { AlarmSeverity } from '../../../../domain/value-objects/alarm.severity';
import { AlarmEntity } from '../entities/alarm.entity';

export class AlarmMapper {
    static toDomain(alarmEntity: AlarmEntity): Alarm {
        const alarmSeverity = new AlarmSeverity(
            alarmEntity.severity as 'critical' | 'low' | 'medium' | 'high',
        );
        const alarmModel = new Alarm(
            alarmEntity.id,
            alarmEntity.name,
            alarmSeverity,
        );
        return alarmModel
    }
}
