import { Injectable } from '@nestjs/common';
import { CreateSampleDto } from './dto/create-sample.dto';
import { UpdateSampleDto } from './dto/update-sample.dto';

@Injectable()
export class SampleService {
  create(createSampleDto: CreateSampleDto) {
    return `This action adds a new sample - ${createSampleDto}`;
  }

  findAll() {
    return `This action returns all sample`;
  }

  findOne(id: number) {
    return `This action returns a #${id} sample`;
  }

  update(id: number, updateSampleDto: UpdateSampleDto) {
    return `This action updates a #${id} sample - ${updateSampleDto}`;
  }

  remove(id: number) {
    return `This action removes a #${id} sample`;
  }
}
