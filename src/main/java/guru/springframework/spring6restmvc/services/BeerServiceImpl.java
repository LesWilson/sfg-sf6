package guru.springframework.spring6restmvc.services;

import guru.springframework.spring6restmvc.entities.Beer;
import guru.springframework.spring6restmvc.mappers.BeerMapper;
import guru.springframework.spring6restmvc.dto.BeerDTO;
import guru.springframework.spring6restmvc.repositories.BeerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static org.apache.commons.lang3.StringUtils.defaultIfBlank;

@Slf4j
@RequiredArgsConstructor
@Service
public class BeerServiceImpl implements BeerService {

    private final BeerRepository repository;
    private final BeerMapper mapper;

    @Override
    public List<BeerDTO> listBeers() {
        return repository.findAll()
                .stream()
                .map(mapper::modelToDto)
                .toList();
    }

    @Override
    public Optional<BeerDTO> getBeerById(UUID id) {
        log.debug("retrieving beer for id: {}", id);
        return Optional.ofNullable(mapper.modelToDto(repository.findById(id).orElse(null)));
    }

    @Override
    public BeerDTO saveNewBeer(BeerDTO beerDTO) {
        Beer beer = mapper.dtoToModel(beerDTO);
        Beer savedBeer = repository.save(beer);
        return mapper.modelToDto(savedBeer);
    }

    @Override
    public Optional<BeerDTO> update(UUID beerId, BeerDTO beer) {
        AtomicReference<Optional<BeerDTO>> beerReference = new AtomicReference<>();
        repository.findById(beerId).ifPresentOrElse( existingBeer -> {
            existingBeer.setBeerName(beer.getBeerName());
            existingBeer.setBeerStyle(beer.getBeerStyle());
            existingBeer.setPrice(beer.getPrice());
            existingBeer.setQuantityOnHand(beer.getQuantityOnHand());
            existingBeer.setUpc(beer.getUpc());
            Beer savedBeer = repository.save(existingBeer);
            beerReference.set(Optional.of(mapper.modelToDto(savedBeer)));
        }, () -> beerReference.set(Optional.empty()));
        return beerReference.get();
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
    public Optional<BeerDTO> patch(UUID beerId, BeerDTO beer) {
        AtomicReference<Optional<BeerDTO>> atomicReference = new AtomicReference<>();
        repository.findById(beerId).ifPresentOrElse(existingBeer -> {
            existingBeer.setBeerName(defaultIfBlank(beer.getBeerName(), existingBeer.getBeerName()));
            if (beer.getBeerStyle() != null) {
                existingBeer.setBeerStyle(beer.getBeerStyle());
            }
            if (beer.getPrice() != null) {
                existingBeer.setPrice(beer.getPrice());
            }
            if (beer.getQuantityOnHand() != null) {
                existingBeer.setQuantityOnHand(beer.getQuantityOnHand());
            }
            existingBeer.setUpc(defaultIfBlank(beer.getUpc(), existingBeer.getUpc()));
            Beer saved = repository.save(existingBeer);
            atomicReference.set(Optional.of(mapper.modelToDto(saved)));
        }, () -> atomicReference.set(Optional.empty()));
        return atomicReference.get();
    }
}
