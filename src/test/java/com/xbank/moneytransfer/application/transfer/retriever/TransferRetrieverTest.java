package com.xbank.moneytransfer.application.transfer.retriever;

import com.xbank.moneytransfer.application.transfer.TransferRepository;
import com.xbank.moneytransfer.domain.TransferRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransferRetrieverTest {

	@Mock
	private TransferRepository transferRepository;

	private TransferRetriever transferRetriever;

	@BeforeEach
	void setup() {
		this.transferRetriever = new TransferRetriever(transferRepository);
	}

	@Test
	void should_throw_exception_when_transfer_registry_does_not_exist() {
		assertThrows(TransferNotFoundException.class,() -> {
			transferRetriever.retrieveTransfer(UUID.randomUUID().toString());
		});
	}

	@Test
	void should_retrieve_transfer_by_its_id() {
		UUID transferId = UUID.randomUUID();

		TransferRegistry transferRegistry = new TransferRegistry(transferId);
		when(transferRepository.withId(transferId)).thenReturn(Optional.of(transferRegistry));

		TransferRegistry retrievedTransfer = transferRetriever.retrieveTransfer(transferId.toString());
		assertThat(retrievedTransfer).isSameAs(transferRegistry);
	}
}