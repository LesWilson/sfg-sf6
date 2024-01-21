package guru.springframework.spring6restmvc.services;

import guru.springframework.spring6restmvc.dto.BeerDTO;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BeerService {

    List<BeerDTO> listBeers();
    Optional<BeerDTO> getBeerById(UUID id);

    BeerDTO saveNewBeer(BeerDTO beer);

    Optional<BeerDTO> update(UUID beerId, BeerDTO beer);

    boolean deleteById(UUID id);

    Optional<BeerDTO> patch(UUID beerId, BeerDTO beer);
}
