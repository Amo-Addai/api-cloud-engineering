import { Controller } from '@nestjs/common';
import { MessagePattern, Payload } from '@nestjs/microservices';
import { SampleService } from './sample.service';
import { CreateSampleDto } from './dto/create-sample.dto';
import { UpdateSampleDto } from './dto/update-sample.dto';

@Controller()
export class SampleController {
  constructor(private readonly sampleService: SampleService) {}

  @MessagePattern('createSample')
  create(@Payload() createSampleDto: CreateSampleDto) {
    return this.sampleService.create(createSampleDto);
  }

  @MessagePattern('findAllSample')
  findAll() {
    return this.sampleService.findAll();
  }

  @MessagePattern('findOneSample')
  findOne(@Payload() id: number) {
    return this.sampleService.findOne(id);
  }

  @MessagePattern('updateSample')
  update(@Payload() updateSampleDto: UpdateSampleDto) {
    return this.sampleService.update(updateSampleDto.id, updateSampleDto);
  }

  @MessagePattern('removeSample')
  remove(@Payload() id: number) {
    return this.sampleService.remove(id);
  }
}
