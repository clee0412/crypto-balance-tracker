package edu.itba.cryptotracker.util;

import edu.itba.cryptotracker.domain.entity.crypto.Crypto;
import edu.itba.cryptotracker.domain.entity.crypto.LastKnownPrices;
import edu.itba.cryptotracker.domain.entity.goal.Goal;
import edu.itba.cryptotracker.domain.entity.platform.Platform;
import edu.itba.cryptotracker.domain.entity.usercrypto.UserCrypto;

import java.math.BigDecimal;

public class TestDataFactory {
    
    // Crypto test data
    public static Crypto createBitcoin() {
        return Crypto.create(
            "bitcoin",
            "btc",
            "Bitcoin",
            "https://example.com/bitcoin.png",
            createBitcoinPrices()
        );
    }
    
    public static Crypto createEthereum() {
        return Crypto.create(
            "ethereum",
            "eth",
            "Ethereum", 
            "https://example.com/ethereum.png",
            createEthereumPrices()
        );
    }
    
    public static LastKnownPrices createBitcoinPrices() {
        return new LastKnownPrices(
            new BigDecimal("45000.00"),
            new BigDecimal("42000.00"),
            new BigDecimal("0.99")
        );
    }
    
    public static LastKnownPrices createEthereumPrices() {
        return new LastKnownPrices(
            new BigDecimal("3200.00"),
            new BigDecimal("3000.00"), 
            new BigDecimal("0.071")
        );
    }
    
    // Platform test data
    public static Platform createBinancePlatform() {
        return Platform.create("binance-id", "Binance");
    }
    
    public static Platform createCoinbasePlatform() {
        return Platform.create("coinbase-id", "Coinbase");
    }
    
    // UserCrypto test data
    public static UserCrypto createUserBitcoin(Platform platform) {
        return UserCrypto.create(
            "user-123",
            new BigDecimal("0.5"),
            platform.getId(),
            "bitcoin"
        );
    }
    
    public static UserCrypto createUserEthereum(Platform platform) {
        return UserCrypto.create(
            "user-123",
            new BigDecimal("10.0"),
            platform.getId(),
            "ethereum"
        );
    }
    
    // Goal test data
    public static Goal createBitcoinGoal() {
        return Goal.create(
            createBitcoin(),
            new BigDecimal("2.0")
        );
    }
    
    public static Goal createEthereumGoal() {
        return Goal.create(
            createEthereum(),
            new BigDecimal("50.0")
        );
    }
    
    // Helper methods for creating custom test data
    public static Crypto createCustomCrypto(String id, String symbol, String name, BigDecimal priceUsd) {
        return Crypto.create(
            id,
            symbol,
            name,
            "https://example.com/" + id + ".png",
            new LastKnownPrices(priceUsd, priceUsd.multiply(new BigDecimal("0.93")), priceUsd.divide(new BigDecimal("45000"), 8, java.math.RoundingMode.HALF_UP))
        );
    }
    
    public static Platform createCustomPlatform(String id, String name) {
        return Platform.create(id, name);
    }
    
    public static UserCrypto createCustomUserCrypto(String userId, Crypto crypto, BigDecimal quantity, Platform platform) {
        return UserCrypto.create(
            userId,
            quantity,
            platform.getId(),
            crypto.getId()
        );
    }
    
    public static Goal createCustomGoal(Crypto crypto, BigDecimal goalQuantity) {
        return Goal.create(crypto, goalQuantity);
    }
}