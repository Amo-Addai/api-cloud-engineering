import { AlarmSeverity } from './value-objects/alarm.severity';
import { AlarmItem } from './alarm-item';

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

    addAlarmItem(item: AlarmItem) {
        this.items.push(item);
    }

}
