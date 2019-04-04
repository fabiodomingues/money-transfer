package com.xbank.moneytransfer.application.transfer.register;

import com.xbank.moneytransfer.application.account.AccountNotFoundException;
import com.xbank.moneytransfer.application.account.AccountRepository;
import com.xbank.moneytransfer.application.transfer.TransferRepository;
import com.xbank.moneytransfer.domain.Account;
import com.xbank.moneytransfer.domain.TransferRegistry;
import com.xbank.moneytransfer.domain.TransferStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransferProcessorTest {

	private TransferProcessor moneyTransferProcessor;

	@Mock
	private AccountRepository accountRepository;

	@Mock
	private TransferRepository transferRepository;

	@BeforeEach
	void setup() {
		moneyTransferProcessor = new TransferProcessor(accountRepository, transferRepository);
	}

	@Test
	void should_throw_account_not_found_when_source_account_does_not_exist() {
		when(accountRepository.withId("x-7789-4")).thenReturn(Optional.empty());

		AccountNotFoundException accountNotFoundException = assertThrows(AccountNotFoundException.class, () -> {
			moneyTransferProcessor.transfer("x-7789-4", "x-99884", 115.45);
		});

		assertThat(accountNotFoundException.getMessage()).contains("x-7789-4");
	}

	@Test
	void should_throw_account_not_found_when_destination_account_does_not_exist() {
		when(accountRepository.withId("x-7789-4")).thenReturn(Optional.of(new Account("x-7789-4", BigDecimal.ZERO)));
		when(accountRepository.withId("x-99884")).thenReturn(Optional.empty());

		AccountNotFoundException accountNotFoundException = assertThrows(AccountNotFoundException.class, () -> {
			moneyTransferProcessor.transfer("x-7789-4", "x-99884", 115.45);
		});

		assertThat(accountNotFoundException.getMessage()).contains("x-99884");
	}

	@Test
	void should_register_the_pending_transfer_and_set_as_canceled_when_source_account_has_insufficient_funds() {
		Account sourceAccount = createAccountWithBalance(1000.00);
		Account destinationAccount = createAccountWithBalance(0.00);

		when(accountRepository.withId("abc-4456")).thenReturn(Optional.of(sourceAccount));
		when(accountRepository.withId("poy-339744-x")).thenReturn(Optional.of(destinationAccount));

		moneyTransferProcessor.transfer("abc-4456", "poy-339744-x", 1000.01);

		InOrder inOrder = Mockito.inOrder(accountRepository, transferRepository);

		ArgumentCaptor<TransferRegistry> transferCaptor = ArgumentCaptor.forClass(TransferRegistry.class);
		inOrder.verify(transferRepository).add(transferCaptor.capture());
		inOrder.verify(transferRepository).update(transferCaptor.capture());

		assertThat(transferCaptor.getValue().getStatus()).isEqualTo(TransferStatus.CANCELED);
	}

	@Test
	void should_transfer_money_from_source_account_to_destination_account() {
		Account sourceAccount = createAccountWithBalance(1000.00);
		Account destinationAccount = createAccountWithBalance(15.00);

		when(accountRepository.withId("378445-10")).thenReturn(Optional.of(sourceAccount));
		when(accountRepository.withId("45471-x10")).thenReturn(Optional.of(destinationAccount));

		moneyTransferProcessor.transfer("378445-10", "45471-x10", 500.00);

		assertThat(sourceAccount.getBalance()).isEqualTo(BigDecimal.valueOf(500.00));
		assertThat(destinationAccount.getBalance()).isEqualTo(BigDecimal.valueOf(515.00));
		verify(accountRepository).update(sourceAccount, destinationAccount);
	}

	@Test
	void should_register_a_pending_transfer_before_execute_the_transfer_make_it_completed_after_the_account_update() {
		Account sourceAccount = createAccountWithBalance(1000.00);
		Account destinationAccount = createAccountWithBalance(15.00);

		when(accountRepository.withId("378445-10")).thenReturn(Optional.of(sourceAccount));
		when(accountRepository.withId("45471-x10")).thenReturn(Optional.of(destinationAccount));

		UUID transferId = moneyTransferProcessor.transfer("378445-10", "45471-x10", 500.00);

		InOrder inOrder = Mockito.inOrder(accountRepository, transferRepository);
		ArgumentCaptor<TransferRegistry> transferCaptor = ArgumentCaptor.forClass(TransferRegistry.class);
		inOrder.verify(transferRepository).add(transferCaptor.capture());
		inOrder.verify(accountRepository).update(sourceAccount, destinationAccount);
		inOrder.verify(transferRepository).update(transferCaptor.capture());

		assertThat(transferCaptor.getValue().getId()).isEqualTo(transferId);
		assertThat(transferCaptor.getValue().getStatus()).isEqualTo(TransferStatus.COMPLETED);
	}

	private Account createAccountWithBalance(double val) {
		return new Account(UUID.randomUUID().toString(), BigDecimal.valueOf(val));
	}
}