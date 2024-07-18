'use strict'

// * install globally
import * as R from 'ramda'
import * as RA from 'ramda-adjunct'

/* // TODO: To-Use

Generics
..

*/

////////////////////////////////////////
//  CODING STYLES
////////////////////////////////////////

// Functional
{
    const users = [
        { id: 1, name: 'Alice', age: 25 },
        { id: 2, name: 'Bob', age: 30 }
    ]

    const getUserById = (id) => users.find(user => user.id === id)
    const getAllUserNames = () => users.map(user => user.name)

    console.log(getUserById(1)); // { id: 1, name: 'Alice', age: 25 }
    console.log(getAllUserNames()); // ['Alice', 'Bob']

}

// Declarative
{
    const numbers = [1, 2, 3, 4, 5];
    const doubled = numbers.map(n => n * 2);

    console.log(doubled); // [2, 4, 6, 8, 10]

}

// OOP
{
    class User {
        constructor (id, name, age) {
        this.id = id
        this.name = name
        this.age = age
        }

        getDetails () {
        return `${this.name}, Age: ${this.age}`
        }
    }

    const user = new User(1, 'Alice', 25)
    console.log(user.getDetails()); // Alice, Age: 25

}

// Module
{
    const UserModule = (function() {
    const users = [];
    
    const addUser = (user) => {
        users.push(user);
    };
    
    const getUser = (id) => users.find(user => user.id === id);
    
    return {
        addUser,
        getUser,
    };
    })();
    
    UserModule.addUser({ id: 1, name: 'Alice', age: 25 });
    console.log(UserModule.getUser(1)); // { id: 1, name: 'Alice', age: 25 }
      
}

// Promises & Async/Await
{
    const fetchUserData = (id) => {
        return new Promise((resolve) => {
            setTimeout(() => {
            resolve({ id: id, name: 'Alice', age: 25 });
            }, 1000);
        });
    };
    
    const displayUser = async (id) => {
        const user = await fetchUserData(id);
        console.log(user);
    };
    
    displayUser(1); // { id: 1, name: 'Alice', age: 25 }
      
}

// Middleware
{
    const express = require('express');
    const app = express();

    const logger = (req, res, next) => {
        console.log(`${req.method} ${req.url}`);
        next();
    };

    app.use(logger);

    app.get('/', (req, res) => {
        res.send('Hello, world!');
    });

    app.listen(3000, () => {
        console.log('Server is running on port 3000');
    });

}

// Callback
{
    const fs = require('fs');

    fs.readFile('example.txt', 'utf8', (err, data) => {
        if (err) {
            console.error(err);
            return;
        }
        console.log(data);
    });

}

// Object Composition
{
    const canEat = {
        eat: function() {
            console.log('Eating...');
        }
    };
    
    const canWalk = {
        walk: function() {
            console.log('Walking...');
        }
    };
    
    const person = Object.assign({}, canEat, canWalk);
    person.eat(); // Eating...
    person.walk(); // Walking...
    
}

////////////////////////////////////////
//  DESIGN PATTERNS
////////////////////////////////////////

// * Creational

// Prototype
{
    function User(id, name, age) {
        this.id = id;
        this.name = name;
        this.age = age;
    }
    
    User.prototype.getDetails = function() {
        return `${this.name}, Age: ${this.age}`;
    };
    
    const user = new User(1, 'Alice', 25);
    console.log(user.getDetails()); // Alice, Age: 25
      
}

// * Structural

// * Behavioral

////////////////////////////////////////
//  OTHER PATTERNS
////////////////////////////////////////

// Event-Driven
{
    const EventEmitter = require('events');
    class UserEmitter extends EventEmitter {}

    const userEmitter = new UserEmitter();

    userEmitter.on('userCreated', (user) => {
        console.log(`User created: ${user.name}`);
    });

    userEmitter.emit('userCreated', { name: 'Alice' }); // User created: Alice

}


////////////////////////////////////////
//  TEST CASES
////////////////////////////////////////

function main () {
    document.write('Hello, World!')
}
