import { Module } from '@nestjs/common';
import { PassportModule } from '@nestjs/passport';
import { JwtModule } from '@nestjs/jwt';
import { UsersModule } from 'src/api/users/users.module';
import { AuthService } from './auth.service';
import { LocalStrategy } from './utils/local.strategy';
import { constants } from './utils/constants';
import { JwtStrategy } from './utils/jwt.strategy';
import { UtilsModule } from 'src/utils/utils.module';

@Module({
  imports: [
    UtilsModule,
    UsersModule,
    PassportModule,
    JwtModule.register({
      secret: constants.jwtSecret,
      signOptions: { expiresIn: '60s' },
    }),
  ],
  providers: [AuthService, LocalStrategy, JwtStrategy],
  exports: [AuthService],
})
export class AuthModule {}
