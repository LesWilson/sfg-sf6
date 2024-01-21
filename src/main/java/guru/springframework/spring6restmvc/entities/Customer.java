package guru.springframework.spring6restmvc.entities;

import jakarta.persistence.Entity;
import lombok.*;

@Getter
@Setter
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Customer extends ModelBase {

    private String name;
}
