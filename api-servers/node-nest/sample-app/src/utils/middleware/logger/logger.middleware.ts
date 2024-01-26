import { Injectable, NestMiddleware } from '@nestjs/common';
import { LoggerService } from '../../logger/logger.service';

@Injectable()
export class LoggerMiddleware implements NestMiddleware {
  private log;

  constructor(private loggerService: LoggerService) {
    this.log = this.loggerService.createLogger('logger-middleware');
  }

  use(req: any, res: any, next: () => void) {
    this.log(`Request...`);
    next();
  }
}
