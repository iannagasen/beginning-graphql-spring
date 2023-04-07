package com.agasen.graphql;

import java.util.Collection;
import java.util.List;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;
import org.springframework.stereotype.Service;

@SpringBootApplication
public class GraphqlApplication {

	public static void main(String[] args) {
		SpringApplication.run(GraphqlApplication.class, args);
	}

	@Bean
	RuntimeWiringConfigurer runtimeWiringConfigurer(CrmService service) {

		return builder -> {

			builder.type("Customer", wiring -> wiring
					.dataFetcher("profile", env -> service.getProfileFor(env.getSource())));

			builder.type("Query", wiring -> wiring
					.dataFetcher("customerById", env -> service.getCustomerById(Integer.parseInt(env.getArgument("id"))))
					.dataFetcher("customers", env -> service.getCustomers()));

		};
	}
}

record Customer(Integer id, String name) {
}

record Profile(Integer id, Integer customerId) {
}

@Service
class CrmService {

	Profile getProfileFor(Customer customer) {
		return new Profile(customer.id(), customer.id());
	}

	Customer getCustomerById(Integer id) {
		return new Customer(id, Math.random() > .5 ? "A" : "B");
	}

	Collection<Customer> getCustomers() {
		return List.of(new Customer(1, "A"), new Customer(2, "B"));
	}
}