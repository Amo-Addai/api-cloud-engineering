package payroll;

import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
class LoadDatabase {

	@Bean
	CommandLineRunner initDatabase(EmployeeRepository employeeRepository, OrderRepository orderRepository) {
		return args -> { // FIRST, PRELOAD THE EMPLOYEES ..
            log.info("");
			log.info("Preloading Employee " + employeeRepository.save(new Employee("Bilbo Baggins", "burglar")));
			log.info("Preloading Employee " + employeeRepository.save(new Employee("Frodo Baggins", "thief")));
            
            // NOW, YOU CAN PRELOAD THE ORDERS ..
            log.info("");
			log.info("Preloading Order " + orderRepository.save(new Order("MacBook Pro", Status.COMPLETED)));
			log.info("Preloading Order " + orderRepository.save(new Order("iPhone", Status.IN_PROGRESS)));

		};
	}
}