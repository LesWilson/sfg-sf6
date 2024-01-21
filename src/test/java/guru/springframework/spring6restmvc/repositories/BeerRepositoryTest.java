package guru.springframework.spring6restmvc.repositories;

import guru.springframework.spring6restmvc.dto.BeerStyle;
import guru.springframework.spring6restmvc.entities.Beer;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;


@ActiveProfiles("test")
@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class BeerRepositoryTest {

    @Autowired
    BeerRepository repository;

    @Test
    void testSaveBeer() {
        Beer savedBeer = repository.save(
                Beer.builder()
                        .beerName("test beer")
                        .upc("UPC")
                        .beerStyle(BeerStyle.LAGER)
                        .price(BigDecimal.valueOf(4.5))
                        .build());
        repository.flush();
        assertThat(savedBeer, is(not(nullValue())));
        assertThat(savedBeer.getId(), is(not(nullValue())));
        assertThat(savedBeer.getBeerName(), is(equalTo("test beer")));

    }
    @Test
    void testSaveBeerWithInvalidLengthName() {
        assertThrows(ConstraintViolationException.class, () -> {
            repository.save(
                    Beer.builder()
                            .beerName("test beer longer than 20 characters")
                            .upc("UPC")
                            .beerStyle(BeerStyle.LAGER)
                            .price(BigDecimal.valueOf(4.5))
                            .build());
            repository.flush();
        });
    }
}