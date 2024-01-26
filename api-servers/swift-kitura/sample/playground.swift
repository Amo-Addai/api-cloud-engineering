

import Foundation
import Kitura
import LoggerAPI
import HeliumLogger
import Application


let router = Router()

router.get("/") { request, response, next in
    response.send("Hello world")
    next()
}

Kitura.addHTTPServer(onPort: 8080, with: router)
Kitura.run()

do {
    HeliumLogger.use(LoggerMessageType.info)
    let app = try Application()
    try app.run()
} catch let error {
    Log.error(error.localizedDescription)
}


// Handle HTTP GET requests to "/"

router.get("/") { request, response, next in
    response.send("Hello, World!")
    next()
}

class MealController {
    
    private var mealStore: [String: Meal] = [:]
    let cloudEnv = CloudEnv()
    let router = Router()

    router.post("/meals", handler: storeHandler)
    func storeHandler(meal: Meal, completion: (Meal?, RequestError?) -> Void ) {
        mealStore[meal.name] = meal
        completion(mealStore[meal.name], nil)
    }

    router.get("/meals", handler: loadHandler)
    func loadHandler(completion: ([Meal]?, RequestError?) -> Void ) {
        let meals: [Meal] = self.mealStore.map({ $0.value })
        completion(meals, nil)
    }

    router.get("/summary", handler: summaryHandler)
    func summaryHandler(completion: (Summary?, RequestError?) -> Void ) {
       let summary: Summary = Summary(self.mealStore)
       completion(summary, nil)
    }

}




