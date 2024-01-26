/*
|--------------------------------------------------------------------------
| Routes
|--------------------------------------------------------------------------
|
| This file is dedicated for defining HTTP routes. A single file is enough
| for majority of projects, however you can define routes in different
| files and just make sure to import them inside this file. For example
|
| Define routes in following two files
| ├── start/routes/cart.ts
| ├── start/routes/customer.ts
|
| and then import them inside `start/routes.ts` as follows
|
| import './routes/cart'
| import './routes/customer'
|
*/

import Route from '@ioc:Adonis/Core/Route'
// Api Routes
import './routes/sample'
import './routes/user'

Route.get('/', async () => {
  return { hello: 'world' }
})

Route.get('/hello', async ({ request, response, auth, logger }) => {
  logger.log('url', request.url());
  logger.log('body + params', `${request.all()}`);
  logger.log('user', auth.user);
  response.send('hello world');
  // OR: return '' / '<p>..</p>' / { hello: 'world' } / new Date();
})
