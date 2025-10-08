
export class AlarmSeverity {

    value: 'critical' | 'low' | 'medium' | 'high' // union type

    constructor(
        alarmSeverity: 'critical' | 'low' | 'medium' | 'high',
    ) {
        this.value = alarmSeverity;
    }
}
