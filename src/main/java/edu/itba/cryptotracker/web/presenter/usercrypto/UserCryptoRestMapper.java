package edu.itba.cryptotracker.web.presenter.usercrypto;

import edu.itba.cryptotracker.web.dto.usercrypto.UserCryptoResponse;
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
