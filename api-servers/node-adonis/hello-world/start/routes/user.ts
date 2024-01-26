import Route from '@ioc:Adonis/Core/Route'


// full resource (CRUD - index, create, store, show, edit, update, destroy)
Route.resource('users', 'UsersController')
Route.resource('users.actions', 'ActionsController') // nested resource
Route.resource('users', 'UsersController')
    .as('investors') // named route
    .only(['index', 'show', 'store']) // allowed actions
    .except(['update', 'destroy']) // restricted actions
    .apiOnly() // remove view routes (only api routes allowed)
    .middleware({ '*': ['auth'], }) // auth middleware
    .middleware({ create: ['auth'], destroy: ['auth'], })



// Route.get('/users', 'UsersController.index')
Route.get('/users', () => {
    return 'List all users'
})

Route.get('/users/create', () => {
    return 'Display a form to create a user'
})

Route.post('/users', async () => {
    return 'Handle user creation form request'
})

Route.get('/users/:id', () => {
    return 'Return a single user'
})

Route.get('/users/:id/edit', () => {
    return 'Display a form to edit a user'
})

Route.put('/users/:id', () => {
    return 'Handle user update form submission'
})

Route.delete('/users/:id', () => {
    return 'Delete user'
})
