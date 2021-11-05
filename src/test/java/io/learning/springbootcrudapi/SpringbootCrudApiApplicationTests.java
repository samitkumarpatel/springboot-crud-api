package io.learning.springbootcrudapi;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;


@SpringBootTest
class SpringbootCrudApiApplicationTests {
	
}


@ExtendWith(SpringExtension.class)
@DisplayName("CustomerService Test")
class CustomerServiceTest {
	
	@MockBean
	private CustomerRepository customerRepository;
	
	private CustomerService customerService;
	
	@BeforeEach
	public void beforEach() {
		customerService = new CustomerService(customerRepository);
			
		when(customerRepository.findById(1l)).thenReturn(
				Optional.of(
						Customer.builder().id(1l).name("John Smith").build())
				);
		
		when(customerRepository.findById(2l)).thenReturn(
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
	public void getByIdTest() {
		assertThat(customerService.getById(1l)).isNotNull();
		assertThat(customerService.getById(1l).getName()).isEqualTo("John Smith");
	}
	
	@Test
	public void getByIdNotFoundExceptionTest() {
		
		assertThrows(CustomerNotFound.class, () -> {
			customerService.getById(5l);
		});		
		
	}
}

