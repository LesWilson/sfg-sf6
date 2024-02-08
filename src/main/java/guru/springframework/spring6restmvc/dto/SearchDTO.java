package guru.springframework.spring6restmvc.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class SearchDTO {

    private String beerName;
    private List<BeerStyle> beerStyles;
}
