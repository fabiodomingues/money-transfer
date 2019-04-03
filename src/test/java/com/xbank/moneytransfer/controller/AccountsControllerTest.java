package com.xbank.moneytransfer.controller;

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

import static org.assertj.core.api.Assertions.assertThat;

class AccountsControllerTest {

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
	void should_create_an_account() {
		AccountDTO accountDTO = new AccountDTO();
		accountDTO.setAccountCode("123456-xyz");
		accountDTO.setInitialBalance(250.15);

		HttpResponse postResult = post("/accounts", accountDTO);
		assertThat(postResult.getStatus().getCode()).isEqualTo(HttpStatus.OK.getCode());

		assertThat(RepositoryFactory.getAccountRepository().withId("123456-xyz")).isPresent().hasValueSatisfying(s -> {
			assertThat(s.getAccountCode()).isEqualTo("123456-xyz");
			assertThat(s.getBalance()).isEqualTo(BigDecimal.valueOf(250.15));
		});
	}

	private HttpResponse<MoneyTransferStatusDTO> get(String uri) {
		return client.toBlocking().exchange(HttpRequest.GET(uri));
	}

	private HttpResponse<MoneyTransferStatusDTO> post(String uri, AccountDTO dto) {
		HttpRequest<AccountDTO> post = HttpRequest.POST(uri, dto);
		return client.toBlocking().exchange(post);
	}
}