import { Test, TestingModule } from '@nestjs/testing';
import { CreateUserDto } from './dto/create-user.dto';
import { UpdateUserDto } from './dto/update-user.dto';
import { UsersService } from './users.service';

describe('UsersService', () => {
  let service: UsersService;

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      providers: [UsersService],
    }).compile();

    service = module.get<UsersService>(UsersService);
  });

  it('should be defined', () => {
    expect(service).toBeDefined();
  });

  it('should create a new user', () => {
    const createUserDto: CreateUserDto = {
      username: 'user',
      password: 'pass',
      first_name: 'Test',
      last_name: 'User',
      other_names: '1',
    };
    const user = service.create(createUserDto);
    expect(user).toBeDefined();
  });

  it('should find all users', () => {
    const users = service.findAll();
    expect(users).toBeDefined();
  });

  it('should find a specific user', () => {
    const id = 1;
    const user = service.findOne(id);
    expect(user).toBeDefined();
  });

  it('should update an existing user', () => {
    const id = 1;
    const updateUserDto: UpdateUserDto = {
      username: 'user',
      password: 'pass',
      first_name: 'Test',
      last_name: 'User',
      other_names: '1',
    };
    const user = service.update(id, updateUserDto);
    expect(user).toBeDefined();
  });

  it('should remove an existing user', () => {
    const id = 1;
    const user = service.remove(id);
    expect(user).toBeUndefined(); //
  });
});
