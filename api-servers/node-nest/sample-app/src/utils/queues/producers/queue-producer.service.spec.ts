import { Test, TestingModule } from '@nestjs/testing';
import { QueueProducerService } from './queue-producer.service';

describe('QueueProducerService', () => {
  let service: QueueProducerService;

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      providers: [QueueProducerService],
    }).compile();

    service = module.get<QueueProducerService>(QueueProducerService);
  });

  it('should be defined', () => {
    expect(service).toBeDefined();
  });
});
