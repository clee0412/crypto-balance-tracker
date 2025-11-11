package exchange;

import edu.itba.class12.exchange.application.exchangerate.HttpExchangeRateProvider;
import edu.itba.class12.exchange.application.usecase.CurrencyConverter;
import edu.itba.class12.exchange.domain.config.ExchangeApiConfig;
import edu.itba.class12.exchange.domain.config.ExchangeApiConfigItem;
import edu.itba.class12.exchange.domain.money.Currency;
import edu.itba.class12.exchange.domain.money.MoneyAmount;
import edu.itba.class12.exchange.httpclient.UniRestHttpClient;
import okhttp3.mockwebserver.MockResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static edu.itba.class12.exchange.mock.JsonFileReader.readJsonFile;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {CurrencyConverter.class, HttpExchangeRateProvider.class, UniRestHttpClient.class,
		ExchangeApiConfig.class})
class CurrencyConverterIT extends IntegrationTest {

	@MockitoBean
	private ExchangeApiConfig exchangeApiConfiguration;

	@Autowired
	private CurrencyConverter currencyConverter;

	@Test
	void givenAValidRequestThenTheConversionShouldWorkFine() {
		when(this.exchangeApiConfiguration.defaultConfig())
				.thenReturn(new ExchangeApiConfigItem("dummyApiKey", "test", this.mockWebServer.url("/").toString()));
		this.mockWebServer.enqueue(new MockResponse().setResponseCode(200).setHeader("Content-Type", "application/json")
				.setBody(readJsonFile("one_currency_response.json")));

		final var moneyAmount = MoneyAmount.create(Currency.EUR, 100);
		final var result = this.currencyConverter.convert(moneyAmount, Currency.USD);
		assertThat(result, is(MoneyAmount.create(Currency.USD, 10200)));
	}
}
