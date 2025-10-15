import { AlarmSeverity } from './value-objects/alarm.severity';
import { AlarmItem } from './alarm-item';
import { SerializedEventPayload } from 'src/shared/domain/interfaces/serializable-event';
import { AlarmAcknowledgedEvent } from './events/alarm-acknowledged.event';

export class Alarm extends VersionedAggregateRoot {

    id: string;
    name: string;
    severity: AlarmSeverity;
    triggeredAt: Date;
    isAcknowledged = false;
    items = new Array<AlarmItem>();

    constructor(
        id: string,
        name: string,
        alarmSeverity: AlarmSeverity,
    ) {
        super();
        this.id = id;
        this.name = name;
        this.severity = alarmSeverity;
    }

    acknowledge() {
        this.isAcknowledged = true;
    }

    acknowledge_(){
        this.apply(new AlarmAcknowledgedEvent(this.id));
    }

    addAlarmItem(item: AlarmItem) {
        this.items.push(item);
    }

    onAlarmAcknowledgedEvent(
        event: SerializedEventPayload<AlarmAcknowledgedEvent>,
    ) {
        if (this.isAcknowledged) throw new Error('Alarm has already been acknowledged');
        this.isAcknowledged = true;
    }

    [`on${AlarmCreatedEvent.name}`](
        event: SerializedEventPayload<AlarmCreatedEvent>,
    ) {
        this.name = event.alarm.name;
        this.severity = new AlarmSeverity(event.alarm.severity);
        this.triggeredAt = new Date(event.alarm.triggeredAt);
        this.isAcknowledged = event.alarm.isAcknowledged;
        this.items = event.alarm.items.map(
            item => new AlarmItem(item.id, item.name, item.type),
        );
    }

    [`on${AlarmAcknowledgedEvent.name}`](
        event: SerializedEventPayload<AlarmAcknowledgedEvent>,
    ) {
        if (this.isAcknowledged) throw new Error('Alarm has already been acknowledged');
        this.isAcknowledged = true;
    }
}
