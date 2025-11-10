package edu.itba.cryptotracker.application.usecase.platform;

import edu.itba.cryptotracker.domain.entity.platform.Platform;
import edu.itba.cryptotracker.domain.persistence.PlatformRepositoryPort;
import edu.itba.cryptotracker.domain.usecases.DeletePlatformUseCasePort;
import edu.itba.cryptotracker.domain.usecases.FindPlatformByIdUseCasePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeletePlatformUseCase implements DeletePlatformUseCasePort {
    
    private final PlatformRepositoryPort platformRepository;
    private final FindPlatformByIdUseCasePort findPlatformByIdUseCase;
    
    @Override
    public void deletePlatform(String platformId) {
        log.debug("Deleting platform with id: {}", platformId);
        
        Platform platform = findPlatformByIdUseCase.findById(platformId);
        
        // TODO: Add logic to delete associated user cryptos when that module is implemented
        // For now, we'll just delete the platform
        
        platformRepository.delete(platform);
        log.info("Platform deleted successfully: {}", platform);
    }
}