package io.learning.springbootcrudapi;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;


@SpringBootTest
class SpringbootCrudApiApplicationTests {

	
	
}


@SpringBootTest
@DisplayName("Customer Service Test")
class CustomerServiceTest {
	
	@MockBean
	private CustomerRepository customerRepository;
	
	@Autowired
	private CustomerService customerService;
	
	@BeforeEach
	public void beforEach() {
		when(customerRepository.findById(1l)).thenReturn(
				Optional.of(
						Customer.builder().id(1l).name("John Smith").build())
				);
		
		when(customerRepository.findById(anyLong())).thenReturn(
				Optional.of(
						Customer.builder().id(2l).name("Unknown Customer").build())
				);
		
		when(customerRepository.findAll()).thenReturn(
				List.of(
						Customer.builder().id(1l).name("John Smith").build(),
						Customer.builder().id(2l).name("Amanda John").build(),
						Customer.builder().id(3l).name("Naidu Marker").build()
				)
				
		);
		
		when(customerRepository.save(any(Customer.class))).thenReturn(				
				Customer.builder().id(1l).name("John Smith").build()
		);
		
		
		when(customerRepository.findByIdAndName(anyLong(), any())).thenReturn(
				Optional.of(
					Customer.builder().id(1l).name("John Smith").build()
				)
		);
		
		doNothing().when(customerRepository).deleteById(anyLong());
	}
	
	@Test
	@DisplayName("Check getById(1l) method should return a valid response")
	public void getByIdValidTest() {
		assertThat(customerService.getById(1l)).isNotNull();
		assertThat(customerService.getById(1l).getName()).isEqualTo("John Smith");
	}
	
	@Test
	@DisplayName("Check getById(5l) should throw CustomerNotFoundException")
	public void getByIdInValidTest() {
		assertThat(customerService.getById(5l)).isOfAnyClassIn(RuntimeException.class);
	}
}

