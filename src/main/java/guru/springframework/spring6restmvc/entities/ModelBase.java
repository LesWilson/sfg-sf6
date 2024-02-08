package guru.springframework.spring6restmvc.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@MappedSuperclass
public class ModelBase {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 36, columnDefinition = "UUID", updatable = false, nullable = false)
    private UUID id;
    @Version
    private Integer version;
    private LocalDateTime createDate;
    private LocalDateTime updateDate;
    private String createUser;
    private String lastUpdateUser;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createDate = now;
        updateDate = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updateDate = LocalDateTime.now();
    }
}
