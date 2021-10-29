package io.learning.springbootcrudapi;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.repository.CrudRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@SpringBootApplication
public class SpringbootCrudApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringbootCrudApiApplication.class, args);
	}

}

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
class Customer {
	@Id
	@GeneratedValue
	private Long id;
	private String name;
}

@ResponseStatus(code = HttpStatus.NOT_FOUND)
class CustomerNotFound extends RuntimeException {

	public CustomerNotFound() {
		super();
	}

	public CustomerNotFound(String message) {
		super(message);
	}
	
}

interface CustomerRepository extends CrudRepository<Customer, Long> {
	//check this documents for more information , How it works- https://docs.spring.io/spring-data/jpa/docs/2.5.6/reference/html/#reference
	Optional<Customer> findByIdAndName(Long id, String name);
}

@Service
class CustomerService {
	
	private CustomerRepository customerRepository;
	
	@Autowired
	public CustomerService(CustomerRepository customerRepository) {
		this.customerRepository = customerRepository;
	}
	
	public List<Customer> getAll() {
		return StreamSupport
				.stream(customerRepository.findAll().spliterator(), false)
				.collect(Collectors.toList());
	}
	
	public Customer getById(Long id) {
		return customerRepository
				.findById(id)
				.orElseThrow(() -> new CustomerNotFound(String.format("Customer Not Found with Id %s", id)));
	}
	
	public Customer getByIdAndName(Long id, String name) {
		return customerRepository
				.findByIdAndName(id, name)
				.orElseThrow(() -> new CustomerNotFound(String.format("Customer Not Found with Id %s & name %s", id, name)));
	}
	
	public Customer create(Customer customer) {
		return customerRepository.save(customer);
	}
	
	public Customer update(Long id, Customer customer) {
		return customerRepository.findById(id).map( customer1 -> {			
			customer1.setName(customer.getName());
			return customerRepository.save(customer1);
		}).orElseThrow(() -> new CustomerNotFound(String.format("Customer Not Found with Id %s", id)));
	}
	
	public void delete(Long id) {
		customerRepository.deleteById(id);			
	}
	
}

@RestController
@RequestMapping("/customer")
class CustomerController {
	
	private CustomerService customerService;

	@Autowired
	public CustomerController(CustomerService customerService) {
		this.customerService = customerService;
	}
	
	@GetMapping("{id}")
	public Customer getById(@PathVariable("id") Long id) {
		return customerService.getById(id);
	}
	
	@GetMapping("/filter")
	public Customer filter(@RequestParam("id") Long id, @RequestParam String name) {
		return customerService.getByIdAndName(id, name);
	}
	
	@GetMapping
	public List<Customer> getAll() {
		return customerService.getAll();
	}
	
	@PostMapping
	public Customer save(@RequestBody Customer customer) {
		return customerService.create(customer);
	}
	
	@PutMapping("{id}")
	public Customer update(@PathVariable("id") Long id, @RequestBody Customer customer) {
		return customerService.update(id, customer);
	}
	
	@DeleteMapping("{id}")
	public void delete(@PathVariable("id") Long id) {
		customerService.delete(id);
	}
	
}