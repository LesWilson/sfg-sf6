package guru.springframework.spring6restmvc.dto;

import lombok.*;

@Getter
@Builder
@EqualsAndHashCode
public class ErrorInfo {
    private final String fieldName;
    private final String errorDescription;
}
