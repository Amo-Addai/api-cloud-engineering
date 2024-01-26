import { Test, TestingModule } from '@nestjs/testing';
import { QueueConsumerService } from './queue-consumer.service';

describe('QueueConsumerService', () => {
  let service: QueueConsumerService;

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      providers: [QueueConsumerService],
    }).compile();

    service = module.get<QueueConsumerService>(QueueConsumerService);
  });

  it('should be defined', () => {
    expect(service).toBeDefined();
  });
});
