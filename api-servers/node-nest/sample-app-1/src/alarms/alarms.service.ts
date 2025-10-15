import { Injectable } from '@nestjs/common';
import { CreateAlarmDto } from './dto/create-alarm.dto';
import { UpdateAlarmDto } from './dto/update-alarm.dto';
import { AlarmFactory } from '../domain/factories/alarm.factory';
import { AlarmRepository } from './ports/alarm.repository';
import { CreateAlarmCommand } from './n-tier/application/commands/create-alarm.command';
import { GetAlarmsQuery } from './queries/get-alarms.query';

@Injectable()
export class AlarmsService {

  constructor(
    private readonly alarmRepository: AlarmRepository,
    private readonly alarmFactory: AlarmFactory,
    private readonly commandBus: CommandBus,
    private readonly queryBus: QueryBus,
  ) {}

  create(createAlarmDto: CreateAlarmDto) {
    return 'This action adds a new alarm';
  }
  
  createCommand(createAlarmCommand: CreateAlarmCommand) {
    const alarm = this.alarmFactory.create(
      createAlarmCommand.name,
      createAlarmCommand.severity,
    );
    return this.alarmRepository.save(alarm);
  }

  createCommand_(createAlarmCommand: CreateAlarmCommand) {
    return this.commandBus.execute(createAlarmCommand);
  }

  findAll() {
    return `This action returns all alarms`;
  }

  findAllCommand() {
    return this.alarmRepository.findAll();
  }

  findAllCommand_() {
    return this.queryBus.execute(new GetAlarmsQuery())
  }

  findOne(id: number) {
    return `This action returns a #${id} alarm`;
  }

  update(id: number, updateAlarmDto: UpdateAlarmDto) {
    return `This action updates a #${id} alarm`;
  }

  remove(id: number) {
    return `This action removes a #${id} alarm`;
  }

  acknowledge(id: string) {
    return this.commandBus.execute(new AcknowledgeAlarmCommand(id));
  }
}
