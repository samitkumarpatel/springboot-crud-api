package net.samitkumar.springboot_crud_api;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import static java.util.Objects.nonNull;
import static reactor.core.publisher.Mono.fromCallable;
import static reactor.core.publisher.Mono.fromRunnable;

@SpringBootApplication
public class SpringbootCrudApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringbootCrudApiApplication.class, args);
	}

	@Bean
	CorsWebFilter corsFilter() {
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowCredentials(true);
		config.addAllowedOriginPattern("*");
		config.addAllowedHeader("*");
		config.addAllowedMethod("*");

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", config);
		return new CorsWebFilter(source);
	}

	@Bean
	RouterFunction<ServerResponse> routerFunction(CustomerService customerService) {
		return RouterFunctions
				.route()
				.path("/customer", builder -> builder
						.GET("", customerService::allCustomer)
						.GET("/{id}", customerService::customerById)
						.POST("", customerService::newCustomer)
						.PUT("/{id}", customerService::updateCustomer)
						.PATCH("/{id}", customerService::patchCustomer)
						.DELETE("/{id}", customerService::deleteCustomer)
				)
				.build();
	}
}

@Table
@Builder(toBuilder = true)
record Customer(@Id Long id, String name, String email, String role) {}

interface CustomerRepository extends ListCrudRepository<Customer, Long> {}

@ResponseStatus(HttpStatus.NOT_FOUND)
class CustomerNotFoundException extends RuntimeException {
	public CustomerNotFoundException(String message) {
		super(message);
	}
}

@Service
@RequiredArgsConstructor
class CustomerService {
	final CustomerRepository customerRepository;

	public Mono<ServerResponse> allCustomer(ServerRequest request) {
		return fromCallable(customerRepository::findAll)
				.flatMap(ServerResponse.ok()::bodyValue);
	}

	public Mono<ServerResponse> customerById(ServerRequest request) {
		var id = Long.valueOf(request.pathVariable("id"));
		return fromCallable(() -> customerRepository.findById(id).orElseThrow(() -> new CustomerNotFoundException("Customer not found")))
				.subscribeOn(Schedulers.boundedElastic())
				.flatMap(ServerResponse.ok()::bodyValue);
	}

	public Mono<ServerResponse> newCustomer(ServerRequest request) {
		return request
				.bodyToMono(Customer.class)
				.map(customerRepository::save)
				.flatMap(ServerResponse.ok()::bodyValue);
	}

	public Mono<ServerResponse> updateCustomer(ServerRequest request) {
		var id = Long.valueOf(request.pathVariable("id"));

		return fromCallable(() -> customerRepository.findById(id).orElseThrow(() -> new CustomerNotFoundException("Customer not found")))
				.subscribeOn(Schedulers.boundedElastic())
				.flatMap(dbCustomer -> request.bodyToMono(Customer.class))
				.map(customer -> customer.toBuilder().id(id).build())
				.map(customerRepository::save)
				.flatMap(ServerResponse.ok()::bodyValue);
	}

	public Mono<ServerResponse> patchCustomer(ServerRequest request) {
		var id = Long.valueOf(request.pathVariable("id"));
		var dbCustomer = fromCallable(() -> customerRepository.findById(id).orElseThrow(() -> new CustomerNotFoundException("Customer not found")))
				.subscribeOn(Schedulers.boundedElastic());
		return request
				.bodyToMono(Customer.class)
				.zipWith(dbCustomer,(customer, db) -> {
					var dbBuilder = db.toBuilder();
					if(nonNull(customer.name())) dbBuilder.name(customer.name());
					if(nonNull(customer.email())) dbBuilder.email(customer.email());
					if(nonNull(customer.role())) dbBuilder.role(customer.role());
					return dbBuilder.build();
				})
				.map(customerRepository::save)
				.flatMap(ServerResponse.ok()::bodyValue);
	}

	public Mono<ServerResponse> deleteCustomer(ServerRequest request) {
		var id = Long.valueOf(request.pathVariable("id"));
		return fromRunnable(() -> customerRepository.deleteById(id))
				.subscribeOn(Schedulers.boundedElastic())
				.then(ServerResponse.ok().build());
	}
}