package guru.springframework.spring6restmvc.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import guru.springframework.spring6restmvc.dto.CustomerDTO;
import guru.springframework.spring6restmvc.services.CustomerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerController.class)
class CustomerControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper;

    @MockBean
    CustomerService service;

    @Test
    void getCustomerById() throws Exception {
        CustomerDTO customer = createCustomer("test Beer");
        given(service.getCustomerById(any(UUID.class)))
                .willReturn(Optional.of(customer));
        mockMvc.perform(get(CustomerController.CUSTOMER_PATH_WITH_ID, customer.getId())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(customer.getId().toString())))
                .andExpect(jsonPath("$.name", is(customer.getName())));

    }

    @Test
    void deleteById() throws Exception {
        UUID id = UUID.randomUUID();
        given(service.deleteById(id)).willReturn(true);
        mockMvc.perform(delete(CustomerController.CUSTOMER_PATH_WITH_ID, id)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
        verify(service).deleteById(id);
    }

    @Test
    void deleteByIdNotFound() throws Exception {
        UUID id = UUID.randomUUID();
        given(service.deleteById(any())).willReturn(false);
        mockMvc.perform(delete(CustomerController.CUSTOMER_PATH_WITH_ID, id)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
        verify(service).deleteById(id);
    }

    @Test
    void getCustomers() throws Exception {
        CustomerDTO customer1 = createCustomer("test Beer 1");
        CustomerDTO customer2 = createCustomer("test Beer 2");
        CustomerDTO customer3 = createCustomer("test Beer 3");

        given(service.listCustomers()).willReturn(List.of(customer1, customer2, customer3));

        mockMvc.perform(get(CustomerController.CUSTOMER_PATH)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()", is(3)))
                .andExpect(jsonPath("$.[0].id", is(customer1.getId().toString())))
                .andExpect(jsonPath("$.[1].id", is(customer2.getId().toString())))
                .andExpect(jsonPath("$.[2].id", is(customer3.getId().toString())))
        ;

    }

    @Test
    void creatCustomer() throws Exception {
        CustomerDTO customer = createCustomer("Innis & Gunn Tap Room");
        given(service.saveNewCustomer(any(CustomerDTO.class))).willReturn(customer);
        mockMvc.perform(post(CustomerController.CUSTOMER_PATH)
                .content(mapper.writeValueAsBytes(customer))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id", is(customer.getId().toString())))
                .andExpect(jsonPath("$.name", is(customer.getName())));
    }

    @Test
    void updateById() throws Exception {
        CustomerDTO customer = createCustomer("testCustomer");
        UUID id = customer.getId();
        given(service.update(eq(id), any(CustomerDTO.class))).willReturn(Optional.of(customer));

        mockMvc.perform(put(CustomerController.CUSTOMER_PATH_WITH_ID, customer.getId())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(customer)))
                .andExpect(status().isNoContent());
        verify(service).update(eq(id), any(CustomerDTO.class));
    }
    @Test
    void updateByIdNotFound() throws Exception {
        CustomerDTO customer = createCustomer("testCustomer");
        UUID id = customer.getId();
        given(service.update(eq(id), any(CustomerDTO.class))).willReturn(Optional.empty());

        mockMvc.perform(put(CustomerController.CUSTOMER_PATH_WITH_ID, id)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(customer)))
                .andExpect(status().isNotFound());
        verify(service).update(eq(id), any(CustomerDTO.class));
    }

    @Test
    void patchById() throws Exception {
        CustomerDTO customer = createCustomer("new name");
        UUID id = UUID.randomUUID();
        given(service.patch(any(UUID.class), any(CustomerDTO.class))).willReturn(Optional.of(customer));

        mockMvc.perform(patch(CustomerController.CUSTOMER_PATH_WITH_ID, id)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(customer)))
                .andExpect(status().isNoContent());
        verify(service).patch(any(UUID.class), any(CustomerDTO.class));
    }
    @Test
    void patchByIdNotFound() throws Exception {
        CustomerDTO customer = createCustomer("new name");
        UUID id = customer.getId();
        given(service.patch(eq(id), any(CustomerDTO.class))).willReturn(Optional.empty());

        mockMvc.perform(patch(CustomerController.CUSTOMER_PATH_WITH_ID, customer.getId())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(customer)))
                .andExpect(status().isNotFound());
        verify(service).patch(eq(id), any(CustomerDTO.class));
    }

    @Test
    void getCustomerByIdNotFound() throws Exception {
        given(service.getCustomerById(any(UUID.class)))
                .willReturn(Optional.empty());
        mockMvc.perform(get(CustomerController.CUSTOMER_PATH_WITH_ID, UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }
    private CustomerDTO createCustomer(String name) {
        CustomerDTO dto = CustomerDTO.builder()
                .name(name)
                .build();
        dto.setId(UUID.randomUUID());
        return dto;
    }
}