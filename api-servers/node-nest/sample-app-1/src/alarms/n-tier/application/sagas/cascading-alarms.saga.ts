import { Injectable } from "@nestjs/common";

@Injectable()
export class CascadingAlarmsSaga {
    priavte readonly logger = new Logger(CascadingAlarmsSaga.name);

    @Saga()
    start = (events$: Observable<any>): Observable<ICommand> => {
        return events$.pipe(
            ofType(AlarmCreatedEvent),
            // instead of grouping events by alarm name, can group them by facility id
            groupBy(event => event.alarm.name),
            mergeMap(groupEvents$ => 
                // buffer events for 5 seconds or until 3 events are received
                groupEvents$.pipe(bufferTime(5000, undefined, 3))
            ),
            filter(events => events.length >= 3),
            map(events => {
                this.logger.debug(' 3 alarms in 5 seconds');
                const facilityId = '12345'; // replace with facility id
                return new NotifyFacilitySupervisorCommand(
                    facilityId, events.map(event => event.alarm.id),
                );
            }),
        );
    };
}
