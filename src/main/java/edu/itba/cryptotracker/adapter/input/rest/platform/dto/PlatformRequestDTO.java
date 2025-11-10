package edu.itba.cryptotracker.adapter.input.rest.platform.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * DTO for platform creation and update requests.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PlatformRequestDTO {
    
    @NotBlank(message = "Platform name is required")
    @Size(min = 1, max = 50, message = "Platform name must be between 1 and 50 characters")
    private String name;
}