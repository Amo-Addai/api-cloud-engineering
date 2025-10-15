
export class AlarmSeverity {

    value: 'critical' | 'low' | 'medium' | 'high' // union type

    constructor(
        alarmSeverity: 'critical' | 'low' | 'medium' | 'high',
    ) {
        this.value = alarmSeverity;
    }

    equals(severity: AlarmSeverity) {
        return this.value === severity.value;
    }

    toJSON() {
        return this.value;
    }
}
