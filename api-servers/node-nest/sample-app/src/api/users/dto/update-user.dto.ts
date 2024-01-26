import { PartialType } from '@nestjs/mapped-types';
import { CreateUserDto } from './create-user.dto';

export class UpdateUserDto extends PartialType(CreateUserDto) {
  username: string;
  password: string;
  first_name: string;
  last_name: string;
  other_names: string;
}

export function validateUpdateUserDto(dto: UpdateUserDto) {
  const data = {
    username: dto.username,
    password: dto.password,
    first_name: dto.first_name,
    last_name: dto.last_name,
    other_names: dto.other_names,
  };
  return data;
}
