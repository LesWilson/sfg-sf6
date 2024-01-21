package guru.springframework.spring6restmvc.dto;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDTO extends DtoBase {
    private String name;
}
