package edu.itba.cryptotracker.adapter.controller.rest.usercrypto.mapper;

import edu.itba.cryptotracker.adapter.controller.rest.usercrypto.dto.UserCryptoResponse;
import edu.itba.cryptotracker.domain.entity.usercrypto.UserCrypto;
import org.springframework.stereotype.Component;

@Component
public class UserCryptoRestMapper {

    public UserCryptoResponse toResponse(UserCrypto userCrypto) {
        return new UserCryptoResponse(
            userCrypto.getId(),
            userCrypto.getCryptoId(),
            userCrypto.getPlatformId(),
            userCrypto.getQuantity()
        );
    }
}
