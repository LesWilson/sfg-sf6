package guru.springframework.spring6restmvc.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import guru.springframework.spring6restmvc.dto.BeerDTO;
import guru.springframework.spring6restmvc.dto.BeerStyle;
import guru.springframework.spring6restmvc.entities.Beer;
import guru.springframework.spring6restmvc.exception.NotFoundException;
import guru.springframework.spring6restmvc.mappers.BeerMapper;
import guru.springframework.spring6restmvc.repositories.BeerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles({"test"})
class BeerControllerIT {

    @Autowired
    private BeerController controller;
    @Autowired
    private BeerRepository repository;
    @Autowired
    private BeerMapper mapper;
    @Autowired
    private WebApplicationContext wac;
    @Autowired
    private ObjectMapper objectMapper;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
    @Transactional
    @Rollback
    void testDeleteById() {
        UUID idToDelete = repository.findAll().get(0).getId();
        ResponseEntity<Void> responseEntity = controller.deleteById(idToDelete);
        assertThat(responseEntity.getStatusCode(), is(equalTo(HttpStatus.NO_CONTENT)));
        Optional<Beer> byId = repository.findById(idToDelete);
        assertThat(byId.isPresent(), is(false));
    }

    @Test
    void testDeleteWhenNotFound() {
        assertThrows(NotFoundException.class, () -> controller.deleteById(UUID.randomUUID()));
    }

    @Test
    @Transactional
    @Rollback
    void testCreateBeer() {
        BeerDTO dto = BeerDTO.builder()
                .beerName("New Beer")
                .beerStyle(BeerStyle.IPA)
                .price(BigDecimal.valueOf(3.6))
                .quantityOnHand(10)
                .upc("upc1")
                .build();

        ResponseEntity<BeerDTO> beerDTOResponseEntity = controller.createBeer(dto);
        assertThat(beerDTOResponseEntity.getStatusCode(), is(equalTo(HttpStatusCode.valueOf(201))));
        URI location = beerDTOResponseEntity.getHeaders().getLocation();
        assertThat(location, is(notNullValue()));
        String[] pathSplit = location.getPath().split("/");
        String uuid = pathSplit[pathSplit.length-1];
        Optional<Beer> byId = repository.findById(UUID.fromString(uuid));
        assertThat(byId.isPresent(), is(true));
        Beer beer = byId.get();
        assertThat(beer.getBeerName(), is(equalTo(dto.getBeerName())));
        assertThat(beer.getVersion(), is(equalTo(0)));
        assertThat(beer.getCreateDate(), is(notNullValue()));
        assertThat(beer.getUpc(), is(equalTo(dto.getUpc())));
        assertThat(repository.findAll(), hasSize(4));
    }

    @Test
    @Rollback
    @Transactional
    void updateById() {
        BeerDTO beerDTO = mapper.modelToDto(repository.findAll().get(0));
        beerDTO.setBeerName(beerDTO.getBeerName() + ":Upd");
        controller.updateById(beerDTO.getId(), beerDTO);
        repository.flush();
        Optional<Beer> byId = repository.findById(beerDTO.getId());
        assertThat(byId.isPresent(), is(true));
        Beer beer = byId.get();
        assertThat(beer.getBeerName(), is(equalTo(beerDTO.getBeerName())));
        assertThat(beer.getVersion(), is(beerDTO.getVersion()+1));
        assertThat(beer.getUpdateDate(), is(not(equalTo(beerDTO.getUpdateDate()))));
        assertThat(beer.getCreateDate(), is(equalTo(beerDTO.getCreatedDate())));
    }

    @Test
    void testUpdateOfNonExistentBeerThrowsException() {
        assertThrows(NotFoundException.class, () -> controller.updateById(UUID.randomUUID(), BeerDTO.builder().build()));
    }

    @Test
    @Transactional
    @Rollback
    void patchById() {
        BeerDTO dto = mapper.modelToDto(repository.findAll().get(0));
        dto.setBeerName(dto.getBeerName() + "Upd");
        dto.setQuantityOnHand(101);
        controller.patchById(dto.getId(), dto);
        repository.flush();
        Optional<Beer> byId = repository.findById(dto.getId());
        assertThat(byId.isPresent(), is(true));
        Beer beer = byId.get();
        assertThat(beer.getBeerName(), is(equalTo(dto.getBeerName())));
        assertThat(beer.getQuantityOnHand(), is(equalTo(dto.getQuantityOnHand())));
        assertThat(beer.getVersion(), is(dto.getVersion()+1));
        assertThat(beer.getCreateDate(), is(equalTo(dto.getCreatedDate())));
        assertThat(beer.getUpdateDate(), is(not(equalTo(dto.getUpdateDate()))));
    }

    @Test
    void testGetBeers() {
        List<BeerDTO> beers = controller.getBeers();
        assertThat(beers, hasSize(3));
    }

    @Test
    @Transactional
    @Rollback
    void testGetBeersReturnsEmptyList() {
        repository.deleteAll();
        List<BeerDTO> beers = controller.getBeers();
        assertThat(beers, hasSize(0));
    }

    @Test
    void testGetBeerByIdWithIdThatExists() {
        UUID id = repository.findAll().get(0).getId();
        BeerDTO beer = controller.getBeerById(id);
        assertThat(beer, is(notNullValue()));
    }
    @Test
    void testGetBeerByIdWithAnIdThatDoesNotExist() {
        assertThrows(NotFoundException.class, () -> controller.getBeerById(UUID.randomUUID()));
    }

    @Test
    void patchByIdWithInvalidName() throws Exception {
        BeerDTO dto = mapper.modelToDto(repository.findAll().get(0));
        dto.setBeerName(dto.getBeerName() + "Updated too long");
        dto.setQuantityOnHand(101);

        MvcResult mvcResult = mockMvc.perform(patch(BeerController.BEER_PATH_WITH_ID, dto.getId())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.length()", is(equalTo(1))))
                .andReturn();
        System.out.println(mvcResult.getResponse().getContentAsString());
    }


}