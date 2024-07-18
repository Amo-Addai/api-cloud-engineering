// package Java;

import java.util.stream.Collectors;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/*
 * Work with in-built DataStructure classes, from all java source libs 
 */

/* // TODO: To-Use

Generics
Functional Interfaces (all in-built & custom)
eg. Function, Consumer, Predicate, BiFunction, BiConsumer, BiPredicate, Supplier, UnaryOperator, BinaryOperator,  ... 
Lombok, 
..

*/

public class Java {

    public static void main(String[] args) {
        Main.main(args);
    }

    public static class Main {

        ////////////////////////////////////////
        // CODING STYLES
        ////////////////////////////////////////

        public static class Obj {

            static class User {
                private int id;
                private String name;
                private int age;
            
                public User(int id, String name, int age) {
                    this.id = id;
                    this.name = name;
                    this.age = age;
                }
            
                public int getId() {
                    return id;
                }
            
                public String getName() {
                    return name;
                }
        
                public String getDetails() {
                    return name + ", Age: " + age;
                }
            }
        
            static class UserModule {
                private static List<User> users = new ArrayList<>();
            
                public static void addUser(User user) {
                    users.add(user);
                }
            
                public static User getUser(int id) {
                    return users.stream().filter(user -> user.getId() == id).findFirst().orElse(null);
                }
            }
        
            static class CloneableUser implements Cloneable {
                private int id;
                private String name;
                private int age;
            
                public CloneableUser(int id, String name, int age) {
                    this.id = id;
                    this.name = name;
                    this.age = age;
                }
            
                public String getDetails() {
                    return name + ", Age: " + age;
                }
            
                @Override
                public CloneableUser clone() {
                    try {
                        return (CloneableUser) super.clone();
                    } catch (CloneNotSupportedException e) {
                        throw new AssertionError();
                    }
                }
            }

        }

        public static class Functional {

            public static void test(String[] args) {
                List<Main.Obj.User> users = Arrays.asList(
                        new Main.Obj.User(1, "Alice", 25),
                        new Main.Obj.User(2, "Bob", 30)
                );

                Optional<Main.Obj.User> user = users.stream()
                        .filter(u -> u.getId() == 1)
                        .findFirst();
                user.ifPresent(u -> System.out.println(u.getName()));

                users.stream()
                        .map(Main.Obj.User::getName)
                        .forEach(System.out::println);
            }

        }

        public class Declarative {

            public static void test(String[] args) {
                List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
                List<Integer> doubled = numbers.stream()
                        .map(x -> x * 2)
                        .collect(Collectors.toList());

                doubled.forEach(System.out::println);  // 2, 4, 6, 8, 10
            }

        }

        public static class OOP {

            public static void test(String[] args) {
                Main.Obj.User user = new Main.Obj.User(1, "Alice", 25);
                System.out.println(user.getDetails());
            }

        }

        public static class AsyncAwait {

            public static void test(String[] args) {
                CompletableFuture<Main.Obj.User> userFuture = fetchUserData(1);
                userFuture.thenAccept(user -> System.out.println(user.getName()));
            }
        
            public static CompletableFuture<Main.Obj.User> fetchUserData(int userId) {
                return CompletableFuture.supplyAsync(() -> {
                    try {
                        Thread.sleep(1000); // Simulate a network call
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return new Main.Obj.User(userId, "Alice", 25);
                });
            }

        }

        public class Callback {

            interface ReadFileCallback {
                void done(Exception error, String data);
            }

            public static void readFile(String filePath, ReadFileCallback callback) {
                try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
                    StringBuilder content = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        content.append(line).append("\n");
                    }
                    callback.done(null, content.toString());
                } catch (IOException e) {
                    callback.done(e, null);
                }
            }

            public static void test(String[] args) {
                readFile("example.txt", (error, data) -> {
                    if (error != null) {
                        System.out.println("Error: " + error);
                    } else {
                        System.out.println(data);
                    }
                });
            }

        }

        public static class ObjectComposition {

            interface EatBehavior {
                void eat();
            }

            interface WalkBehavior {
                void walk();
            }

            class Eat implements EatBehavior {
                public void eat() {
                    System.out.println("Eating...");
                }
            }

            class Walk implements WalkBehavior {
                public void walk() {
                    System.out.println("Walking...");
                }
            }

            class Person {
                private EatBehavior eatBehavior = new Eat();
                private WalkBehavior walkBehavior = new Walk();

                public void eat() {
                    eatBehavior.eat();
                }

                public void walk() {
                    walkBehavior.walk();
                }
            }

            public static void test(String[] args) {
                Person person = new Person();
                person.eat();
                person.walk();
            }

        }


        ////////////////////////////////////////
        // DESIGN PATTERNS
        ////////////////////////////////////////
        
        // * Creational
        
        public static class Prototype {

            public static void test(String[] args) {
                Main.Obj.CloneableUser user = new Main.Obj.CloneableUser(1, "Alice", 25);
                Main.Obj.CloneableUser userClone = user.clone();
                System.out.println(userClone.getDetails());
            }

        }
        
        // * Structural
        
        
        // * Behavioral
        
        
        // * Others

        public static class Module {
            
            public static void test(String[] args) {
                Main.Obj.UserModule.addUser(new Main.Obj.User(1, "Alice", 25));
                System.out.println(Main.Obj.UserModule.getUser(1).getName());
            }
            
        }
        
        public static class Middleware {

            interface MiddlewareFunction {
                void apply(String context, Runnable next);
            }

            class Middleware {
                private final List<MiddlewareFunction> middlewares = new ArrayList<>();
                private int current = -1;

                public void use(MiddlewareFunction middleware) {
                    middlewares.add(middleware);
                }

                public void execute(String context) {
                    current = -1;
                    next(context);
                }

                private void next(String context) {
                    current++;
                    if (current < middlewares.size()) {
                        MiddlewareFunction middleware = middlewares.get(current);
                        middleware.apply(context, () -> next(context));
                    }
                }
            }

            public static void test(String[] args) {
                Middleware middleware = new Middleware();
                middleware.use((context, next) -> {
                    System.out.println("Logging: " + context);
                    next.run();
                });
                middleware.use((context, next) -> {
                    System.out.println("Handling: " + context);
                });

                middleware.execute("GET /home");
            }

        }
        
        ////////////////////////////////////////
        // OTHER PATTERNS
        ////////////////////////////////////////

        public static class EventDriven {
            
            interface EventListener {
                void onEvent(Object event);
            }

            class EventEmitter {
                private final List<EventListener> listeners = new ArrayList<>();

                public void on(String event, EventListener listener) {
                    listeners.add(listener);
                }

                public void emit(String event, Object data) {
                    for (EventListener listener : listeners) {
                        listener.onEvent(data);
                    }
                }
            }

            public static void test(String[] args) {
                EventEmitter eventEmitter = new EventEmitter();
                eventEmitter.on(
                    "user_created", 
                    (user) -> 
                        System.out.println(
                            "User created: " 
                            + ((Main.Obj.User) user).getName()
                        )
                );
                eventEmitter.emit("user_created", new Main.Obj.User(1, "Alice", 25));
            }

        }


        ////////////////////////////////////////
        // TEST CASES
        ////////////////////////////////////////

        public static void main(String[] args) {
            System.out.println("Hello, World!");
        }

    }

}

/**
 * NOTES:
 * 
 */