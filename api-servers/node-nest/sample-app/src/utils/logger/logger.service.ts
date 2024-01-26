import { Injectable } from '@nestjs/common';
import { ConfigService } from '@nestjs/config';
import debug from 'debug';

@Injectable()
export class LoggerService {
  constructor(private configService: ConfigService) {}

  createLogger(loggerName: string) {
    return debug(`${this.configService.get<string>('app_name')}:${loggerName}`);
  }
}
