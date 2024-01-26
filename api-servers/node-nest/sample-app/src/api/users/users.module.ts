import { Module } from '@nestjs/common';
// import { TypeOrmModule, getRepositoryToken } from '@nestjs/typeorm';
// import { SequelizeModule, getModelToken } from '@nestjs/sequelize';
import { MongooseModule, getModelToken } from '@nestjs/mongoose';
import { UsersService } from './users.service';
import { UsersController } from './users.controller';
import { UserSubscriber } from './subscriptions/user.subscriber';
// import { User } from './entities/user.entity'; // typeorm
// import { User } from './entities/user.model'; // sequelize
import { User, UserSchema } from './entities/user.schema'; // mongoose

@Module({
  imports: [
    // TypeOrmModule.forFeature([User]), // typeorm
    // SequelizeModule.forFeature([User]), // sequelize
    MongooseModule.forFeature([{ name: User.name, schema: UserSchema }]), // mongoose
  ],
  controllers: [UsersController],
  providers: [
    UsersService,
    UserSubscriber,
    /* // setting up mock data, for testing
    {
      provide: getRepositoryToken(User), // typeorm
      useValue: {}, // mockRepository
    },
    {
      provide: getModelToken(User), // sequelize
      useValue: {}, // mockModel
    },
    */
    {
      provide: getModelToken(User.name), // mongoose
      useValue: {}, // mockModel
    },
  ],
  exports: [
    UsersService,
    // TypeOrmModule,
    // SequelizeModule,
    MongooseModule,
  ],
})
export class UsersModule {}
