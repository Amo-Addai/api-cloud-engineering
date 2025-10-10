import { Alarm } from '../../../../domain/alarm';
import { AlarmSeverity } from '../../../../domain/value-objects/alarm.severity';
import { AlarmEntity } from '../entities/alarm.entity';
import { AlarmItemEntity } from '../entities/alarm-item.entity';
import { AlarmItem } from '../../../../domain/alarm-item';

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
        alarmModel.name = alarmEntity.name;
        alarmModel.isAcknowledged = alarmEntity.isAcknowledged;
        alarmModel.severity = alarmSeverity;
        alarmModel.triggeredAt = alarmEntity.triggeredAt;
        alarmModel.items = alarmEntity.items.map(
            item => new AlarmItem(item.id, item.name, item.type)
        )
        return alarmModel
    }

    static toPersistence: (Alarm) => AlarmEntity = (
        alarm: Alarm,
        [entity, itemEntity]: any[] = []
    ) => (
        entity = new AlarmEntity(),
        entity.id = alarm.id,
        entity.name = alarm.name,
        entity.severity = alarm.severity.value,
        entity.isAcknowdledged = alarm.isAcknowledged,
        entity.triggeredAt = alarm.triggeredAt,
        entity.items = alarm.items.map(item => (
            itemEntity = new AlarmItemEntity(),
            itemEntity.id = item.id,
            itemEntity.name = item.name,
            itemEntity.type = item.type,
            itemEntity
        )),
        entity
    )
}
