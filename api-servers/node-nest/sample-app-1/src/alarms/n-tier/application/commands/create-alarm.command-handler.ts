import { CommandHandler, ICommandHandler } from '@nestjs/cqrs';
import { Logger } from '@nestjs/common';
import { CreateAlarmCommand } from './create-alarm.command';
import { AlarmFactory } from '../../domain/factories/alarm.factory';
import { AlarmsRepository } from '../ports/alarm.repository';
import { AlarmCreatedEvent } from '../../domain/events/alarm-created.event';

@CommandHandler(CreateAlarmCommand)
export class CreateAlarmCommandHandler implements ICommandHandler<CreateAlarmCommand> {

    private readonly logger = new Logger(CreateAlarmCommandHandler.name);

    constructor(
        private readonly alarmRepository: AlarmsRepository,
        private readonly alarmFactory: AlarmFactory,
        private readonly eventBus: EventBus,
        private readonly eventPublisher: EventPublisher,
    ) {}

    async execute(command: CreateAlarmCommand) {
        this.logger.debug(`Processing "CreateAlarmCommand": ${JSON.stringify(command)}`);
        const alarm = this.alarmFactory.create(command.name, command.severity, command.triggeredAt, command.items);
        return this.alarmRepository.save(alarm);
    }

    async execute_(command: CreateAlarmCommand) {
        this.logger.debug(`Processing "CreateAlarmCommand": ${JSON.stringify(command)}`)
        const alarm = this.alarmFactory.create(command.name, command.severity);
        const newAlarm = await this.alarmRepository.save(alarm);
        this.eventBus.publish(new AlarmCreatedEvent(alarm));
        return newAlarm;
    }

    async execute__(command: CreateAlarmCommand) {
        this.logger.debug(`Processing "CreateAlarmCommand": ${JSON.stringify(command)}`)
        const alarm = this.alarmFactory.create(command.name, command.severity, command.triggeredAt, command.items);
        this.eventPublisher.mergeObjectContext(alarm);
        alarm.commit();
        return alarm;
    }

}
