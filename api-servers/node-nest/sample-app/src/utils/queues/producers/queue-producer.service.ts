import { Injectable } from '@nestjs/common';
import { InjectQueue } from '@nestjs/bull';
import { Queue } from 'bull';

@Injectable()
export class QueueProducerService {
  constructor(@InjectQueue('queue') private queue: Queue) {}

  async addJob(name: string) {
    return this.queue.add(name, { job: 'data' });
  }
}
