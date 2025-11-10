//package edu.itba.cryptotracker.application.usecase.platform;
//
//import edu.itba.cryptotracker.domain.entity.platform.Platform;
//import edu.itba.cryptotracker.domain.exception.DuplicatedPlatformException;
//import edu.itba.cryptotracker.domain.usecase.platform.FindPlatformByIdUseCase;
//import edu.itba.cryptotracker.domain.usecase.platform.UpdatePlatformUseCase;
//import edu.itba.cryptotracker.domain.gateway.PlatformRepositoryGateway;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//
//@Slf4j
//@Service
//@RequiredArgsConstructor
//public class UpdatePlatformUseCaseImpl implements UpdatePlatformUseCase {
//
//    private final PlatformRepositoryGateway platformRepository;
//    private final FindPlatformByIdUseCase findPlatformByIdUseCase;
//
//    @Override
//    public Platform updatePlatform(String platformId, String platformName) {
//        log.debug("Updating platform with id: {} to name: {}", platformId, platformName);
//
//        Platform existingPlatform = findPlatformByIdUseCase.findById(platformId);
//        String normalizedName = platformName.toUpperCase();
//
//        // Check if name is different and validate it doesn't exist
//        if (!existingPlatform.getName().equals(normalizedName)) {
//            validatePlatformNameNotExists(normalizedName);
//        }
//
//        Platform updatedPlatform = Platform.reconstitute(platformId, normalizedName);
//        Platform savedPlatform = platformRepository.save(updatedPlatform);
//
//        log.info("Platform updated successfully. Before: {}, After: {}", existingPlatform, savedPlatform);
//        return savedPlatform;
//    }
//
//    private void validatePlatformNameNotExists(String platformName) {
//        if (platformRepository.existsByName(platformName)) {
//            log.warn("Platform already exists with name: {}", platformName);
//            throw new DuplicatedPlatformException("Platform already exists with name: " + platformName);
//        }
//    }
//}
