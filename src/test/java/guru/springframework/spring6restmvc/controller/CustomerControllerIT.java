package guru.springframework.spring6restmvc.controller;

import guru.springframework.spring6restmvc.entities.Customer;
import guru.springframework.spring6restmvc.exception.NotFoundException;
import guru.springframework.spring6restmvc.mappers.CustomerMapper;
import guru.springframework.spring6restmvc.dto.CustomerDTO;
import guru.springframework.spring6restmvc.repositories.CustomerRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
class CustomerControllerIT {

    @Autowired
    CustomerController controller;
    @Autowired
    CustomerRepository repository;
    @Autowired
    EntityManager entityManager;
    @Autowired
    CustomerMapper mapper;

    @Transactional
    @Rollback
    @Test
    void deleteById() {
        CustomerDTO dto = mapper.modelToDto(repository.findAll().get(0));
        UUID id = dto.getId();
        ResponseEntity<Void> voidResponseEntity = controller.deleteById(id);
        assertThat(voidResponseEntity.getStatusCode(), is(equalTo(HttpStatus.NO_CONTENT)));

        boolean customerExists = repository.existsById(id);
        assertThat(customerExists, is(false));
    }

    @Test
    void getCustomers() {
        List<CustomerDTO> customers = controller.getCustomers();
        assertThat(customers, hasSize(2));
    }

    @Transactional
    @Rollback
    @Test
    void getEmptyListOfCustomers() {
        repository.deleteAll();
        List<CustomerDTO> customers = controller.getCustomers();
        assertThat(customers, hasSize(0));
    }

    @Test
    void getCustomerByIdThatExists() {
        CustomerDTO dto = mapper.modelToDto(repository.findAll().get(0));
        CustomerDTO customer = controller.getCustomerById(dto.getId());
        assertThat(customer, is(notNullValue()));
    }
    @Test
    void testGetCustomerByIdWithAnIdThatDoesNotExist() {
        assertThrows(NotFoundException.class, () -> controller.getCustomerById(UUID.randomUUID()));
    }

    @Transactional
    @Rollback
    @Test
    void createCustomer() {
        CustomerDTO dto = CustomerDTO.builder().name("new name").build();
        ResponseEntity<CustomerDTO> responseEntity = controller.createCustomer(dto);
        entityManager.flush();
        assertThat(responseEntity.getStatusCode(), is(equalTo(HttpStatus.CREATED)));
        URI location = responseEntity.getHeaders().getLocation();
        assertThat(location, is(notNullValue()));
        String[] pathSplit = location.getPath().split("/");
        String uuid = pathSplit[pathSplit.length-1];
        Optional<Customer> byId = repository.findById(UUID.fromString(uuid));
        assertThat(byId.isPresent(), is(true));
        Customer customer = byId.get();
        assertThat(customer.getName(), is(equalTo(dto.getName())));
        assertThat(customer.getVersion(), is(equalTo(0)));
        assertThat(customer.getCreateDate(), is(notNullValue()));
        assertThat(customer.getUpdateDate(), is(notNullValue()));
        assertThat(customer.getUpdateDate(), is(equalTo(customer.getCreateDate())));
    }

    @Test
    @Transactional
    @Rollback
    void updateById() {
        CustomerDTO dto = mapper.modelToDto(repository.findAll().get(0));
        dto.setName("updated name for test");
        ResponseEntity<Void> responseEntity = controller.updateById(dto.getId(), dto);
        entityManager.flush();
        assertThat(responseEntity.getStatusCode(), is(equalTo(HttpStatus.NO_CONTENT)));

        Optional<Customer> byId = repository.findById(dto.getId());
        assertThat(byId.isPresent(), is(true));
        Customer customer = byId.get();
        assertThat(customer.getName(), is(equalTo(dto.getName())));
        assertThat(customer.getVersion(), is(equalTo(dto.getVersion() + 1)));
        assertThat(customer.getCreateDate(), is(equalTo(dto.getCreatedDate())));
        assertThat(customer.getUpdateDate(), is(notNullValue()));
        assertThat(customer.getUpdateDate(), is(not(equalTo(customer.getCreateDate()))));
    }

    @Transactional
    @Rollback
    @Test
    void patchById() {
        CustomerDTO dto = mapper.modelToDto(repository.findAll().get(0));
        dto.setName("updated name for test");
        ResponseEntity<Void> responseEntity = controller.patchById(dto.getId(), dto);
        entityManager.flush();
        assertThat(responseEntity.getStatusCode(), is(equalTo(HttpStatus.NO_CONTENT)));

        Optional<Customer> byId = repository.findById(dto.getId());
        assertThat(byId.isPresent(), is(true));
        Customer customer = byId.get();
        assertThat(customer.getName(), is(equalTo(dto.getName())));
        assertThat(customer.getVersion(), is(equalTo(dto.getVersion() + 1)));
        assertThat(customer.getCreateDate(), is(equalTo(dto.getCreatedDate())));
        assertThat(customer.getUpdateDate(), is(notNullValue()));
        assertThat(customer.getUpdateDate(), is(not(equalTo(customer.getCreateDate()))));
    }

    @Test
    void testDeleteByNonExistentId() {
        assertThrows(NotFoundException.class, () -> controller.deleteById(UUID.randomUUID()));
    }
    @Test
    void testUpdateByNonExistentId() {
        assertThrows(NotFoundException.class, () -> controller.updateById(UUID.randomUUID(), CustomerDTO.builder().build()));
    }
    @Test
    void testPatchByNonExistentId() {
        assertThrows(NotFoundException.class, () -> controller.patchById(UUID.randomUUID(), CustomerDTO.builder().build()));
    }
}