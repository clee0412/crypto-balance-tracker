package edu.itba.cryptotracker.web.presenter.platform;

import edu.itba.cryptotracker.web.dto.platform.PlatformResponseDTO;
import edu.itba.cryptotracker.domain.entity.platform.Platform;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Mapper between Platform domain entity and REST DTOs.
 */
@Component
public class PlatformRestMapper {

    /**
     * Maps from domain Platform to PlatformResponseDTO.
     */
    public PlatformResponseDTO toResponseDTO(Platform platform) {
        if (platform == null) {
            return null;
        }

        return new PlatformResponseDTO(
            platform.getId(),
            platform.getName()
        );
    }

    /**
     * Maps a list of Platforms to PlatformResponseDTOs.
     */
    public List<PlatformResponseDTO> toResponseDTOs(List<Platform> platforms) {
        if (platforms == null) {
            return List.of();
        }

        return platforms.stream()
                .map(this::toResponseDTO)
                .toList();
    }
}
