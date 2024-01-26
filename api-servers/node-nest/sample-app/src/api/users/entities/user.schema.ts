import { Prop, Schema, SchemaFactory } from '@nestjs/mongoose';
import { HydratedDocument } from 'mongoose';

export type UserDocument = HydratedDocument<User>;

@Schema()
export class User {
  @Prop()
  id: number;
  @Prop()
  username: string;
  @Prop()
  password: string;
  @Prop()
  first_name: string;
  @Prop()
  last_name: string;
  @Prop()
  other_names: string;
  @Prop({ default: true })
  is_active: boolean;

  constructor({ username, password, first_name, last_name, other_names }) {
    this.username = username;
    this.password = password;
    this.first_name = first_name;
    this.last_name = last_name;
    this.other_names = other_names;
  }
}

export const UserSchema = SchemaFactory.createForClass(User);
