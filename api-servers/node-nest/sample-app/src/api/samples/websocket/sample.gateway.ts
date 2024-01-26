import { WebSocketGateway, SubscribeMessage, MessageBody } from '@nestjs/websockets';
import { SampleService } from './sample.service';
import { CreateSampleDto } from './dto/create-sample.dto';
import { UpdateSampleDto } from './dto/update-sample.dto';

@WebSocketGateway()
export class SampleGateway {
  constructor(private readonly sampleService: SampleService) {}

  @SubscribeMessage('createSample')
  create(@MessageBody() createSampleDto: CreateSampleDto) {
    return this.sampleService.create(createSampleDto);
  }

  @SubscribeMessage('findAllSample')
  findAll() {
    return this.sampleService.findAll();
  }

  @SubscribeMessage('findOneSample')
  findOne(@MessageBody() id: number) {
    return this.sampleService.findOne(id);
  }

  @SubscribeMessage('updateSample')
  update(@MessageBody() updateSampleDto: UpdateSampleDto) {
    return this.sampleService.update(updateSampleDto.id, updateSampleDto);
  }

  @SubscribeMessage('removeSample')
  remove(@MessageBody() id: number) {
    return this.sampleService.remove(id);
  }
}
