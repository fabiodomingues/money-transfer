package com.xbank.moneytransfer.controller;

import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Controller("/money-transfer")
public class MoneyTransferController {

	private static final Logger LOGGER = LoggerFactory.getLogger(MoneyTransferController.class);

	@Get("/")
	public List<MoneyTransferDTO> listTransfersForAccount(String account) {
		LOGGER.debug("Listing transfers related to account " + account);
		return null;
	}

	@Post("/")
	public void makeTransfer(MoneyTransferDTO transfer) {
		LOGGER.debug("Transfering " + transfer.getValue() + " [sourceAccount:" + transfer.getSourceAccount() + "] [destinationAccount:" + transfer.getDestinationAccount() + "]");
	}
}
