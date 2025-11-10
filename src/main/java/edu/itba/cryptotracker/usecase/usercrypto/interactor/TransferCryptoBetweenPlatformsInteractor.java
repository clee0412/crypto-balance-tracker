package edu.itba.cryptotracker.usecase.usercrypto.interactor;

import edu.itba.cryptotracker.domain.entity.usercrypto.UserCrypto;
import edu.itba.cryptotracker.domain.exception.InvalidTransferException;
import edu.itba.cryptotracker.domain.exception.UserCryptoNotFoundException;
import edu.itba.cryptotracker.usecase.usercrypto.port.input.TransferCryptoBetweenPlatformsUseCase;
import edu.itba.cryptotracker.usecase.usercrypto.port.output.UserCryptoRepositoryPort;
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
public class TransferCryptoBetweenPlatformsInteractor implements TransferCryptoBetweenPlatformsUseCase {

    private final UserCryptoRepositoryPort userCryptoRepository;

    @Transactional
    public void execute(TransferRequest request) {
        if (request.quantityToTransfer.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transfer quantity must be positive");
        }

        if (request.networkFee.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Network fee cannot be negative");
        }

        if (request.fromPlatformId.equals(request.toPlatformId)) {
            throw new InvalidTransferException("Cannot transfer to same platform");
        }

        UserCrypto source = userCryptoRepository.findById(request.userCryptoId)
            .orElseThrow(() -> UserCryptoNotFoundException.byId(request.userCryptoId));

        if (!source.getPlatformId().equals(request.fromPlatformId)) {
            throw new InvalidTransferException("Source platform mismatch");
        }

        BigDecimal totalToSubtract;
        BigDecimal quantityToReceive;

        if (Boolean.TRUE.equals(request.sendFullQuantity)) {
            totalToSubtract = request.quantityToTransfer.add(request.networkFee);
            quantityToReceive = request.quantityToTransfer;
        } else {
            totalToSubtract = request.quantityToTransfer;
            quantityToReceive = request.quantityToTransfer.subtract(request.networkFee);
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
                request.toPlatformId
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
                request.toPlatformId,
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
    }

    public record TransferRequest(
        UUID userCryptoId,
        String fromPlatformId,
        String toPlatformId,
        BigDecimal quantityToTransfer,
        BigDecimal networkFee,
        Boolean sendFullQuantity
    ) {}
}
