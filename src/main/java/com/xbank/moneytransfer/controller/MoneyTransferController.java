package com.xbank.moneytransfer.controller;

import com.xbank.moneytransfer.application.MoneyTransferService;
import com.xbank.moneytransfer.infrastructure.InMemoryAccountRepository;
import com.xbank.moneytransfer.infrastructure.InMemoryTransferRepository;
import com.xbank.moneytransfer.infrastructure.RepositoryFactory;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.UUID;

@Controller("/money-transfer")
public class MoneyTransferController {

	private static final Logger LOGGER = LoggerFactory.getLogger(MoneyTransferController.class);

	private MoneyTransferService moneyTransferService;

	public MoneyTransferController() {
		this.moneyTransferService = new MoneyTransferService(RepositoryFactory.getAccountRepository(), RepositoryFactory.getTransferRepository());
	}

	@Get("/{protocol}")
	public MoneyTransferProtocolDTO checkProtocol(String protocol) {
		LOGGER.debug("Checking status for protocol " + protocol);
		return null;
	}

	@Post
	public MutableHttpResponse<Object> makeTransfer(MoneyTransferDTO transfer) {
		LOGGER.debug("Transfering " + transfer.getAmount() + " [sourceAccount:" + transfer.getSourceAccount() + "] [destinationAccount:" + transfer.getDestinationAccount() + "]");

		UUID transferId = moneyTransferService.transfer(transfer.getSourceAccount(), transfer.getDestinationAccount(), transfer.getAmount());

		MoneyTransferProtocolDTO body = new MoneyTransferProtocolDTO();
		body.setProtocolId(transferId);

		return HttpResponse.accepted().body(body);
	}
}
