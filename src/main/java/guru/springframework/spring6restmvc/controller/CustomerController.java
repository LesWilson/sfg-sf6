package guru.springframework.spring6restmvc.controller;

import guru.springframework.spring6restmvc.exception.NotFoundException;
import guru.springframework.spring6restmvc.dto.CustomerDTO;
import guru.springframework.spring6restmvc.services.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping()
public class CustomerController {

    public static final String CUSTOMER_PATH = "/api/v1/customer";
    public static final String CUSTOMER_PATH_WITH_ID = CUSTOMER_PATH + "/{id}";

    private final CustomerService service;

    @DeleteMapping(CUSTOMER_PATH_WITH_ID)
    public ResponseEntity<Void> deleteById(@PathVariable("id") UUID id) {
        if(service.deleteById(id)) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        throw new NotFoundException();
    }

    @GetMapping(CUSTOMER_PATH)
    public List<CustomerDTO> getCustomers() {
        return service.listCustomers();
    }

    @GetMapping(CUSTOMER_PATH_WITH_ID)
    public CustomerDTO getCustomerById(@PathVariable("id") UUID id) {
        log.debug("in customer controller with id: {}", id);
        return service.getCustomerById(id).orElseThrow(NotFoundException::new);
    }

    @PostMapping(CUSTOMER_PATH)
    public ResponseEntity<CustomerDTO> createCustomer(@RequestBody CustomerDTO customer) {
        CustomerDTO savedCustomer = service.saveNewCustomer(customer);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", "/api/v1/customer/"+savedCustomer.getId());
        return new ResponseEntity<>(savedCustomer, headers, HttpStatus.CREATED);
    }

    @PutMapping(CUSTOMER_PATH_WITH_ID)
    public ResponseEntity<Void> updateById(@PathVariable("id") UUID id, @RequestBody CustomerDTO customer) {
        if(service.update(id, customer).isEmpty()) {
            throw new NotFoundException();
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping(CUSTOMER_PATH_WITH_ID)
    public ResponseEntity<Void> patchById(@PathVariable("id") UUID id, @RequestBody CustomerDTO customer) {
        if(service.patch(id, customer).isEmpty()) {
            throw new NotFoundException(String.format("Customer with id {} not found", id));
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
