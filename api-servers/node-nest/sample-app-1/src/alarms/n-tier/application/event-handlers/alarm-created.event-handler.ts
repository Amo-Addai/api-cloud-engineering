import { Logger } from '@nestjs/common';
import { EventsHandler, IEventHandler } from '@nestjs/cqrs';
import { AlarmCreatedEvent } from '../../domain/events/alarm-created.event';

@EventsHnadler(AlarmCreatedEvent)
export class AlarmCreatedEventHandler implements IEventHandler<AlarmCreatedEvent> {
    private readonly logger = new Logger(AlarmCreatedEventHandler.name);

    constructor(
        private readonly upsertMaterializedAlarmRepository: UpsertMaterializedAlarmRepository,
    ) {}

    async handle(event: AlarmCreatedEvent) {
        this.logger.log(`Alarm created event: ${JSON.stringify(event)}`);

        /**
         * in a real world application,
         * we would have to ensure that this operation is atomic
         * in the read model (eg. because the data operation may fail)
         * @for more info: Transactional inbox / outbox pattern
         */
        await this.upsertMaterializedAlarmRepository.upsert({
            id: event.alarm.id,
            name: event.alarm.name,
            severity: event.alarm.severity.value,
            triggeredAt: event.alarm.triggeredAt,
            isAcknowledged: event.alarm.isAcknowledged,
            items: event.alarm.items,
        });
    }
}
