import { Injectable } from '@nestjs/common';

@Injectable()
export class AggregateRehydrator {
    constructor(
        private readonly eventStore: EventStore,
        private readonly eventPublisher: EventPublisher,
    ) {}

    async rehydrate<T extends VersionedAggregateRoot>(
        aggregateId: string,
        AggregateCls: Type<T>,
    ): Promise<T> {
        const events = await this.eventStore.getEventsByStreamId(aggregateId);
        const AggregatedClsWithDispatcher = this.eventPublisher.mergeClassContext(AggregateCls);
        const aggregate = new AggregatedClsWithDispatcher(aggregateId);
        aggregate.loadFromHistory(events);
        return aggregate;
    }
}
