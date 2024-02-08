package guru.springframework.spring6restmvc.services;

import guru.springframework.spring6restmvc.dto.BeerDTO;
import guru.springframework.spring6restmvc.dto.SearchDTO;
import guru.springframework.spring6restmvc.entities.Beer;
import guru.springframework.spring6restmvc.mappers.BeerMapper;
import guru.springframework.spring6restmvc.repositories.BeerRepository;
import guru.springframework.spring6restmvc.repositories.BeerSpecifications;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static org.apache.commons.lang3.StringUtils.defaultIfBlank;

@Slf4j
@Service
public class BeerServiceImpl implements BeerService {

    private final BeerRepository repository;
    private final BeerMapper mapper;
    private final EntityManager entityManager;
    private CriteriaBuilder criteriaBuilder;

    public BeerServiceImpl(BeerRepository repository, BeerMapper mapper, EntityManager entityManager) {
        this.repository = repository;
        this.mapper = mapper;
        this.entityManager = entityManager;
        this.criteriaBuilder = entityManager.getCriteriaBuilder();
    }

    @Override
    public List<BeerDTO> listBeers() {
        return repository.findAll()
                .stream()
                .map(mapper::modelToDto)
                .toList();
    }

    @Override
    public Page<BeerDTO> findBeers(SearchDTO searchDTO, Pageable pageable) {
        Specification<Beer> spec = Specification.where(null);
        if(CollectionUtils.isNotEmpty(searchDTO.getBeerStyles())) {
            spec = spec.and(BeerSpecifications.hasBeerStyle(searchDTO.getBeerStyles()));
        }
        if(StringUtils.isNotEmpty(searchDTO.getBeerName())) {
            spec = spec.and(BeerSpecifications.hasBeerNameLike(searchDTO.getBeerName()));
        }
        return repository.findAll(spec, pageable).map(mapper::modelToDto);

    }
    @Override
    public List<BeerDTO> search(SearchDTO searchDTO) {
//        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
//        CriteriaQuery<Beer> criteriaQuery = criteriaBuilder.createQuery(Beer.class);
//        Root<Beer> beer = criteriaQuery.from(Beer.class);
//        criteriaQuery.where(criteriaBuilder.like(beer.get("beerName"), searchDTO.getBeerName()));
//        final List<String> styles = searchDTO.getBeerStyles().stream().map(BeerStyle::ordinal).map(String::valueOf).toList();
//        criteriaQuery.where(criteriaBuilder.in(beer.get("beerStyle")).value(styles));
//
////        criteriaBuilder.in(employee.get("firstName")).value("Bob").value("Fred").value("Joe")
////        employee.get("firstName").in("Bob", "Fred", "Joe")
////        employee.get("firstName").in(criteriaBuilder.parameter(List.class, "names")
//
//        Query query = entityManager.createQuery(criteriaQuery);
//        List<Beer> result = query.getResultList();

//        criteriaBuilder.and(
//        criteriaBuilder.equal(employee.get("firstName"), "Bob"),
//        criteriaBuilder.equal(employee.get("lastName"), "Smith")
//        )

        CriteriaQuery<Beer> criteriaQuery = criteriaBuilder.createQuery(Beer.class);
        Root<Beer> root = criteriaQuery.from(Beer.class);
        final CriteriaQuery<Beer> select = criteriaQuery.select(root);
        List<Order> orderBys = List.of(criteriaBuilder.asc(root.get("beerStyle")), criteriaBuilder.desc(root.get("beerName")));
        Predicate queryPredicate = null;
        if(CollectionUtils.isNotEmpty(searchDTO.getBeerStyles())) {
            queryPredicate = root.get("beerStyle").in(searchDTO.getBeerStyles());
        }

        if(StringUtils.isNotEmpty(searchDTO.getBeerName())) {
            Predicate beerNamePredicate = criteriaBuilder.like(root.get("beerName"), "%" + searchDTO.getBeerName() + "%");
            if(queryPredicate != null) {
                queryPredicate = criteriaBuilder.and(queryPredicate, beerNamePredicate);
            } else {
                queryPredicate = beerNamePredicate;
            }
        }
        select.where(queryPredicate).orderBy(orderBys);

        TypedQuery<Beer> query = entityManager.createQuery(select);
        return query.getResultList()
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
