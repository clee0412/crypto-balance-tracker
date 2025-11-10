package edu.itba.cryptotracker.adapter.output.persistence.jpa.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
    name = "platforms",
    indexes = {
        @Index(name = "idx_platform_name", columnList = "name", unique = true)
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class PlatformEntity {
    
    @Id
    @EqualsAndHashCode.Include
    @Column(name = "id", length = 36)
    private String id;
    
    @Column(name = "name", nullable = false, unique = true, length = 50)
    private String name;
}