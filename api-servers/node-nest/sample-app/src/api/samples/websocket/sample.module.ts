import { Module } from '@nestjs/common';
import { SampleService } from './sample.service';
import { SampleGateway } from './sample.gateway';

@Module({
  providers: [SampleGateway, SampleService],
})
export class SampleModule {}
