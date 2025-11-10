package edu.itba.cryptotracker.adapter.input.rest.platform.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * DTO for platform responses.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PlatformResponseDTO {
    private String id;
    private String name;
}