import { Injectable, Logger } from '@nestjs/common';
import { Saga, ICommand, ofType } from '@nestjs/cqrs';
import { Observable } from 'rxjs';
import { AlarmAcknowledgedEvent } from '../../domain/events/alarm-acknowledged.event';
import { AlarmCreatedEvent } from '../../domain/events/alarm-created.event';
import { NotifyFacilitySupervisorCommand } from '../commands/notify-facility-supervisor.command';

@Injectable()
export class UnknowledgedAlarmsSaga {
    private readonly logger = new Logger(UnacknowledgedAlarmsSaga.name);

    @Saga()
    start = (events$: Observable<any>): Observable<ICommand> => {
        // a stream of alarm acknowledged events
        const alarmAcknowledgedEvents$ = events$.pipe(
            ofType(AlarmAcknowledgedEvent),
        );
        // a stream of alarm created events
        const alarmCreatedEvents$ = events$.pipe(ofType(AlarmCreatedEvent));

        return alarmCreatedEvents$.pipe(
            // wait for an alarm to be acknowledged or 10 seconds to pass
            mergeMap(AlarmCreatedEvent =>
                race(
                    alarmAcknowledgedEvents$.pipe(
                        filter(
                            alarmAcknowledgedEvent =>
                                alarmAcknowledgedEvent.alarmId === AlarmCreatedEvent.alarm.id,
                        ),
                        first(),
                        // if the alarm is acknowledged, we don't need to do anything
                        // just return an empty observable that never emits
                        mergeMap(() => EMPTY),
                    ),
                    timer(15000).pipe(map(() => alarmCreatedEvent)),
                ),
            ),
        );
    };
}
