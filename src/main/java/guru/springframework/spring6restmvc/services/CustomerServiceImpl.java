package guru.springframework.spring6restmvc.services;

import guru.springframework.spring6restmvc.entities.Customer;
import guru.springframework.spring6restmvc.mappers.CustomerMapper;
import guru.springframework.spring6restmvc.dto.CustomerDTO;
import guru.springframework.spring6restmvc.repositories.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static org.apache.commons.lang3.StringUtils.defaultIfEmpty;

@Slf4j
@RequiredArgsConstructor
@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository repository;
    private final CustomerMapper mapper;

    @Override
    public List<CustomerDTO> listCustomers() {
        return repository.findAll()
                .stream()
                .map(mapper::modelToDto)
                .toList();
    }

    @Override
    public Optional<CustomerDTO> getCustomerById(UUID id) {
        log.debug("retrieving customer for id: {}", id);
        return Optional.ofNullable(mapper.modelToDto(repository.findById(id).orElse(null)));
    }

    @Override
    public CustomerDTO saveNewCustomer(CustomerDTO customerDTO) {
        Customer customer = mapper.dtoToModel(customerDTO);
        Customer saved = repository.save(customer);
        return mapper.modelToDto(saved);
    }

    @Override
    public Optional<CustomerDTO> update(UUID id, CustomerDTO customer) {
        AtomicReference<Optional<CustomerDTO>> referenceToOptional = new AtomicReference<>();
        repository.findById(id).ifPresentOrElse( existingCustomer -> {
            existingCustomer.setName(customer.getName());
            Customer savedCustomer = repository.save(existingCustomer);
            referenceToOptional.set(Optional.of(mapper.modelToDto(savedCustomer)));
        }, () -> referenceToOptional.set(Optional.empty()));

        return referenceToOptional.get();
    }

    @Override
    public boolean deleteById(UUID id) {
        if(repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public Optional<CustomerDTO> patch(UUID id, CustomerDTO customer) {
        AtomicReference<Optional<CustomerDTO>> atomicReference = new AtomicReference<>();
        Optional<CustomerDTO> optionalCustomer = getCustomerById(id);
        repository.findById(id).ifPresentOrElse(existingCustomer -> {
            existingCustomer.setName(defaultIfEmpty(customer.getName(), existingCustomer.getName()));
            Customer saved = repository.save(existingCustomer);
            atomicReference.set(Optional.of(mapper.modelToDto(saved)));
        }, () -> atomicReference.set(Optional.empty()));
        return optionalCustomer;
    }
}
