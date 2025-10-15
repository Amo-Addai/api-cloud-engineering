import { Module } from '@nestjs/common';
import { TypeOrmModule } from '@nestjs/typeorm';
import { MongooseModule } from '@nestjs/mongoose';
import { ApplicationBootstrapOptions } from '../common/interfaces/application-bootstrap-options.interface';
import { EVENT_STORE_CONNECTION } from './core.constants';

@Module({
    imports: [
        MongooseModule.forRoot('mongodb://localhost:27018/vf-event-store', {
            connectionName: EVENT_STORE_CONNECTION,
            directConnection: true,
        })
    ]
})
export class CoreModule {
    static forRoot(options: ApplicationBootstrapOptions) {
        const imports = options.driver == 'orm'
            ? [
                // hardcoding db connection options
                TypeOrmModule.forRoot({
                    type: 'postgres',
                    host: 'localhost',
                    port: 5432,
                    password: 'pass123',
                    username: 'postgres',
                    autoLoadEntities: true,
                    synchronize: true,
                }),
                MongooseModule.forRoot('mongodb://uri_'),
            ]
            : [];
        return {
            module: CoreModule,
            imports,
        };
    }
}
