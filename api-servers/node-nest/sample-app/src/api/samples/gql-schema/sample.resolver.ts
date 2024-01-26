import { Resolver, Query, Mutation, Args } from '@nestjs/graphql';
import { SampleService } from './sample.service';
import { CreateSampleInput } from './dto/create-sample.input';
import { UpdateSampleInput } from './dto/update-sample.input';

@Resolver('Sample')
export class SampleResolver {
  constructor(private readonly sampleService: SampleService) {}

  @Mutation('createSample')
  create(@Args('createSampleInput') createSampleInput: CreateSampleInput) {
    return this.sampleService.create(createSampleInput);
  }

  @Query('sample')
  findAll() {
    return this.sampleService.findAll();
  }

  @Query('sample')
  findOne(@Args('id') id: number) {
    return this.sampleService.findOne(id);
  }

  @Mutation('updateSample')
  update(@Args('updateSampleInput') updateSampleInput: UpdateSampleInput) {
    return this.sampleService.update(updateSampleInput.id, updateSampleInput);
  }

  @Mutation('removeSample')
  remove(@Args('id') id: number) {
    return this.sampleService.remove(id);
  }
}
