package com.xbank.moneytransfer.controller;

import com.xbank.moneytransfer.controller.transfer.MoneyTransferDTO;
import com.xbank.moneytransfer.controller.transfer.MoneyTransferStatusDTO;
import com.xbank.moneytransfer.domain.Account;
import com.xbank.moneytransfer.infrastructure.RepositoryFactory;
import io.micronaut.context.ApplicationContext;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MutableHttpRequest;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.runtime.server.EmbeddedServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MoneyTransferControllerTest {

	private static EmbeddedServer server;
	private static HttpClient client;

	@BeforeAll
	static void setupServer() {
		server = ApplicationContext.run(EmbeddedServer.class);
		client = server.getApplicationContext().createBean(HttpClient.class, server.getURL());
	}

	@AfterAll
	static void stopServer() {
		if (server != null) server.stop();
		if (client != null) client.stop();
	}

	@Test
	void should_return_bad_request_when_getting_transfers_when_transfer_id_is_not_an_uuid() {
		MutableHttpRequest<Object> request = HttpRequest.GET("/money-transfer/123");

		HttpClientResponseException exception = assertThrows(HttpClientResponseException.class, () -> {
			client.toBlocking().exchange(request);
		});

		assertThat(exception).hasMessageContaining("Bad Request");
	}

	@Test
	void should_return_not_found_when_getting_transfers_when_transfer_id_does_not_exist() {
		MutableHttpRequest<Object> request = HttpRequest.GET("/money-transfer/" + UUID.randomUUID().toString());

		HttpClientResponseException exception = assertThrows(HttpClientResponseException.class, () -> {
			client.toBlocking().exchange(request);
		});

		assertThat(exception).hasMessageContaining("Not Found");
	}



	@Test
	void should_make_a_successful_transfer_asynchronously_and_should_be_possible_to_check_its_status() throws Exception {
		Account sourceAccount = new Account("123", BigDecimal.valueOf(2000.00));
		Account destinationAccount = new Account("456", BigDecimal.ZERO);
		RepositoryFactory.getAccountRepository().add(sourceAccount);
		RepositoryFactory.getAccountRepository().add(destinationAccount);

		MoneyTransferDTO dto = new MoneyTransferDTO();
		dto.setSourceAccount(sourceAccount.getAccountCode());
		dto.setDestinationAccount(destinationAccount.getAccountCode());
		dto.setAmount(1104.45);

		HttpResponse<MoneyTransferStatusDTO> postResult = post("/money-transfer", dto);
		String location = postResult.header("Location");

		assertThat(postResult.getStatus().getCode()).isEqualTo(HttpStatus.ACCEPTED.getCode());
		assertThat(location).contains("/money-transfer/");
		assertThat(location.split("/money-transfer/")[1]).containsPattern(getUUIDPattern());

		MoneyTransferStatusDTO moneyTransferStatusDTO = client.toBlocking().retrieve(HttpRequest.GET(location), MoneyTransferStatusDTO.class);
		assertThat(moneyTransferStatusDTO.getTransferId()).isNotNull();
		assertThat(moneyTransferStatusDTO.getStatus()).isEqualTo("COMPLETED");
	}

	private String getUUIDPattern() {
		return "[0-9a-fA-F]{8}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{4}\\-[0-9a-fA-F]{12}";
	}

	private <T> HttpResponse<T> post(String uri, MoneyTransferDTO dto) {
		HttpRequest<MoneyTransferDTO> post = HttpRequest.POST(uri, dto);
		return client.toBlocking().exchange(post);
	}
}