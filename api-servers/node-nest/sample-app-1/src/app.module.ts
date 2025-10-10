import { Module } from '@nestjs/common';
import { CqrsModule } from '@nestjs/cqrs';

import { AppController } from './app.controller';
import { AppService } from './app.service';
import { CoreModule } from './core/core.module';
import { ApplicationBootstrapOptions } from './common/interfaces/application-bootstrap-options.interface';
import { AlarmsModule } from './alarms/alarms.module';
import { AlarmsInfrastructureModule } from './alarms/n-tier/infrastructure/alarms-infrastructure.module';

@Module({
  imports: [CoreModule, CqrsModule.forRoot()], // removed: AlarmsModule, - CoreModule will interconnect all module dependencies
  controllers: [AppController],
  providers: [AppService],
})
export class AppModule {
  static register(options: ApplicationBootstrapOptions) {
    return {
      module: AppModule,
      imports: [
        CoreModule.forRoot(options),
        AlarmsModule.withInfrastructure(
          AlarmsInfrastructureModule.use(options.driver),
        ),
      ],
    };
  }
}
