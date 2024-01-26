import { Column, Model, Table } from 'sequelize-typescript';

@Table
export class User extends Model {
  @Column
  id: number;
  @Column
  username: string;
  @Column
  password: string;
  @Column
  first_name: string;
  @Column
  last_name: string;
  @Column
  other_names: string;
  @Column({ defaultValue: true })
  is_active: boolean;

  constructor({ username, password, first_name, last_name, other_names }) {
    super();
    this.username = username;
    this.password = password;
    this.first_name = first_name;
    this.last_name = last_name;
    this.other_names = other_names;
  }
}
