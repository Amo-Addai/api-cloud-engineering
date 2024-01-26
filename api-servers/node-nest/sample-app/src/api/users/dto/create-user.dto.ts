export class CreateUserDto {
  username: string;
  password: string;
  first_name: string;
  last_name: string;
  other_names: string;
}

export function validateCreateUserDto(dto: CreateUserDto) {
  const data = {
    username: dto.username,
    password: dto.password,
    first_name: dto.first_name,
    last_name: dto.last_name,
    other_names: dto.other_names,
  };
  return data;
}
