import { CACHE_MANAGER, Inject, Injectable } from '@nestjs/common';
import { Cache } from 'cache-manager';

@Injectable()
export class AppService {
  constructor(
    @Inject(CACHE_MANAGER)
    private cacheManager: Cache,
  ) {}

  getHello(): string {
    return 'Hello World!';
  }

  async getFromCache(key: string): Promise<any> {
    return await this.cacheManager.get(key);
  }

  async setInCache(key: string, value: any, expiresIn = 0) {
    await this.cacheManager.set(key, value, expiresIn);
  }

  async removeFromCache(key: string) {
    await this.cacheManager.del(key);
  }

  async clearCache() {
    await this.cacheManager.reset();
  }
}
