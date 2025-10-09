import { Injectable } from '@nestjs/common';
import { CreateAlarmDto } from './dto/create-alarm.dto';
import { UpdateAlarmDto } from './dto/update-alarm.dto';
import { CreateAlarmCommand } from './n-tier/commands/create-alarm.command';
import { AlarmFactory } from '../domain/factories/alarm.factory';
import { AlarmRepository } from './ports/alarm.repository';

@Injectable()
export class AlarmsService {

  constructor(
    private readonly alarmRepository: AlarmRepository,
    private readonly alarmFactory: AlarmFactory,
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

  findAll() {
    return `This action returns all alarms`;
  }

  findAllCommand() {
    return this.alarmRepository.findAll();
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
}
