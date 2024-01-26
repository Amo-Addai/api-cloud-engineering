import { Entity, Column, PrimaryGeneratedColumn } from 'typeorm';

@Entity()
export class Sample {
  @PrimaryGeneratedColumn()
  id: number;
  @Column()
  name: string;
  @Column()
  details: string;

  constructor({ name, details }) {
    this.name = name;
    this.details = details;
  }
}
