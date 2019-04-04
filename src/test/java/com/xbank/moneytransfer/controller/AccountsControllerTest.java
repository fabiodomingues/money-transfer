package com.xbank.moneytransfer.controller;

import com.xbank.moneytransfer.controller.account.AccountDTO;
import io.micronaut.context.ApplicationContext;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.runtime.server.EmbeddedServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
	void should_create_and_retrieve_accounts() {
		AccountDTO accountDTO = new AccountDTO();
		accountDTO.setAccountCode("ttt-987654-xyz");
		accountDTO.setBalance(250.15);

		HttpResponse<AccountDTO> postResult = post("/accounts", accountDTO);
		assertThat(postResult.getStatus().getCode()).isEqualTo(HttpStatus.CREATED.getCode());

		AccountDTO accountRetrieved = get("/accounts/ttt-987654-xyz");
		assertThat(accountRetrieved.getAccountCode()).isEqualTo("ttt-987654-xyz");
		assertThat(accountRetrieved.getBalance()).isEqualTo(250.15);
	}

	@Test
	void should_return_unprocessable_entity_when_account_code_is_already_in_use() {
		AccountDTO accountDTO = new AccountDTO();
		accountDTO.setAccountCode("123456-xyz");
		accountDTO.setBalance(250.15);
		post("/accounts", accountDTO);

		HttpClientResponseException exception = assertThrows(HttpClientResponseException.class, () -> {
			post("/accounts", accountDTO);
		});

		assertThat(exception).hasMessageContaining("Unprocessable Entity");
	}

	private AccountDTO get(String uri) {
		return client.toBlocking().retrieve(uri, AccountDTO.class);
	}

	private HttpResponse<AccountDTO> post(String uri, AccountDTO dto) {
		HttpRequest<AccountDTO> post = HttpRequest.POST(uri, dto);
		return client.toBlocking().exchange(post);
	}
}
