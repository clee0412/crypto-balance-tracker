//package edu.itba.cryptotracker.application.usecase.platform;
//
//import edu.itba.cryptotracker.domain.entity.platform.Platform;
//import edu.itba.cryptotracker.domain.exception.DuplicatedPlatformException;
//import edu.itba.cryptotracker.domain.usecase.platform.SavePlatformUseCase;
//import edu.itba.cryptotracker.domain.gateway.PlatformRepositoryGateway;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//
//@Slf4j
//@Service
//@RequiredArgsConstructor
//public class SavePlatformUseCaseImpl implements SavePlatformUseCase {
//
//    private final PlatformRepositoryGateway platformRepository;
//
//    @Override
//    public Platform savePlatform(String platformName) {
//        log.debug("Saving platform with name: {}", platformName);
//
//        validatePlatformDoesNotExist(platformName);
//
//        Platform platform = Platform.create(platformName);
//        Platform savedPlatform = platformRepository.save(platform);
//
//        log.info("Platform saved successfully: {}", savedPlatform);
//        return savedPlatform;
//    }
//
//    private void validatePlatformDoesNotExist(String platformName) {
//        String normalizedName = platformName.toUpperCase();
//
//        if (platformRepository.existsByName(normalizedName)) {
//            log.warn("Platform already exists with name: {}", normalizedName);
//            throw new DuplicatedPlatformException("Platform already exists with name: " + normalizedName);
//        }
//    }
//}
