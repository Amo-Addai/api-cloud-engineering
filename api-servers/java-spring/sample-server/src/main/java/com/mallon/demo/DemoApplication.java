package com.mallon.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		
		// Start the RESTFul services
		SpringApplication.run(DemoApplication.class, args);
		
		// Example client code to make use of the RESTFul services
		WebClient webClient = WebClient.builder().baseUrl("http://localhost:8080").defaultHeader("Content-Type", "application/json").build();		

		TransferObject tfObject = new TransferObject(33, "Mike");
		TransferObject tfObject1 = new TransferObject(32, "Rachel");
		TransferObject tfObject2 = new TransferObject(52, "Greg");
		TransferObject tfObject3 = new TransferObject(43, "Jane");
		
		
		// Call the POST methods
		webClient.post().uri("/example").body(Mono.just(tfObject), TransferObject.class).retrieve().bodyToFlux(TransferObject.class).subscribe(
			confirmation-> {System.out.println("Transfer Object Stored!");},
			error->System.out.println(error)
		);
		webClient.post().uri("/example").body(Mono.just(tfObject1), TransferObject.class).retrieve().bodyToFlux(TransferObject.class).subscribe(
				confirmation-> {System.out.println("Transfer Object Stored!");},
				error->System.out.println(error)
			);
		webClient.post().uri("/example").body(Mono.just(tfObject2), TransferObject.class).retrieve().bodyToFlux(TransferObject.class).subscribe(
				confirmation-> {System.out.println("Transfer Object Stored!");},
				error->System.out.println(error)
			);
		webClient.post().uri("/example").body(Mono.just(tfObject3), TransferObject.class).retrieve().bodyToFlux(TransferObject.class).subscribe(
				confirmation-> {System.out.println("Transfer Object Stored!");},
				error->System.out.println(error)
			);
		
		/* Sleep a little, as the above are async, so if we don't give enough time
		 * there will be nothing to retrieve. 
		 */
		try {
			Thread.sleep(3000);
		} catch (Exception e) {
			
		}
		
		// Now we should be able to request the data from the server...
		
		// Call the GET methods
		Flux<TransferObject> returnedList = 
				webClient.get().uri("/example").accept(MediaType.APPLICATION_JSON).retrieve().bodyToFlux(TransferObject.class);
		
		returnedList.toStream().forEach((TransferObject object)-> {
			System.out.println(object);
		});
		
		
	}


}
