### This project was inspired by this [SpringDeveloper playlist](https://www.youtube.com/watch?v=gvIqFDNGgwU&list=PLgGXSWYM2FpNRPDQnAGfAHxMl3zUG2Run)

---

## **What is GraphQL**

- it is an alternative to REST for building Web APIs
- With GraphQL, clients can send queries to the server that specify exactly what they need
  - this allows clients to reduce the amount of data they need to retrieve and can improve the performance of the application
- It also **supports real-time data updates** using WebSockets, which can be useful for applications that require real-time data, such as chat applications or financial trading platforms

---

## **Advantage of GraphQL over REST**

1. **Efficient data retrieval**
   - allows client to specify exactly what data they need
2. **Strongly-typed schema**
   - provides a clear and well defined contract between the client and the server
   - With REST, schema is oftet implicit and loosely defined, which can lead to confusion and errors
3. **Query batching**
   - multiple queries can be batched into a single request
     - reducing the number of round trips and improving performance
     - useful for mobile applications or other scenarios where network connectivity may be slow or intermittent
4. **Field Level Granularity**
   - Give clients more control over the data they receive
5. **Evolutionary API design**
   - with graphQl, the server can evolve the API schema over time without breaking existing clients. With REST, changes to the API schema can often break existing clients and require versioning

---

## WebSockets vs HTTP

|               | WebSockets                                                                                                            | HTTP                                                                     |
| :------------ | --------------------------------------------------------------------------------------------------------------------- | ------------------------------------------------------------------------ |
| Connection    | Persistent, bidirectional connection between client and server                                                        | Request-Response based connection                                        |
|               | keeps the connection open after the initial handshake, allowing data to be transmitted in both directions at any time | requires new connection to be established for each request               |
| Latency       | Low latency, real-time communication                                                                                  | Higher latency due to request-response model                             |
| Overhead      | Lower overhead, as data is sent in binary format                                                                      | Higher overhead, as data is sent in text format(e.g. JSON, XML)          |
| Data Transfer | data is sent as raw binary data                                                                                       | data is sent as text, typically in JSON or XML format                    |
| Security      | Uses SSL/TLS encryption for scure comms                                                                               | Uses SSL/TLS encryption for scure comms                                  |
| Scalability   | Can support a large number of concurrent connections                                                                  | Limited by the number of available connections and network bandwidth     |
| Use Cases     | Real-time communications, gaming, chat apps, financial trading platforms                                              | Traditional web apps, REST APIs, file transfers, static content delivery |

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
