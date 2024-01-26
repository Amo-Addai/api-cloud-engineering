import { Global, Module } from '@nestjs/common';
import { ConfigModule } from '@nestjs/config';
import { UtilsService } from './utils.service';
import { LoggerService } from './logger/logger.service';
import { LoggerMiddleware } from './middleware/logger/logger.middleware';
import { QueueProducerService } from './queues/producers/queue-producer.service';
import { QueueConsumerService } from './queues/consumers/queue-consumer.service';

@Global()
@Module({
  imports: [ConfigModule],
  providers: [
    UtilsService,
    LoggerMiddleware,
    LoggerService,
    QueueProducerService,
    QueueConsumerService,
  ],
  exports: [LoggerService],
})
export class UtilsModule {}
