package com.example.samples.repositories.seeding;

import com.example.samples.models.Company;
import com.example.samples.models.User;
import com.example.samples.repositories.CompanyRepoImpl;
import com.example.samples.repositories.UserRepoImpl;

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
public class DatabaseSeeder {

    private final UserRepoImpl userRepo;
    private final CompanyRepoImpl companyRepo;

    // Inject UserRepoImpl & CompanyRepoImpl using constructor injection
    public DatabaseSeeder(
            UserRepoImpl userRepo,
            CompanyRepoImpl companyRepo
    ) {
        this.userRepo = userRepo;
        this.companyRepo = companyRepo;

        seedDatabases( // * Method reference replaces lambda callback: (T obj) -> _Repo.save(obj)
                userRepo::save, // (User user) -> userRepo.save(user)
                companyRepo::save // (Company company) -> companyRepo.save(company)
        );
    }

    private static <T, U> void seedDatabases(
            Function<T, T> userRepo_Save,
            Function<U, U> companyRepo_Save
    ) {
        try {
            HttpResponse<String> data = makeRequest("https://jsonplaceholder.typicode.com/users");
            if (data == null) throw new Exception("Seed data request failed");
            HashMap<T, U> objects = serialize(data);
            if (objects == null) throw new Exception("Data Serialization failed");

            objects.forEach((T user, U company) -> {

                /**
                 * TODO: can extend User model class with other props - phone, website, & (Company) companyData
                 * & prune these unnecessary props on database seed
                 * match Company id after companyRepo.save() with User company_id before userRepo.save()
                 */

                // cast both generic types to hard-coded expected types (User-Company)
                Company newCompany = (Company) companyRepo_Save.apply(company); // cast returned-type U -> Company
                User newUser = (User) user; // cast type T -> User before calling .setCompany_id(..)
                System.out.println(
                        "Assigning Company ("
                        + newCompany.getName()
                        + ") ID: "
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

    private static <T, U> HashMap<T, U> serialize(HttpResponse<String> data) {

        HashMap<User, Company> seedData = new HashMap<>(); // should return empty data on error (after handling)

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

                        0L, // default long company_id
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
                        company.get("catchPhrase").asText(),
                        Date.from(Instant.now())
                );

                seedData.put(user, companyValue); // * careful of company data-duplicates, as deep-populated props of each user-object json-node
                // todo: sub-optimal solution: handle all discrepancies in .save() methods, in _RepositoryImplementation classes
                // only save new object if it doesn't already exist in database
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return (HashMap<T, U>) seedData; // * risky cast - should break if wrong/unexpected generic-method call
        // * <T - User, U - Company> - on specific generic seedDatabase(cb<T = User>, cb<U = Company>) call
        // so should be fine for now

    }

}
