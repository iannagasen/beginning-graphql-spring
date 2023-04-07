### This project was inspired by this [SpringDeveloper playlist](https://www.youtube.com/watch?v=gvIqFDNGgwU&list=PLgGXSWYM2FpNRPDQnAGfAHxMl3zUG2Run)

---

## GUIDE:

#### _**NOTE:** Issue encountered during these project. Can not start application server, always terminate. Solution was to copy pom.xml generated in the spring.starter.io. Issue was with Spring WebFlux from Reactive Spring in vscode_

<br>

#### **Step 1.** First is we need to provide a schema.

- Create a `graphql` directory under `resources`
- Create a schema, anyname will do, with a extension of .graphqls
  - in this project will use: `engine.graphqls`

#### **Step 2.** Describe the types we want to work with in that schema

- Create a root type called `Query`
- Add a field named `customers` that will return an array of `Customer`
- Create also a type of `Customer`
  - fields of `id` of type `ID`, `name` of type `String`
- Under `Query`
  - create another field named `customerById` which takes an **`ID` as an argument** and return a `Customer` object
  - create another field name `profile` with type `Profile`
- Create also a type of `Profile`
  - fields of `id` of type `ID`, `customerId` of type `ID`

```graphql
type Query {
  customers: [Customer]
  customerById(id: ID): Customer
}

type Customer {
  id: ID
  name: String
  profile: Profile
}

type Profile {
  id: ID
  customerId: ID
}
```

**NOTE:** _Think of the fields as Getters_

#### **Step 3.** Add these code to define a simple GraphQL in our app

#### **`GraphqlApplication.java`**

```java
...
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

```

#### **Step 4.** Add this to app.props to enable `graphiql`

> `spring.graphql.graphiql.enabled=true`

<br>

#### **Step 5.** Run the application and test `graphql` with these link:

> [http://localhost:8080/graphiql?](http://localhost:8080/graphiql)

<br>

#### **Step 6.** Run query scripts

- Query 1: Get all the customers id

```graphql
# query is the root query
query {
  customers: {
    id
  }
}
```

- Query 2: Get id, name, profile of customer with id = 2

```graphql
query {
  customerById(id: 2) {
    id
    name
    profile {
      id
      customerId
    }
  }
}
```
