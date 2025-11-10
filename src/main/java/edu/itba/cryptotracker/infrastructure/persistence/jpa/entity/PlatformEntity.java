package edu.itba.cryptotracker.infrastructure.persistence.jpa.entity;

import jakarta.persistence.*;
import lombok.*;
@Entity
@Table(name = "platforms")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlatformEntity {

    @Id
    @Column(name = "id", nullable = false, length = 100)
    private String id;

    @Column(name = "name", nullable = false)
    private String name;
}
