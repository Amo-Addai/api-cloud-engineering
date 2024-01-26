// All Type ORM Entities

import { Sample as SampleREST } from '../../../api/samples/rest/entities/sample.entity';
import { Sample as SampleGQLCode } from '../../../api/samples/gql-code/entities/sample.entity';
import { Sample as SampleGQLSchema } from '../../../api/samples/gql-schema/entities/sample.entity';
import { Sample as SampleMicroService } from '../../../api/samples/microservice/entities/sample.entity';
import { Sample as SampleWebSocket } from '../../../api/samples/websocket/entities/sample.entity';
//
import { User as UserEntity } from '../../../api/users/entities/user.entity';

// All Sequelize Models

import { User as UserModel } from '../../../api/users/entities/user.model';

// All Mongoose Schemas

import { User as UserSchema } from '../../../api/users/entities/user.schema';

// All Exports

export const entities = {
  SampleREST,
  SampleGQLCode,
  SampleGQLSchema,
  SampleMicroService,
  SampleWebSocket,
  //
  UserEntity,
}; // typeorm

export const models = {
  UserModel,
}; // sequelize

export const schemas = {
  UserSchema,
}; // mongoose
