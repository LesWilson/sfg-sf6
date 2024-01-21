package guru.springframework.spring6restmvc.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import guru.springframework.spring6restmvc.dto.BeerDTO;
import guru.springframework.spring6restmvc.dto.BeerStyle;
import guru.springframework.spring6restmvc.dto.ErrorInfo;
import guru.springframework.spring6restmvc.services.BeerService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BeerController.class)
class BeerControllerTest {

    public static final String MUST_NOT_BE_BLANK = "must not be blank";
    public static final String MUST_NOT_BE_NULL = "must not be null";
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper;

    @MockBean
    BeerService service;

    @Captor
    ArgumentCaptor<UUID> uuidArgumentCaptor;
//    @Captor
//    ArgumentCaptor<BeerDTO> beerArgumentCaptor;

    @Test
    void getBeerById() throws Exception {
        BeerDTO beer = createBeer("test Beer");
        given(service.getBeerById(any(UUID.class))).willReturn(Optional.of(beer));

        mockMvc.perform(get(BeerController.BEER_PATH_WITH_ID, beer.getId())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(beer.getId().toString())))
                .andExpect(jsonPath("$.beerName", is(beer.getBeerName())));
    }

    @Test
    void testCreateNewBeer() throws Exception {
        BeerDTO beer = createBeer("Innis & Gunn");
        given(service.saveNewBeer(any(BeerDTO.class))).willReturn(beer);
        mockMvc.perform(post(BeerController.BEER_PATH)
                .content(mapper.writeValueAsBytes(beer))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id", is(beer.getId().toString())))
                .andExpect(jsonPath("$.beerName", is(beer.getBeerName())));
    }

    @Test
    void testCreateNewBeerWithNoBeerName() throws Exception {
        BeerDTO beer = createBeer(null);
        given(service.saveNewBeer(any(BeerDTO.class))).willReturn(beer);
        MvcResult mvcResult = mockMvc.perform(post(BeerController.BEER_PATH)
                .content(mapper.writeValueAsBytes(beer))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.length()", is(1)))
                .andExpect(jsonPath("$.[0].fieldName", is("beerName")))
                .andExpect(jsonPath("$.[0].errorDescription", is("must not be blank")))
                .andReturn();
        System.out.println(mvcResult.getResponse().getContentAsString());
    }

    @Test
    void testCreateNewBeerWithInvalidValues() throws Exception {
        BeerDTO beer = createBeer(null);
        beer.setUpc(null);
        beer.setBeerStyle(null);
        beer.setPrice(null);
        given(service.saveNewBeer(any(BeerDTO.class))).willReturn(beer);
        MvcResult mvcResult = mockMvc.perform(post(BeerController.BEER_PATH)
                .content(mapper.writeValueAsBytes(beer))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.length()", is(4)))
                .andExpect(jsonPath("$.[*].fieldName", containsInAnyOrder("beerName", "upc", "beerStyle", "price")))
                .andReturn();

        List<ErrorInfo> expectedErrors = List.of(
                ErrorInfo.builder().fieldName("beerName").errorDescription(MUST_NOT_BE_BLANK).build(),
                ErrorInfo.builder().fieldName("upc").errorDescription(MUST_NOT_BE_BLANK).build(),
                ErrorInfo.builder().fieldName("beerStyle").errorDescription(MUST_NOT_BE_NULL).build(),
                ErrorInfo.builder().fieldName("price").errorDescription(MUST_NOT_BE_NULL).build());
        List<ErrorInfo> returnedErrors = mapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>() {});

        expectedErrors.forEach(error -> assertThat(returnedErrors, hasItem(error)));
    }
    @Test
    void deleteById() throws Exception {
        UUID id = UUID.randomUUID();
        given(service.deleteById(id)).willReturn(true);
        mockMvc.perform(delete(BeerController.BEER_PATH_WITH_ID, id)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
        verify(service).deleteById(uuidArgumentCaptor.capture());
        assertThat(uuidArgumentCaptor.getValue(), is(id));
        verify(service).deleteById(id);
    }

    @Test
    void updateById() throws Exception {
        BeerDTO beer = createBeer("test Beer");
        given(service.update(any(UUID.class), any(BeerDTO.class))).willReturn(Optional.of(beer));
        mockMvc.perform(put(BeerController.BEER_PATH_WITH_ID, beer.getId())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(beer)))
                .andExpect(status().isNoContent());
        verify(service).update(any(UUID.class), any(BeerDTO.class));
    }

    @Test
    void patchById() throws Exception {
        BeerDTO beer = BeerDTO.builder().beerName("new name").build();
        UUID id = UUID.randomUUID();
        given(service.patch(eq(id), any(BeerDTO.class))).willReturn(Optional.of(beer));

        mockMvc.perform(patch(BeerController.BEER_PATH_WITH_ID, id)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(beer)))
                .andExpect(status().isNoContent());
        verify(service).patch(eq(id), any(BeerDTO.class));
    }

    @Test
    void getBeers() throws Exception {
        BeerDTO beer1 = createBeer("test Beer 1");
        BeerDTO beer2 = createBeer("test Beer 2");
        BeerDTO beer3 = createBeer("test Beer 3");

        given(service.listBeers()).willReturn(List.of(beer1, beer2, beer3));

        mockMvc.perform(get(BeerController.BEER_PATH)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()", is(3)))
                .andExpect(jsonPath("$.[0].id", is(beer1.getId().toString())))
                .andExpect(jsonPath("$.[1].id", is(beer2.getId().toString())))
                .andExpect(jsonPath("$.[2].id", is(beer3.getId().toString())))
        ;
    }

    @Test
    void getBeerByIdNotFound() throws Exception {
        given(service.getBeerById(any(UUID.class)))
                .willReturn(Optional.empty());
        mockMvc.perform(get(BeerController.BEER_PATH_WITH_ID, UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateBeerByIdNotFound() throws Exception {
        BeerDTO beer = BeerDTO.builder().beerName("new name").build();
        given(service.update(beer.getId(), beer)).willReturn(Optional.empty());
        mockMvc.perform(put(BeerController.BEER_PATH_WITH_ID, beer.getId())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(beer)))
                .andExpect(status().isNotFound());
    }

    @Test
    void patchByIdNotFound() throws Exception {
        BeerDTO beer = BeerDTO.builder().beerName("new name").build();
        UUID id = UUID.randomUUID();
        given(service.patch(eq(id), any(BeerDTO.class))).willReturn(Optional.empty());

        mockMvc.perform(patch(BeerController.BEER_PATH_WITH_ID, id)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(beer)))
                .andExpect(status().isNotFound());
        verify(service).patch(eq(id), any(BeerDTO.class));
    }

    private BeerDTO createBeer(String beerName) {
        BeerDTO dto = BeerDTO.builder()
                .beerName(beerName)
                .beerStyle(BeerStyle.IPA)
                .upc("this is a upc")
                .quantityOnHand(123)
                .price(new BigDecimal("1.23"))
                .build();
        dto.setId(UUID.randomUUID());
        return dto;
    }

}