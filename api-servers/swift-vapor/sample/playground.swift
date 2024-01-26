import Vapor

public func boot(_ app: Application) throws {
    // your code here
    try app.run()

    // or, use this code
    let client = try app.make(Client.self)
    let res = try client.get("http://vapor.codes").wait()
    print(res) // Response
}

final class HelloController {
    func greet(_ req: Request) throws -> String {
        return try req.make(BCryptHasher.self).hash("hello") // OR JUST "Hello!"
    }
}

let router = try EngineRouter.default()

router.get("users") { req in
    return // fetch the users
}

router.get("path", "to", "something") {}

let helloController = HelloController()
router.get("greet", use: helloController.greet)

// BEST PLACE TO ADD ROUTES IS IN routes.swift

public func routes(_ router: Router) throws {
    
    // Basic "Hello, world!" example
    router.get("hello") { req in
        return "Hello, world!"
    }

    // With Request Parameters
    router.get("users", Int.parameter) { req -> String in
        let id = try req.parameters.next(Int.self)
        return "requested id #\(id)"
    }

}

// Creating custom Request Content (Body) 

struct LoginRequest: Content { // Content CLASS IS IN-BUILT
    var email: String
    var password: String
}

router.post("login") { req -> Future<HTTPStatus> in
    return req.content.decode(LoginRequest.self).map(to: HTTPStatus.self) { loginRequest in
        print(loginRequest.email) // user@vapor.codes
        print(loginRequest.password)
        return .ok
    }
}


// Creating custom Response Content (Body) 

struct User: Content {
    var name: String
    var email: String
}

router.get("user") { req -> User in
    return User(
        name: "Vapor User",
        email: "user@vapor.codes"
    )
}


