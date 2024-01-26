import { Injectable } from '@nestjs/common';
import { InjectModel } from '@nestjs/mongoose';
import { Model } from 'mongoose';
// import { InjectRepository } from '@nestjs/typeorm';
// import { Repository } from 'typeorm';
// import { InjectModel } from '@nestjs/sequelize';
// import { User, Prisma } from '@prisma/client';
import { CreateUserDto, validateCreateUserDto } from './dto/create-user.dto';
import { UpdateUserDto, validateUpdateUserDto } from './dto/update-user.dto';
import { User, UserDocument } from './entities/user.schema'; // mongoose
// import { User } from './entities/user.entity'; // typeorm
// import { User } from './entities/user.model'; // sequelize
// import { PrismaService } from '../../utils/db/prisma/prisma.service'; // prisma

@Injectable()
export class UsersService {
  constructor(
    @InjectModel(User.name)
    private userModel: Model<UserDocument>, // mongoose
  ) {}
  /* 
  @InjectRepository(User)
  private usersRepository: Repository<User>, // typeorm
  @InjectModel(User)
  private userModel: typeof User, // sequelize
  private prisma: PrismaService, // prisma
  */

  async create(createUserDto: CreateUserDto) {
    const data = validateCreateUserDto(createUserDto);
    const user = new User(data); // test User() for each ORM
    await this.userModel.create(user); // mongoose
    // await this.usersRepository.save(user); // typeorm
    // sequelize
    // this.prisma.user.create({ data }); // prisma
    // const data: Prisma.UserCreateInput)
    return 'This action adds a new user';
  }

  findAll(): Promise<User[]> {
    return this.userModel.find().exec(); // mongoose
    // return this.usersRepository.find(); // typeorm
    // return this.userModel.findAll(); // sequelize
    // this.prisma.user.findMany({ skip, take, cursor, where, orderBy }); // prisma
    // const skip?: number, take?: number, cursor?: Prisma.UserWhereUniqueInput,
    // where?: Prisma.UserWhereInput, orderBy?: Prisma.UserOrderByWithRelationInput
  }

  findOne(filter: any) {
    return this.userModel.findOne(filter).exec(); // mongoose
    // return this.usersRepository.findOneBy(filter); // typeorm
    // return this.userModel.findOne({ where: filter }); // sequelize
    // this.prisma.user.findUnique({ where: userWhereUniqueInput }); // prisma
    // const userWhereUniqueInput: Prisma.UserWhereUniqueInput
  }

  findById(id: number): Promise<User> {
    return this.userModel.findOne({ _id: id }).exec(); // mongoose
    // return this.usersRepository.findOneBy({ id }); // typeorm
    // return this.userModel.findOne({ where: { id } }); // sequelize
    // this.prisma.user.findUnique({ where: userWhereUniqueInput }); // prisma
    // const userWhereUniqueInput: Prisma.UserWhereUniqueInput
  }

  async update(id: number, updateUserDto: UpdateUserDto) {
    const data = validateUpdateUserDto(updateUserDto);
    let user;
    user = this.userModel.findOne({ _id: id }).exec(); // mongoose
    // user = this.usersRepository.findOneBy({ id }); // typeorm
    // user = this.userModel.findOne({ where: { id } }); // sequelize
    // this.prisma.user.findUnique({ where: userWhereUniqueInput }); // prisma
    // const userWhereUniqueInput: Prisma.UserWhereUniqueInput
    user = Object.assign(user, data);
    user.save(); // mongoose
    // await this.usersRepository.save(await user); // typeorm
    // sequelize
    // this.prisma.user.update({ data, where }); // prisma
    // const where: Prisma.UserWhereUniqueInput, data: Prisma.UserUpdateInput
    return `This action updates an existing user`;
  }

  async remove(id: number): Promise<void> {
    await this.userModel.findByIdAndRemove({ _id: id }).exec(); // mongoose
    // await this.usersRepository.delete(id); // typeorm
    // await (await this.userModel.findOne({ where: { id } })).destroy(); // sequelize
    // await this.prisma.user.delete({ where }); // prisma
    // const where: Prisma.UserWhereUniqueInput
  }
}
