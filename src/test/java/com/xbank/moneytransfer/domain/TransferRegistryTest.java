package com.xbank.moneytransfer.domain;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class TransferRegistryTest {

	@Test
	void should_create_a_transfer_with_pending_status() {
		assertThat(new TransferRegistry(UUID.randomUUID()).getStatus()).isEqualTo(TransferStatus.PENDING);
	}
}