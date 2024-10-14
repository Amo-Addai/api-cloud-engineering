package com.example.demo.samples;

import com.example.samples.models.Company;
import com.example.samples.models.User;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.function.Function;


@Component
public class SampleDatabaseSeeder {

    // static db-seeding
    static {

        
        /*
         *  
         * Java, static blocks are executed when the class is first loaded, which typically happens before the application starts and before any objects are created.
         * This means that the static block would execute once when the DBSeeder class is loaded, which may be too early for accessing Spring-managed beans like UserRepoImpl.
         * 
         * Pitfalls of Static Block Seeding:
         *  - No Spring Context: Since static blocks run before the Spring context is fully initialized, you may not be able to use Spring-managed beans like UserRepoImpl to perform database operations.
         *  - Single Execution: Static blocks only run once per class loading, which might not be ideal for certain runtime database operations or multiple executions.
         *  - Limited Dependency Injection: Dependency injection doesn’t work well inside static blocks since Spring beans aren't available at the time the static block runs.
         * 
         * 
         
            * Static block for seeding (not recommended for Spring-managed beans)
            static {
                * This might not work due to lack of Spring context at this point
                System.out.println("Running static block seeding...");
                * Assuming this causes issues with Spring-managed components
                userRepo.save(new User("john", "john@example.com")); // * This would fail because userRepo is an external class in spring-api
            }
        
        */


        /*

        user:
        id=>id
        username=>username
        first_name=>first index of 'name' split with space
        last_name=>remaining of 'name' after first index of 'name' split with space
        email=>email
        address=>concatenate with space delimited, (address.street, address.suite, address.city, address.zipcode) with address.geo.lat+':'+address.geo.lng
        company_id=>company_id 'id' FK for 'company' table
        record_date=>current server date

        company:
        id=>system generated id
        name=>company.name
        info=>company.catchPhrase
        code=>company.bs
        record_date=>company.record_date

        {
            "id": 1, // todo: convert from int to long if required
            "name": "Leanne Graham", // todo: split into first & last names
            "username": "Bret",
            "email": "Sincere@april.biz",
            "address": { // todo: concatenate into address string
              "street": "Kulas Light",
              "suite": "Apt. 556",
              "city": "Gwenborough",
              "zipcode": "92998-3874",
              "geo": {
                "lat": "-37.3159",
                "lng": "81.1496"
              }
            },
            // todo: leave out next 2
            "phone": "1-770-736-8031 x56442",
            "website": "hildegard.org",

            "company": { // todo: seed Company Table
              "name": "Romaguera-Crona",
              "catchPhrase": "Multi-layered client-server neural-net",
              "bs": "harness real-time e-markets"
            }
        }

        */

        UserRepoImpl userRepo = new UserRepoImpl();
        CompanyRepoImpl companyRepo = new CompanyRepoImpl();

        seedDatabases( // * Method reference replaces lambda callback: (T obj) -> _Repo.save(obj)
            (User user) -> userRepo.save(user), // userRepo::save, // 
            (Company company) -> companyRepo.save(company) // companyRepo::save // 
        );

    }

    private final UserRepoImpl userRepo;
    private final CompanyRepoImpl companyRepo;

    // Inject UserRepoImpl using constructor injection
    public SampleDatabaseSeeder(
        UserRepoImpl userRepo,
        CompanyRepoImpl companyRepo
    ) {
        this.userRepo = userRepo;
        this.companyRepo = companyRepo;

        seedDatabases( // * Method reference replaces lambda callback: (T obj) -> _Repo.save(obj)
                userRepo::save, // (User user) -> userRepo.save(user), // 
                companyRepo::save // (Company company) -> companyRepo.save(company) // 
        );
    }

    public void seedDatabase() {
        // Seeding logic here
        User user = new User("john", "john@example.com");
        userRepo.save(user); // Saving the user
        System.out.println("Database seeded with user: " + user.getUsername());
    }


    // TODO: Unnecessary Generic Logic (suffix-T) - only 2 domain-types being worked with
    // also brings on unnecessary forced-castings


    private static <T, U> void seedDatabasesT(
            Function<T, T> userRepo_Save,
            Function<U, U> companyRepo_Save
    ) {
        try {
            HttpResponse<String> data = makeRequest("https://jsonplaceholder.typicode.com/users");
            if (data == null) throw new Exception("Seed data request failed");
            HashMap<T, U> objects = serialize(data);
            if (objects == null) throw new Exception("Data Serialization failed");
            // todo: confirm _this static context exec'd after h2-db setup in-build
            objects.forEach((T user, U company) -> {

                /**
                 * TODO: can extend User model class with other props - phone, website, & (Company) companyData
                 * & prune these unnecessary props on database seed
                 * match Company id after companyRepo.save() with User company_id before userRepo.save()
                 */

                // TODO: this should trigger error if db-seeding occurs in static environment (this.entityManager in CompanyRepoImpl.java external file - not in this file's static environment)
                // Cannot invoke "jakarta.persistence.EntityManager.persist(Object)" because "this.entityManager" is null

                // cast both generic types to hard-coded expected types (User-Company)
                Company newCompany = (Company) companyRepo_Save.apply(company); // cast returned-type U -> Company
                User newUser = (User) user; // cast type T -> User before calling .setCompany_id(..)
                System.out.println(
                        "Assigning Company ID: "
                        + newCompany.getId().toString()
                        + " ; to User: "
                        + newUser.getUsername()
                );
                newUser.setCompany_id(newCompany.getId());
                // cast User back -> to T before .applying generic Function<T, T> userRepo_Save
                userRepo_Save.apply((T) newUser);
            });
        } catch (Exception e) {
            System.out.println("Error making seed-data request");
            System.out.println(e.toString());
            e.printStackTrace();
        }
    }

    private static HttpResponse<String> makeRequestT(String url) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request =
                    HttpRequest.newBuilder()
                            .uri(URI.create(url))
                            .GET()
                            .build();
            HttpResponse<String> response =
                    client.send(
                            request,
                            HttpResponse.BodyHandlers
                                    .ofString()
                    );
            return response;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static <T, U> HashMap<T, U> serializeT(HttpResponse<String> data) {
        try {
            ObjectMapper mapper = new ObjectMapper();

            /** json-data & T-U (User-Company) class definitions do not 100%-match
            return List.of(
                    mapper.readValue(
                            data.body(),
                            T[].class
                            // * matched Company id after companyRepo.save() with User company_id before userRepo.save()
                    )
            );
            */

            // parse manually
            JsonNode root = mapper.readTree(data.body());
            HashMap<User, Company> seedData = new HashMap<>();

            // * don't define dummy user/company objects outside loop
            // * need user-object's key reference as HashMap key (no unintentional object-reference mixups)
            String[] fullName; JsonNode address; JsonNode company;

            for (JsonNode node : root) {

                fullName = node.get("name").asText().split("\s");
                address = node.get("address");
                company = node.get("company");

                User user = new User(
                        node.get("username").asText(),
                        fullName[0],
                        fullName[1],
                        node.get("email").asText(),

                        // * building address string
                        "( "
                        + address.get("street").asText()
                        + ", " + address.get("suite").asText()
                        + ", " + address.get("city").asText()
                        + ", " + address.get("zipcode").asText()
                        + ", " + address.get("geo").get("lat").asText()
                        + ":" + address.get("geo").get("lng").asText()
                        + " )",

                        // TODO: Using .company.catchPhrase as pseudo - company foreign key
                        // * FunctionalInterface callback for extra logic & console-log
                        ((Function<JsonNode, Long>) (JsonNode comp) -> {
                            String catchPrase = comp.get("catchPhrase").asText();
                            System.out.println("Returning CatchPhrase as Long id -> " + catchPrase);
                            return Long.valueOf(catchPrase);
                            /**
                             * NOTE: optimal way to seed company data 1st, then assign all ids as users' foreign keys
                             */
                        }).apply(company),

                        Date.from(Instant.now())

                        /*
                        // todo: phone & website props left out
                        "phone": "1-770-736-8031 x56442",
                        "website": "hildegard.org",
                         */
                );

                Company companyValue = new Company(
                        company.get("name").asText(),
                        company.get("bs").asText(),
                        company.get("catchPrase").asText(),
                        Date.from(Instant.now())
                );

                seedData.put(user, companyValue); // * careful of company data-duplicates, as deep-populated props of each user-object json-node
                // todo: sub-optimal solution: handle all discrepancies in .save() methods, in _RepositoryImplementation classes
                // only save new object if it doesn't already exist in database
            }

            return (HashMap<T, U>) seedData; // * risky cast - should break if wrong/unexpected generic-method call
            // * <T - User, U - Company> - on specific generic seedDatabase(cb<T = User>, cb<U = Company>) call
            // so should be fine for now

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    
    // TODO: Non-Generic equivalent - only 2 domain-types - User & Company


    private static void seedDatabases(
            Function<User, User> userRepo_Save,
            Function<Company, Company> companyRepo_Save
    ) {
        try {
            HttpResponse<String> data = makeRequest("https://jsonplaceholder.typicode.com/users");
            if (data == null) throw new Exception("Seed data request failed");
            HashMap<User, Company> objects = serialize(data);
            if (objects == null) throw new Exception("Data Serialization failed");
            // todo: confirm _this static context exec'd after h2-db setup in-build
            objects.forEach((User user, Company company) -> {

                /**
                 * TODO: can extend User model class with other props - phone, website, & (Company) companyData
                 * & prune these unnecessary props on database seed
                 * match Company id after companyRepo.save() with User company_id before userRepo.save()
                 */

                company = companyRepo_Save.apply(company);
                System.out.println(
                        "Assigning Company ID: "
                                + company.getId().toString()
                                + " ; to User: "
                                + user.getUsername()
                );
                user.setCompany_id(company.getId());
                userRepo_Save.apply(user);
            });
        } catch (Exception e) {
            System.out.println("Error making seed-data request");
            System.out.println(e.toString());
            e.printStackTrace();
        }
    }

    private static HttpResponse<String> makeRequest(String url) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request =
                    HttpRequest.newBuilder()
                            .uri(URI.create(url))
                            .GET()
                            .build();
            HttpResponse<String> response =
                    client.send(
                            request,
                            HttpResponse.BodyHandlers
                                    .ofString()
                    );
            return response;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static HashMap<User, Company> serialize(HttpResponse<String> data) {
        try {
            ObjectMapper mapper = new ObjectMapper();

            /** json-data & T-U (User-Company) class definitions do not 100%-match
             return List.of(
                mapper.readValue(
                    data.body(),
                    T[].class
                    // * matched Company id after companyRepo.save() with User company_id before userRepo.save()
                )
             );
             */

            // parse manually
            JsonNode root = mapper.readTree(data.body());
            HashMap<User, Company> seedData = new HashMap<>();

            // * don't define dummy user/company objects outside loop
            // * need user-object's key reference as HashMap key (no unintentional object-reference mixups)
            String[] fullName; JsonNode address; JsonNode company;

            for (JsonNode node : root) {

                fullName = node.get("name").asText().split("\s");
                address = node.get("name");
                company = node.get("company");

                User user = new User(
                        node.get("username").asText(),
                        fullName[0],
                        fullName[1],
                        node.get("email").asText(),

                        // * building address string
                        "( "
                        + address.get("street").asText()
                        + ", " + address.get("suite").asText()
                        + ", " + address.get("city").asText()
                        + ", " + address.get("zipcode").asText()
                        + ", " + address.get("geo").get("lat").asText()
                        + ":" + address.get("geo").get("lng").asText()
                        + " )",

                        // TODO: Using .company.catchPhrase as pseudo - company foreign key
                        // * FunctionalInterface callback for extra logic & console-log
                        ((Function<JsonNode, Long>) (JsonNode comp) -> {
                            String catchPrase = comp.get("catchPhrase").asText();
                            System.out.println("Returning CatchPhrase as Long id -> " + catchPrase);
                            return Long.valueOf(catchPrase);
                            /**
                             * NOTE: optimal way to seed company data 1st, then assign all ids as users' foreign keys
                             */
                        }).apply(company),

                        Date.from(Instant.now())

                        /*
                        // todo: phone & website props left out
                        "phone": "1-770-736-8031 x56442",
                        "website": "hildegard.org",
                         */
                );

                Company companyValue = new Company(
                        company.get("name").asText(),
                        company.get("bs").asText(),
                        company.get("catchPrase").asText(),
                        Date.from(Instant.now())
                );

                seedData.put(user, companyValue); // * careful of company data-duplicates, as deep-populated props of each user-object json-node
                // todo: sub-optimal solution: handle all discrepancies in .save() methods, in _RepositoryImplementation classes
                // only save new object if it doesn't already exist in database
            }

            return seedData;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
