
import Kitura
import KituraCORS
import Foundation
import KituraContracts
import LoggerAPI
import Configuration
import CloudEnvironment
import Health
import Dispatch

public let projectPath = ConfigurationManager.BasePath.project.path
public let health = Health()
public var port: Int = 8080

public class Application {
    
    let router = Router()
    let cloudEnv = CloudEnv()
    var todoStore = [ToDo]()
    var nextId: Int = 0
    private let workerQueue = DispatchQueue(label: "worker")
    
    func postInit() throws {

        // Capabilities
        initializeMetrics(app: self)
        
        let options = Options(allowedOrigin: .all)
        let cors = CORS(options: options)
        router.all("/*", middleware: cors)
        
        // Endpoints
        initializeHealthRoutes(app: self)
        
        // Routes
        router.post("/", handler: createHandler)
        router.get("/", handler: getAllHandler)
        router.get("/", handler: getOneHandler)
        router.delete("/", handler: deleteAllHandler)
        router.delete("/", handler: deleteOneHandler)
        router.patch("/", handler: updateHandler)
        router.put("/", handler: updatePutHandler)
        
    }
    
    
    public init() throws {
        // Configuration
        port = cloudEnv.port
    }
    
    public func run() throws {
        try postInit()
        Kitura.addHTTPServer(onPort: port, with: router)
        Kitura.run()
    }
    
    func createHandler(todo: ToDo, completion: (ToDo?, RequestError?) -> Void ) -> Void {
        var todo = todo
        todo.completed = todo.completed ?? false
        todo.id = nextId
        todo.url = "http://localhost:8080/\(nextId)"
        nextId += 1
        execute {
            todoStore.append(todo)
        }
        completion(todo, nil)
    }
    
    func getAllHandler(completion: ([ToDo]?, RequestError?) -> Void ) -> Void {
        completion(todoStore, nil)
    }
    
    func getOneHandler(id: Int, completion: (ToDo?, RequestError?) -> Void ) -> Void {
        guard let idMatch = todoStore.first(where: { $0.id == id }), let idPosition = todoStore.index(of: idMatch) else {
            completion(nil, .notFound)
            return
        } // idMatch =~= todoStore[idPosition]
        completion(todoStore[idPosition], nil)
    }
    
    func deleteAllHandler(completion: (RequestError?) -> Void ) -> Void {
        execute {
            todoStore = [ToDo]()
        }
        completion(nil)
    }
    
    func deleteOneHandler(id: Int, completion: (RequestError?) -> Void ) -> Void {
        guard let idMatch = todoStore.first(where: { $0.id == id }), let idPosition = todoStore.index(of: idMatch) else {
            completion(.notFound)
            return
        }
        execute {
            todoStore.remove(at: idPosition)
        }
        completion(nil)
    }
    
    func updateHandler(id: Int, new: ToDo, completion: (ToDo?, RequestError?) -> Void ) -> Void {
        guard let idMatch = todoStore.first(where: { $0.id == id }), let idPosition = todoStore.index(of: idMatch) else {
            completion(nil, .notFound)
            return
        } // idMatch =~= current
        var current = todoStore[idPosition]
        current.user = new.user ?? current.user
        current.order = new.order ?? current.order
        current.title = new.title ?? current.title
        current.completed = new.completed ?? current.completed
        execute {
            todoStore[idPosition] = current
        }
        completion(todoStore[idPosition], nil)
    }
    
    func updatePutHandler(id: Int, new: ToDo, completion: (ToDo?, RequestError?) -> Void ) -> Void {
        guard let idMatch = todoStore.first(where: { $0.id == id }), let idPosition = todoStore.index(of: idMatch) else {
            completion(nil, .notFound)
            return
        }
        var current = todoStore[idPosition]
        current.user = new.user
        current.order = new.order
        current.title = new.title
        current.completed = new.completed
        execute {
            todoStore[idPosition] = current
        }
        completion(todoStore[idPosition], nil)
    }
    
    func execute(_ block: (() -> Void)) {
        workerQueue.sync {
            block()
        }
    }
    
}
