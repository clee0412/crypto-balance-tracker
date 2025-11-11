package exchange;

import edu.itba.class12.exchange.application.usecase.GetHistoricalRates;
import edu.itba.class12.exchange.domain.exchangerate.ExchangeRateProvider;
import edu.itba.class12.exchange.domain.exchangerate.ExchangeRateResponse;
import edu.itba.class12.exchange.domain.exchangerate.HistoricalExchangeRateResponse;
import edu.itba.class12.exchange.domain.money.MoneyAmount;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static edu.itba.class12.exchange.domain.money.Currency.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetHistoricalRatesTest {

	@Mock
	private ExchangeRateProvider mockExchangeRateProvider;

	@Test
	void givenAValidConversionRateHistoricRateShouldSucceedForTwoCurrencies() {
		// Given
		when(this.mockExchangeRateProvider.getHistoricalExchangeRate(any()))
				.thenReturn(new HistoricalExchangeRateResponse(
						Map.of("2025-05-07", new ExchangeRateResponse(Map.of(USD, 102.0, JPY, 765.0)))));
		final var historicalRatesUseCase = new GetHistoricalRates(this.mockExchangeRateProvider);

		// When
		final var result = historicalRatesUseCase.getHistoricalRate(MoneyAmount.create(EUR, 100), List.of(USD, JPY),
				LocalDate.now());

		// Then
		assertThat(result, is(notNullValue()));
		assertThat(result.date(), is(LocalDate.of(2025, 5, 7)));
		assertThat(result.exchangeRateResponse().getExchange(USD), is(102.0));
		assertThat(result.exchangeRateResponse().getExchange(JPY), is(765.0));
	}
}
