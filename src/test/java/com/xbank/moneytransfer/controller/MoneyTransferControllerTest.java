package com.xbank.moneytransfer.controller;

import io.micronaut.context.ApplicationContext;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.client.HttpClient;
import io.micronaut.runtime.server.EmbeddedServer;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

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
	void listTransfer() throws Exception {
		HttpRequest request = HttpRequest.GET("/money-transfer/123");
		String result = client.toBlocking().retrieve(request);
		assertThat(result).isEqualTo("[]");
	}
}