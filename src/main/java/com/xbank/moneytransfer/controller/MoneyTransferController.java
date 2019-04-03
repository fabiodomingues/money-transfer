package com.xbank.moneytransfer.controller;

import com.xbank.moneytransfer.application.MoneyTransferService;
import com.xbank.moneytransfer.domain.Transfer;
import com.xbank.moneytransfer.infrastructure.RepositoryFactory;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Produces;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;
import java.util.UUID;

@Controller("/money-transfer")
public class MoneyTransferController {

	private static final Logger LOGGER = LoggerFactory.getLogger(MoneyTransferController.class);

	private MoneyTransferService moneyTransferService;

	public MoneyTransferController() {
		this.moneyTransferService = new MoneyTransferService(RepositoryFactory.getAccountRepository(), RepositoryFactory.getTransferRepository());
	}

	@Get("/{transferId}")
	public HttpResponse<Object> checkProtocol(String transferId) {
		LOGGER.debug("Checking status for transferId " + transferId);

		if (!isUUID(transferId)) {
			return HttpResponse.badRequest();
		}

		Optional<Transfer> transferOptional = RepositoryFactory.getTransferRepository().withId(UUID.fromString(transferId));
		if (!transferOptional.isPresent()) {
			return HttpResponse.notFound();
		}

		Transfer transfer = transferOptional.get();
		MoneyTransferStatusDTO moneyTransferStatusDTO = new MoneyTransferStatusDTO();
		moneyTransferStatusDTO.setTransferId(transfer.getId().toString());
		moneyTransferStatusDTO.setStatus(transfer.getStatus().name());

		return HttpResponse.ok(moneyTransferStatusDTO);
	}

	public boolean isUUID(String uuid) {
		try{
			UUID.fromString(uuid);
			return true;
		} catch (IllegalArgumentException exception){
			return false;
		}
	}

	@Post
	@Produces(MediaType.APPLICATION_JSON)
	public HttpResponse<Object> makeTransfer(MoneyTransferDTO transferDTO) {
		LOGGER.debug("Transfering " + transferDTO.getAmount() + " [sourceAccount:" + transferDTO.getSourceAccount() + "] [destinationAccount:" + transferDTO.getDestinationAccount() + "]");

		UUID transferId = moneyTransferService.transfer(transferDTO.getSourceAccount(), transferDTO.getDestinationAccount(), transferDTO.getAmount());

		return HttpResponse.accepted(getCheckMoneyTransferURI(transferId));
	}

	private URI getCheckMoneyTransferURI(UUID transferId) {
		try {
			return new URI("/money-transfer/" + transferId);
		} catch (URISyntaxException e) {
			throw new RuntimeException(e.getMessage());
		}
	}
}
