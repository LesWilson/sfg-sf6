package guru.springframework.spring6restmvc.services;

import guru.springframework.spring6restmvc.dto.CustomerDTO;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CustomerService {

    List<CustomerDTO> listCustomers();
    Optional<CustomerDTO> getCustomerById(UUID id);

    CustomerDTO saveNewCustomer(CustomerDTO customer);

    Optional<CustomerDTO> update(UUID id, CustomerDTO customer);

    boolean deleteById(UUID id);

    Optional<CustomerDTO> patch(UUID id, CustomerDTO customer);
}
