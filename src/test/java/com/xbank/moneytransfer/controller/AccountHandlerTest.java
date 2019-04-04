package com.xbank.moneytransfer.controller;

import com.xbank.moneytransfer.application.account.AccountHandler;
import com.xbank.moneytransfer.application.account.AccountRepository;
import com.xbank.moneytransfer.application.transfer.register.AccountBalanceNegativeException;
import com.xbank.moneytransfer.application.transfer.register.AccountCodeAlreadyUsedException;
import com.xbank.moneytransfer.domain.Account;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountHandlerTest {

	@Mock
	private AccountRepository accountRepository;

	@Test
	void should_create_account_and_add_to_repository() {
		AccountHandler accountHandler = new AccountHandler(accountRepository);

		accountHandler.createAccount("123", 10115.43);

		ArgumentCaptor<Account> accountArgumentCaptor = ArgumentCaptor.forClass(Account.class);
		verify(accountRepository).add(accountArgumentCaptor.capture());
		assertThat(accountArgumentCaptor.getValue().getAccountCode()).isEqualTo("123");
		assertThat(accountArgumentCaptor.getValue().getBalance()).isEqualTo(BigDecimal.valueOf(10115.43));
	}

	@Test
	void should_retrieve_account_from_repository() {
		AccountHandler accountHandler = new AccountHandler(accountRepository);

		when(accountRepository.withId("123")).thenReturn(Optional.of(new Account("123", BigDecimal.valueOf(100))));

		Optional<Account> account = accountHandler.retrieveAccount("123");

		assertThat(account).isPresent().hasValueSatisfying(x -> {
			assertThat(x.getAccountCode()).isEqualTo("123");
			assertThat(x.getBalance()).isEqualTo(BigDecimal.valueOf(100));
		});
	}

	@Test
	void should_throw_exception_when_code_already_exist() {
		when(accountRepository.withId("456")).thenReturn(Optional.of(new Account("456", BigDecimal.ZERO)));

		AccountHandler accountHandler = new AccountHandler(accountRepository);

		assertThrows(AccountCodeAlreadyUsedException.class, () -> {
			accountHandler.createAccount("456", 10115.43);
		});
	}

	@Test
	void should_throw_exception_balance_is_negative() {
		AccountHandler accountHandler = new AccountHandler(accountRepository);

		assertThrows(AccountBalanceNegativeException.class, () -> {
			accountHandler.createAccount("456", -554.12);
		});
	}
}