package edu.itba.cryptotracker.web.presenter.usercrypto;

import edu.itba.cryptotracker.domain.model.TransferCryptoResponseModel;
import edu.itba.cryptotracker.web.dto.usercrypto.TransferCryptoResponseDTO;
import edu.itba.cryptotracker.web.dto.usercrypto.UserCryptoResponseDTO;
import edu.itba.cryptotracker.domain.entity.usercrypto.UserCrypto;
import org.springframework.stereotype.Component;

@Component
public class UserCryptoRestMapper {

    public UserCryptoResponseDTO toResponse(UserCrypto userCrypto) {
        return new UserCryptoResponseDTO(
            userCrypto.getId(),
            userCrypto.getCryptoId(),
            userCrypto.getPlatformId(),
            userCrypto.getQuantity()
        );
    }

    public TransferCryptoResponseDTO toTransferResponse(TransferCryptoResponseModel model) {
        return new TransferCryptoResponseDTO(
            true,
            "Transfer completed successfully",
            model.sourceId(),
            model.destinationId(),
            model.fromPlatform(),
            model.toPlatform(),
            model.quantityTransferred(),
            model.networkFee(),
            model.quantityReceived()
        );
    }
}
