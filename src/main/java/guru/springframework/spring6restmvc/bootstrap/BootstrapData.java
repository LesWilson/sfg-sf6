package guru.springframework.spring6restmvc.bootstrap;

import guru.springframework.spring6restmvc.entities.Beer;
import guru.springframework.spring6restmvc.entities.Customer;
import guru.springframework.spring6restmvc.dto.BeerStyle;
import guru.springframework.spring6restmvc.repositories.BeerRepository;
import guru.springframework.spring6restmvc.repositories.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
@Slf4j
public class BootstrapData implements CommandLineRunner {

    private final BeerRepository beerRepository;
    private final CustomerRepository customerRepository;

    @Override
    public void run(String... args) {

        long beerCount = beerRepository.count();
        if(beerCount == 0) {
            Beer innisAndGunn = new Beer();
            innisAndGunn.setBeerName("Innis & Gunn");
            innisAndGunn.setBeerStyle(BeerStyle.LAGER);
            innisAndGunn.setUpc("upc innis");
            innisAndGunn.setPrice(BigDecimal.valueOf(4.5));
            beerRepository.save(innisAndGunn);

            Beer tennents = new Beer();
            tennents.setBeerName("Tennents");
            tennents.setBeerStyle(BeerStyle.LAGER);
            tennents.setUpc("ten_upc");
            tennents.setPrice(BigDecimal.valueOf(4.1));
            beerRepository.save(tennents);

            Beer guinness = new Beer();
            guinness.setBeerName("Guinness");
            guinness.setBeerStyle(BeerStyle.STOUT);
            guinness.setUpc("guin_upc");
            guinness.setPrice(BigDecimal.valueOf(5.1));
            beerRepository.save(guinness);

            Beer cider = new Beer();
            cider.setBeerName("Kopaberg");
            cider.setBeerStyle(BeerStyle.CIDER);
            cider.setUpc("cider_upc");
            cider.setPrice(BigDecimal.valueOf(2.1));
            beerRepository.save(cider);
        }
        long customerCount = customerRepository.count();
        if(customerCount == 0) {
            Customer customer1 = new Customer();
            customer1.setName("Jack Reacher");
            customerRepository.save(customer1);

            Customer rebusCustomer = new Customer();
            rebusCustomer.setName("Rebus");
            customerRepository.save(rebusCustomer);
        }

        log.info("beer count: {}", beerRepository.count());
        log.info("customer count: {}", customerRepository.count());

    }
}
