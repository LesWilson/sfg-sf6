package guru.springframework.spring6restmvc.services;

import guru.springframework.spring6restmvc.dto.BeerDTO;
import guru.springframework.spring6restmvc.dto.SearchDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BeerService {

    List<BeerDTO> listBeers();

    Page<BeerDTO> findBeers(SearchDTO searchDTO, Pageable pageable);

    List<BeerDTO> search(SearchDTO searchDTO);

    Optional<BeerDTO> getBeerById(UUID id);

    BeerDTO saveNewBeer(BeerDTO beer);

    Optional<BeerDTO> update(UUID beerId, BeerDTO beer);

    boolean deleteById(UUID id);

    Optional<BeerDTO> patch(UUID beerId, BeerDTO beer);
}
