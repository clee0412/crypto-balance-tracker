package exchange;

import edu.itba.class12.exchange.application.usecase.CurrencyConverter;
import edu.itba.class12.exchange.domain.exchangerate.ExchangeRateProvider;
import edu.itba.class12.exchange.domain.exchangerate.ExchangeRateResponse;
import edu.itba.class12.exchange.domain.money.MoneyAmount;
import edu.itba.class12.exchange.domain.persistence.ConversionGateway;
import edu.itba.class12.exchange.domain.persistence.SingleConversionEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static edu.itba.class12.exchange.domain.money.Currency.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CurrencyConverterTest {

	@Mock
	private ExchangeRateProvider mockExchangeRateProvider;

	@Spy
	private ConversionGateway conversionGateway;

	@Test
	void givenTheRequestFailsThenTheConversionShouldBeZero() {
		// Given
		when(this.mockExchangeRateProvider.getExchangeRate(any())).thenReturn(ExchangeRateResponse.empty());
		final var converter = new CurrencyConverter(this.mockExchangeRateProvider, this.conversionGateway);

		// When
		final var result = converter.convert(MoneyAmount.create(EUR, 100), USD);

		// Then
		final var expected = MoneyAmount.create(USD, 0);
		assertThat(result, is(expected));
	}

	@Test
	void givenAValidConversionRateThenConversionShouldSucceed() {
		// Given
		when(this.mockExchangeRateProvider.getExchangeRate(any()))
				.thenReturn(new ExchangeRateResponse(Map.of(USD, 102.0)));
		final var converter = new CurrencyConverter(this.mockExchangeRateProvider, this.conversionGateway);

		// When
		final var fromMoney = MoneyAmount.create(EUR, 100);
		final var result = converter.convert(fromMoney, USD);

		// Then
		final var expected = MoneyAmount.create(USD, 10200);
		assertThat(result, is(expected));
		final var singeConversionEntity = new SingleConversionEntity(LocalDate.now(), fromMoney, expected);
		verify(this.conversionGateway, times(1)).save(singeConversionEntity);
	}

	@Test
	void givenAValidConversionRateThenConversionShouldSucceedForTwoCurrencies() {
		// Given
		when(this.mockExchangeRateProvider.getExchangeRate(any()))
				.thenReturn(new ExchangeRateResponse(Map.of(USD, 102.0, JPY, 765.0)));
		final var converter = new CurrencyConverter(this.mockExchangeRateProvider, this.conversionGateway);

		// When
		final var result = converter.convert(MoneyAmount.create(EUR, 100), List.of(USD, JPY));

		// Then
		final var expected = List.of(MoneyAmount.create(USD, 10200), MoneyAmount.create(JPY, 76500));
		assertThat(result, hasSize(2));
		assertThat(result, containsInAnyOrder(expected.toArray()));
	}

}
