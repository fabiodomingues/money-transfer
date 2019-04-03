package com.xbank.moneytransfer.controller;

import com.xbank.moneytransfer.domain.Account;
import com.xbank.moneytransfer.domain.TransferStatus;
import com.xbank.moneytransfer.infrastructure.RepositoryFactory;
import io.micronaut.context.ApplicationContext;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.runtime.server.EmbeddedServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

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
	void should_make_a_successful_transfer_asynchronously_and_should_be_possible_to_check_its_status() throws Exception {
		Account sourceAccount = new Account("123", BigDecimal.valueOf(2000.00));
		Account destinationAccount = new Account("456", BigDecimal.ZERO);
		RepositoryFactory.getAccountRepository().add(sourceAccount);
		RepositoryFactory.getAccountRepository().add(destinationAccount);

		MoneyTransferDTO dto = new MoneyTransferDTO();
		dto.setSourceAccount(sourceAccount.getAccountCode());
		dto.setDestinationAccount(destinationAccount.getAccountCode());
		dto.setAmount(1104.45);

		HttpResponse<MoneyTransferProtocolDTO> postResult = post("/money-transfer", dto);
		Optional<MoneyTransferProtocolDTO> protocolOptional = postResult.getBody();

		assertThat(postResult.getStatus().getCode()).isEqualTo(HttpStatus.ACCEPTED.getCode());
		assertThat(protocolOptional).hasValueSatisfying(protocol -> {
			assertThat(protocol.getProtocolId()).isNotNull();
			assertThat(protocol.getStatus()).isEqualTo(TransferStatus.PENDING);
		});

		HttpResponse<MoneyTransferProtocolDTO> getResult = get("/money-transfer/" + protocolOptional.get().getProtocolId());
		assertThat(getResult.getStatus().getCode()).isEqualTo(HttpStatus.OK);
		assertThat(getResult.getBody()).hasValueSatisfying(protocol -> {
			assertThat(protocol.getProtocolId()).isNotNull();
			assertThat(protocol.getStatus()).isEqualTo(TransferStatus.COMPLETED);
		});
	}

	private HttpResponse<MoneyTransferProtocolDTO> get(String uri) {
		return client.toBlocking().exchange(HttpRequest.GET(uri));
	}

	private HttpResponse<MoneyTransferProtocolDTO> post(String uri, MoneyTransferDTO dto) {
		HttpRequest<MoneyTransferDTO> post = HttpRequest.POST(uri, dto);
		return client.toBlocking().exchange(post);
	}
}