package guru.springframework.spring6restmvc.entities;

import guru.springframework.spring6restmvc.dto.BeerStyle;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Beer extends ModelBase {
    @NotBlank
    @Size(max = 20)
    private String beerName;
    @NotNull
    private BeerStyle beerStyle;
    @NotBlank
    private String upc;
    private Integer quantityOnHand;
    @NotNull
    private BigDecimal price;
}
