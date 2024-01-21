package guru.springframework.spring6restmvc.controller;

import guru.springframework.spring6restmvc.exception.NotFoundException;
import guru.springframework.spring6restmvc.dto.BeerDTO;
import guru.springframework.spring6restmvc.services.BeerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping()
public class BeerController {

    public static final String BEER_PATH = "/api/v1/beer";
    public static final String BEER_PATH_WITH_ID = BEER_PATH + "/{id}";
    private final BeerService beerService;

    @DeleteMapping(BEER_PATH_WITH_ID)
    public ResponseEntity<Void> deleteById(@PathVariable("id") UUID id) {
        if(beerService.deleteById(id)) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        throw new NotFoundException();
    }
    @PostMapping(BEER_PATH)
    public ResponseEntity<BeerDTO> createBeer(@Validated @RequestBody BeerDTO beer) {
        BeerDTO savedBeer = beerService.saveNewBeer(beer);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", BEER_PATH+"/"+savedBeer.getId());
        return new ResponseEntity<>(savedBeer, headers, HttpStatus.CREATED);
    }

    @PutMapping(BEER_PATH_WITH_ID)
    public ResponseEntity<Void> updateById(@PathVariable("id") UUID beerId, @Validated @RequestBody BeerDTO beer) {
        if(beerService.update(beerId, beer).isEmpty()) {
            throw new NotFoundException();
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT) ;
    }

    @PatchMapping(BEER_PATH_WITH_ID)
    public ResponseEntity<Void> patchById(@PathVariable("id") UUID beerId, @RequestBody BeerDTO beer) {
        if(beerService.patch(beerId, beer).isEmpty()) {
            throw new NotFoundException();
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping(BEER_PATH)
    public List<BeerDTO> getBeers() {
        return beerService.listBeers();
    }

    @GetMapping(BEER_PATH_WITH_ID)
    public BeerDTO getBeerById(@PathVariable("id") UUID id) {
        log.info("in beer controller with id: {}", id);
        return beerService.getBeerById(id).orElseThrow(NotFoundException::new);
    }

}
