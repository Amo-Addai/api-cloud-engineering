import { AlarmSeverity } from './value-objects/alarm.severity'

export class Alarm {

    id: string;
    name: string;
    severity: AlarmSeverity;

    constructor(
        id: string,
        name: string,
        alarmSeverity: AlarmSeverity,
    ) {
        this.id = id;
        this.name = name;
        this.severity = alarmSeverity;
    }
}
