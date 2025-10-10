import { Module } from '@nestjs/common';
import { TypeOrmModule } from '@nestjs/typeorm';
import { MongooseModule } from '@nestjs/mongoose';
import { ApplicationBootstrapOptions } from '../common/interfaces/application-bootstrap-options.interface';

@Module({})
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
