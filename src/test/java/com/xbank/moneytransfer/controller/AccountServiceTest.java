package com.xbank.moneytransfer.controller;

import com.xbank.moneytransfer.application.AccountBalanceNegativeException;
import com.xbank.moneytransfer.application.AccountCodeAlreadyUsedException;
import com.xbank.moneytransfer.application.AccountRepository;
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
class AccountServiceTest {

	@Mock
	private AccountRepository accountRepository;

	@Test
	void should_create_account_and_add_to_repository() {
		AccountService accountService = new AccountService(accountRepository);

		accountService.createAccount("123", 10115.43);

		ArgumentCaptor<Account> accountArgumentCaptor = ArgumentCaptor.forClass(Account.class);
		verify(accountRepository).add(accountArgumentCaptor.capture());
		assertThat(accountArgumentCaptor.getValue().getAccountCode()).isEqualTo("123");
		assertThat(accountArgumentCaptor.getValue().getBalance()).isEqualTo(BigDecimal.valueOf(10115.43));
	}

	@Test
	void should_throw_exception_when_code_already_exist() {
		when(accountRepository.withId("456")).thenReturn(Optional.of(new Account("456", BigDecimal.ZERO)));

		AccountService accountService = new AccountService(accountRepository);

		assertThrows(AccountCodeAlreadyUsedException.class, () -> {
			accountService.createAccount("456", 10115.43);
		});
	}

	@Test
	void should_throw_exception_balance_is_negative() {
		AccountService accountService = new AccountService(accountRepository);

		assertThrows(AccountBalanceNegativeException.class, () -> {
			accountService.createAccount("456", -554.12);
		});
	}
}