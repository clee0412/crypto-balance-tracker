package edu.itba.cryptotracker.application.usecase.usercrypto;

import edu.itba.cryptotracker.domain.entity.usercrypto.UserCrypto;
import edu.itba.cryptotracker.domain.exception.InvalidTransferException;
import edu.itba.cryptotracker.domain.exception.PlatformNotFoundException;
import edu.itba.cryptotracker.domain.exception.UserCryptoNotFoundException;
import edu.itba.cryptotracker.domain.gateway.PlatformRepositoryGateway;
import edu.itba.cryptotracker.domain.model.TransferCryptoRequestModel;
import edu.itba.cryptotracker.domain.model.TransferCryptoResponseModel;
import edu.itba.cryptotracker.domain.usecase.usercrypto.TransferCryptoBetweenPlatformsUseCase;
import edu.itba.cryptotracker.domain.gateway.UserCryptoRepositoryGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransferCryptoBetweenPlatformsUseCaseImpl implements TransferCryptoBetweenPlatformsUseCase {

    private final UserCryptoRepositoryGateway userCryptoRepository;
    private final PlatformRepositoryGateway platformRepository;

    @Transactional
    public TransferCryptoResponseModel execute(TransferCryptoRequestModel request) {
        if (request.quantityToTransfer().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transfer quantity must be positive");
        }

        if (request.networkFee().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Network fee cannot be negative");
        }

        if (request.fromPlatformId().equals(request.toPlatformId())) {
            throw new InvalidTransferException("Cannot transfer to same platform");
        }

        if (request.fromPlatformId().isBlank()) {
            throw new IllegalArgumentException("Source platform ID cannot be blank");
        }

        if (request.toPlatformId().isBlank()) {
            throw new IllegalArgumentException("Destination platform ID cannot be blank");
        }

        if (!platformRepository.findById(request.toPlatformId()).isPresent()) {
            throw new PlatformNotFoundException(request.toPlatformId());
        }

        UserCrypto source = userCryptoRepository.findById(request.userCryptoId())
            .orElseThrow(() -> UserCryptoNotFoundException.byId(request.userCryptoId()));

        if (!source.getPlatformId().equals(request.fromPlatformId())) {
            throw new InvalidTransferException("Source platform mismatch");
        }

        // Capture source ID before potential deletion
        UUID sourceId = source.getId();

        BigDecimal totalToSubtract;
        BigDecimal quantityToReceive;

        if (Boolean.TRUE.equals(request.sendFullQuantity())) {
            totalToSubtract = request.quantityToTransfer().add(request.networkFee());
            quantityToReceive = request.quantityToTransfer();
        } else {
            totalToSubtract = request.quantityToTransfer();
            quantityToReceive = request.quantityToTransfer().subtract(request.networkFee());
        }

        // Validate sufficient balance
        if (!source.hasSufficientBalance(totalToSubtract)) {
            throw InvalidTransferException.insufficientBalance(
                source.getQuantity().toPlainString(),
                totalToSubtract.toPlainString()
            );
        }

        if (quantityToReceive.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidTransferException("Network fee exceeds transfer amount");
        }

        source.subtractQuantity(totalToSubtract);

        Optional<UserCrypto> destinationOpt = userCryptoRepository
            .findByUserIdAndCryptoIdAndPlatformId(
                source.getUserId(),
                source.getCryptoId(),
                request.toPlatformId()
            );

        List<UserCrypto> toSave = new ArrayList<>();
        UserCrypto destination;

        if (destinationOpt.isPresent()) {
            destination = destinationOpt.get();
            destination.addQuantity(quantityToReceive);
            toSave.add(destination);
        } else {
            destination = UserCrypto.create(
                source.getUserId(),
                quantityToReceive,
                request.toPlatformId(),
                source.getCryptoId()
            );
            toSave.add(destination);
        }

        if (source.isZeroBalance()) {
            userCryptoRepository.deleteById(source.getId());
        } else {
            toSave.add(source);
        }

        userCryptoRepository.saveAll(toSave);

        // Build and return response
        return new TransferCryptoResponseModel(
            sourceId,
            destination.getId(),
            request.fromPlatformId(),
            request.toPlatformId(),
            request.quantityToTransfer(),
            request.networkFee(),
            quantityToReceive
        );
    }
}
