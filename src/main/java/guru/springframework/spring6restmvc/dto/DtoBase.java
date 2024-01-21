package guru.springframework.spring6restmvc.dto;

import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@MappedSuperclass
public class DtoBase {
    private UUID id;
    private Integer version = 0;
    private LocalDateTime createdDate = LocalDateTime.now();
    private LocalDateTime updateDate = LocalDateTime.now();
}
