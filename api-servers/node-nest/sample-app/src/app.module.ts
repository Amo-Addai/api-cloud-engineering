import { APP_INTERCEPTOR, APP_GUARD } from '@nestjs/core';
import {
  Module,
  NestModule,
  MiddlewareConsumer,
  CacheModule,
  CacheInterceptor,
} from '@nestjs/common';
import { ConfigModule } from '@nestjs/config';
import { ThrottlerModule, ThrottlerGuard } from '@nestjs/throttler';
import { BullModule } from '@nestjs/bull';
import { TypeOrmModule } from '@nestjs/typeorm';
import { SequelizeModule } from '@nestjs/sequelize';
import { MongooseModule } from '@nestjs/mongoose';
import { AppController } from './app.controller';
import { AppService } from './app.service';
import config from './config/config';
import { UtilsModule } from './utils/utils.module';
import { AuthModule } from './auth/auth.module';
import { JwtAuthGuard } from './auth/utils/jwt-auth.guard';
import { LoggerMiddleware } from './utils/middleware/logger/logger.middleware';
import { entities /*, models, schemas */ } from './utils/db/entities/entities';
// Sample Modules
import { SampleModule as RESTModule } from './api/samples/rest/sample.module';
import { SampleModule as GQLCModule } from './api/samples/gql-code/sample.module';
import { SampleModule as GQLSModule } from './api/samples/gql-schema/sample.module';
import { SampleModule as MicroserviceModule } from './api/samples/microservice/sample.module';
import { SampleModule as WebSocketModule } from './api/samples/websocket/sample.module';
// Custom Modules
import { UsersModule } from './api/users/users.module';

@Module({
  imports: [
    ConfigModule.forRoot({
      isGlobal: true,
      load: [config],
      // envFilePath: ['.env.development.local', '.env.development'],
    }),
    CacheModule.register({ isGlobal: true }),
    ThrottlerModule.forRoot({
      ttl: 60,
      limit: 10,
    }),
    BullModule.forRoot('queues-config', {
      redis: {
        host: 'localhost',
        port: 6379,
      },
    }),
    TypeOrmModule.forRoot({
      type: 'mysql',
      host: 'localhost',
      port: 3306,
      username: 'root',
      password: 'root',
      database: 'test',
      entities: Object.values(entities),
      synchronize: true, // WARNING - Setting synchronize: true shouldn't be used in production - otherwise you can lose production data.
    }),
    SequelizeModule.forRoot({
      dialect: 'mysql',
      host: 'localhost',
      port: 3306,
      username: 'root',
      password: 'root',
      database: 'test',
      // models: Object.values(models),
      autoLoadModels: true,
      synchronize: true,
    }),
    MongooseModule.forRoot('mongodb://localhost/nest'),
    AuthModule,
    UtilsModule,
    // Sample Modules
    RESTModule,
    GQLCModule,
    GQLSModule,
    MicroserviceModule,
    WebSocketModule,
    // Custom Modules
    UsersModule,
  ],
  controllers: [AppController],
  providers: [
    AppService,
    {
      provide: APP_INTERCEPTOR,
      useClass: CacheInterceptor,
    },
    {
      provide: APP_GUARD,
      useClass: JwtAuthGuard,
    },
    {
      provide: APP_GUARD,
      useClass: ThrottlerGuard,
    },
  ],
})
export class AppModule implements NestModule {
  configure(consumer: MiddlewareConsumer) {
    consumer.apply(LoggerMiddleware);
  }
}
