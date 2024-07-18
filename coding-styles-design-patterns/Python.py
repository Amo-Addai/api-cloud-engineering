
import asyncio

# TODO: NB: 

''' # TODO: To-Use

Generics
..

'''


########################################
##  CODING STYLES (ascending)
########################################

# TODO: Use module constructs in-file

class Functional:

    users = [
        {'id': 1, 'name': 'Alice', 'age': 25},
        {'id': 2, 'name': 'Bob', 'age': 30},
    ]

    def get_user_by_id(user_id):
        return next((user for user in users if user['id'] == user_id), None)

    def get_all_user_names():
        return [user['name'] for user in users]

    print(get_user_by_id(1))  # {'id': 1, 'name': 'Alice', 'age': 25}
    print(get_all_user_names())  # ['Alice', 'Bob']


class Declarative:

    numbers = [1, 2, 3, 4, 5]
    doubled = list(map(lambda x: x * 2, numbers))

    print(doubled)  # [2, 4, 6, 8, 10]


class OOP:

    class User:
        def __init__(self, user_id, name, age):
            self.id = user_id
            self.name = name
            self.age = age

        def get_details(self):
            return f"{self.name}, Age: {self.age}"

    user = User(1, 'Alice', 25)
    print(user.get_details())  # Alice, Age: 25


class AsyncAwait:

    async def fetch_user_data(user_id):
        await asyncio.sleep(1)  # Simulate a network call
        return {'id': user_id, 'name': 'Alice', 'age': 25}

    async def display_user(user_id):
        user = await fetch_user_data(user_id)
        print(user)

    asyncio.run(display_user(1))  # {'id': 1, 'name': 'Alice', 'age': 25}


class Callback:

    def read_file(file_path, callback):
        try:
            with open(file_path, 'r') as file:
                data = file.read()
            callback(None, data)
        except Exception as e:
            callback(e, None)

    def print_file_contents(error, data):
        if error:
            print(f"Error: {error}")
        else:
            print(data)

    read_file('example.txt', print_file_contents)


class ObjectComposition:

    class EatMixin:
        def eat(self):
            print("Eating...")

    class WalkMixin:
        def walk(self):
            print("Walking...")

    class Person(EatMixin, WalkMixin):
        pass

    person = Person()
    person.eat()  # Eating...
    person.walk()  # Walking...


########################################
##  DESIGN PATTERNS
########################################

# * Creational

class Prototype:
    
    class User:
        def __init__(self, user_id, name, age):
            self.id = user_id
            self.name = name
            self.age = age

        def clone(self):
            return User(self.id, self.name, self.age)

    user = User(1, 'Alice', 25)
    user_clone = user.clone()
    print(user_clone.get_details())  # Alice, Age: 25


# * Structural


# * Behavioral


# * Others

class Module:

    class UserModule:
        _users = []

        @staticmethod
        def add_user(user):
            UserModule._users.append(user)

        @staticmethod
        def get_user(user_id):
            return next((user for user in UserModule._users if user['id'] == user_id), None)

    UserModule.add_user({'id': 1, 'name': 'Alice', 'age': 25})
    print(UserModule.get_user(1))  # {'id': 1, 'name': 'Alice', 'age': 25}


class Middleware:

    class Middleware:
        def __init__(self):
            self._middlewares = []

        def use(self, middleware):
            self._middlewares.append(middleware)

        async def execute(self, context):
            for middleware in self._middlewares:
                await middleware(context)

    async def logger(context, next_middleware):
        print(f"Logging: {context['request']}")
        await next_middleware(context)

    async def handler(context):
        print(f"Handling: {context['request']}")

    middleware = Middleware()
    middleware.use(logger)
    middleware.use(handler)

    context = {'request': 'GET /home'}
    asyncio.run(middleware.execute(context))


########################################
##  OTHER PATTERNS
########################################

class EventDriven:

    class EventEmitter:
        def __init__(self):
            self._events = {}

        def on(self, event, callback):
            if event not in self._events:
                self._events[event] = []
            self._events[event].append(callback)

        def emit(self, event, *args):
            if event in self._events:
                for callback in self._events[event]:
                    asyncio.create_task(callback(*args))

    async def user_created(user):
        print(f"User created: {user['name']}")

    event_emitter = EventEmitter()
    event_emitter.on('user_created', user_created)
    event_emitter.emit('user_created', {'name': 'Alice'})




########################################
##  TEST CASES
########################################

if __name__ == "__main__":
    print("Hello, World!")

