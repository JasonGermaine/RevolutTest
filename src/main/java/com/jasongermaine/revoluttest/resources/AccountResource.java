package com.jasongermaine.revoluttest.resources;

import java.util.List;
import java.util.function.Supplier;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jasongermaine.revoluttest.account.AccountCreator;
import com.jasongermaine.revoluttest.core.Account;
import com.jasongermaine.revoluttest.core.AccountCreationRequest;

@Path("/account")
@Produces(MediaType.APPLICATION_JSON)
public class AccountResource {
  private static final Logger LOG = LoggerFactory.getLogger(AccountResource.class);

  private final AccountCreator accountCreator;
  private final Supplier<List<Account>> accountSupplier;

  @Inject
  public AccountResource(AccountCreator accountCreator,
                         Supplier<List<Account>> accountSupplier) {
    this.accountCreator = accountCreator;
    this.accountSupplier = accountSupplier;
  }

  @POST
  public Response createAccount(AccountCreationRequest request) {
    try {
      return Response.ok(accountCreator.apply(request)).build();
    } catch (IllegalArgumentException e) {
      LOG.warn("Failed to create account for request={}", request, e);
      return Response.status(Status.BAD_REQUEST).build();
    }
  }

  @GET
  public List<Account> getAllAccounts() {
    return accountSupplier.get();
  }
}
