import { CreateSampleInput } from './create-sample.input';
import { PartialType } from '@nestjs/mapped-types';

export class UpdateSampleInput extends PartialType(CreateSampleInput) {
  id: number;
}
