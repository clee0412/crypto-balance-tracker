package edu.itba.cryptotracker.domain.model.platform;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.Collections;
import java.util.Objects;
import java.util.UUID;

@Getter
@ToString
@EqualsAndHashCode
public class Platform {
    private String id;
    private String name;

    public Platform(String id, String name) {
        this.id = Objects.requireNonNull(id);
        this.name = name; // throw illegal arguemnt exception if anme is null or isBlank
    }

    // factory method to create new platform
    public static Platform create(String name) {
        return new Platform(UUID.randomUUID().toString(), name);
    }

    // factory method to reconstitue from DB
    public static Platform reconstitute(String id, String name) {
        return new Platform(id, name);
    }
}
