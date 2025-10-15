import { Logger } from '@nestjs/common';
import { CommandHandler, ICommandHandler } from '@nestjs/cqrs';
import { AggregateRehydrator } from 'src/shared/application/aggregate-rehydrator';
import { AcknowledgeAlarmCommand } from './acknowledge-alarm.command';

@CommandHandler(AcknowledgeAlarmCommand)
export class AcknowledgeAlarmCommandHandler implements ICommandHandler<AcknowledgeAlarmCommand> {
    private readonly logger = new Logger(AcknowledgeAlarmCommandHandler.name);

    constructor(private readonly aggregatedRehydrator: AggregateRehydrator) {}

    async execute(command: AcknowledgeAlarmCommand) {
        this.logger.debug(
            `Processing "AcknowledgeAlarmCommand": ${JSON.stringify(command)}`,
        );
        const alarm = await this.aggregatedRehydrator.rehydrate(
            command.alarmId, Alarm,
        );
        alarm.acknowledge(); alarm.commit();
        return alarm;
    }
}
