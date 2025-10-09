import { Module } from '@nestjs/common';
import { TypeOrmModule } from '@nestjs/typeorm';
import { ApplicationBootstrapOptions } from '../common/interfaces/application-bootstrap-options.interface';

@Module({})
export class CoreModule {
    static forRoot(options: ApplicationBootstrapOptions) {
        const imports = options.driver == 'orm'
            ? [
                // hardcoding db connection options
                TypeOrmModule.forRoot({
                    type: 'postgres',

                }),
            ]
            : [];
        return {
            module: CoreModule,
            imports,
        };
    }
}
