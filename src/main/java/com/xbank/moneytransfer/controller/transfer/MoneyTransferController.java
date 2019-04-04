package com.xbank.moneytransfer.controller.transfer;

import com.xbank.moneytransfer.application.transfer.register.TransferProcessor;
import com.xbank.moneytransfer.application.transfer.retriever.TransferNotFoundException;
import com.xbank.moneytransfer.application.transfer.retriever.TransferRetriever;
import com.xbank.moneytransfer.domain.TransferRegistry;
import com.xbank.moneytransfer.infrastructure.RepositoryFactory;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Error;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Produces;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

@Controller("/money-transfer")
public class MoneyTransferController {

	private static final Logger LOGGER = LoggerFactory.getLogger(MoneyTransferController.class);

	private TransferRetriever transferRetriever;
	private TransferProcessor moneyTransferProcessor;

	public MoneyTransferController() {
		this.moneyTransferProcessor = new TransferProcessor(RepositoryFactory.getAccountRepository(), RepositoryFactory.getTransferRepository());
		this.transferRetriever = new TransferRetriever(RepositoryFactory.getTransferRepository());
	}

	@Get("/{transferId}")
	public HttpResponse<Object> checkProtocol(String transferId) {
		LOGGER.debug("Checking status for transferId " + transferId);

		if (!isUUID(transferId)) {
			return HttpResponse.badRequest();
		}

		TransferRegistry transferRegistry = transferRetriever.retrieveTransfer(transferId);
		MoneyTransferStatusDTO moneyTransferStatusDTO = new MoneyTransferStatusDTO();
		moneyTransferStatusDTO.setTransferId(transferRegistry.getId().toString());
		moneyTransferStatusDTO.setStatus(transferRegistry.getStatus().name());

		return HttpResponse.ok(moneyTransferStatusDTO);
	}

	@Error(exception = TransferNotFoundException.class, global = true)
	public HttpResponse<?> handleTransferNotFoundException() {
		return HttpResponse.status(HttpStatus.NOT_FOUND);
	}

	@Post
	@Produces(MediaType.APPLICATION_JSON)
	public HttpResponse<Object> makeTransfer(MoneyTransferDTO transferDTO) throws URISyntaxException {
		LOGGER.debug("Transfering " + transferDTO.getAmount() + " [sourceAccount:" + transferDTO.getSourceAccount() + "] [destinationAccount:" + transferDTO.getDestinationAccount() + "]");

		UUID transferId = moneyTransferProcessor.transfer(transferDTO.getSourceAccount(), transferDTO.getDestinationAccount(), transferDTO.getAmount());

		return HttpResponse.accepted(new URI("/money-transfer/" + transferId));
	}

	public boolean isUUID(String uuid) {
		try {
			UUID.fromString(uuid);
			return true;
		} catch (IllegalArgumentException exception) {
			return false;
		}
	}
}
