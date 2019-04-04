package com.xbank.moneytransfer.controller.account;

import com.xbank.moneytransfer.application.account.AccountHandler;
import com.xbank.moneytransfer.application.transfer.register.AccountCodeAlreadyUsedException;
import com.xbank.moneytransfer.domain.Account;
import com.xbank.moneytransfer.infrastructure.RepositoryFactory;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Error;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

@Controller("/accounts")
public class AccountsController {

	private static final Logger LOGGER = LoggerFactory.getLogger(AccountsController.class);

	private AccountHandler accountHandler;

	public AccountsController() {
		this.accountHandler = new AccountHandler(RepositoryFactory.getAccountRepository());
	}

	@Post
	public MutableHttpResponse<Object> createAccount(AccountDTO accountDTO) {
		LOGGER.debug("Creating account " + accountDTO.getAccountCode() + " with [balance:" + accountDTO + "]");

		accountHandler.createAccount(accountDTO.getAccountCode(), accountDTO.getBalance());

		return HttpResponse.created(accountDTO);
	}

	@Get("/{accountCode}")
	public MutableHttpResponse<Optional<Account>> getAccount(String accountCode) {
		return HttpResponse.ok(accountHandler.retrieveAccount(accountCode));
	}

	@Error(exception = AccountCodeAlreadyUsedException.class, global = true)
	public HttpResponse<?> handleAccountCodeAlreadyUsedException() {
		return HttpResponse.status(HttpStatus.UNPROCESSABLE_ENTITY);
	}
}
