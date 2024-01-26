import { Processor, Process, OnQueueActive } from '@nestjs/bull';
import { Job } from 'bull';

@Processor('queue')
export class QueueConsumerService {
  @Process()
  async transcode(job: Job<unknown>) {
    let progress = 0;
    // this should probably be a do / while loop
    for (let i = 0; i < 100; i++) {
      await doSth(job.data);
      progress += 1;
      await job.progress(progress);
    }
    return {};
  }

  @OnQueueActive() // Event Listener
  onActive(job: Job) {
    console.log(
      `Processing job ${job.id} of type ${job.name} with data ${job.data}...`,
    );
  }
}

async function doSth(data: unknown) {
  throw new Error(`Function not implemented. ${data}`);
}
