package integration;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.JerseyClientBuilder;

import com.jasongermaine.revoluttest.core.Account;
import com.jasongermaine.revoluttest.core.AccountCreationRequest;
import com.jasongermaine.revoluttest.core.Money;
import com.jasongermaine.revoluttest.core.Transfer;
import com.jasongermaine.revoluttest.core.TransferRequest;
import com.neovisionaries.i18n.CurrencyCode;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import io.cucumber.datatable.DataTable;

public class StepDefinitions {
  private static final GenericType<List<Account>> ACCOUNT_TYPE_REF = new GenericType<List<Account>>() {
  };
  private static final GenericType<List<Transfer>> TRANSFER_TYPE_REF = new GenericType<List<Transfer>>() {
  };
  private static final Supplier<String> API_PATH_SUPPLIER = () -> String.format("http://localhost:%d", ApplicationSupport.SUPPORT.getLocalPort());
  private static final Supplier<String> ACCOUNT_PATH_SUPPLIER = () -> String.format("%s/account", API_PATH_SUPPLIER.get());
  private static final Supplier<String> TRANSFER_PATH_SUPPLIER = () -> String.format("%s/transfer", API_PATH_SUPPLIER.get());

  private final Executor executor = Executors.newFixedThreadPool(5);
  private final Client client = new JerseyClientBuilder().build();
  private final Map<String, Integer> accountAliasToIdMap = new ConcurrentHashMap<>();
  private Response response;

  @Given("^the following accounts exist:$")
  public void theFollowingAccountsExist(DataTable accountTable) {
    accountTable.asMaps()
        .forEach(entry -> {
          Response creationResponse = createAccountFromProps(entry);
          if (creationResponse.getStatus() != 200) {
            throw new IllegalStateException("Failed to save account with code=" + creationResponse.getStatus());
          }

          Optional.ofNullable(entry.get("alias")).ifPresent(alias -> accountAliasToIdMap.put(alias, creationResponse.readEntity(Account.class).getId()));
        });
  }

  @When("^I add account:$")
  public void iAddTheFollowingAccounts(DataTable accountTable) {
    response = createAccountFromProps(accountTable.asMap(String.class, String.class));
  }

  @When("^I submit transfer:$")
  public void iSubmitTransfer(DataTable transferTable) {
    response = createTransferFromProps(transferTable.asMap(String.class, String.class));
  }

  @When("^I submit transfers:$")
  public void iSubmitTransfers(DataTable transferTable) {
    List<CompletableFuture<Response>> submissionFutures = transferTable.asMaps()
        .stream()
        .map(transfer -> CompletableFuture.supplyAsync(() -> createTransferFromProps(transfer), executor))
        .collect(Collectors.toList());
    CompletableFuture.allOf(submissionFutures.toArray(new CompletableFuture[submissionFutures.size()])).join();
  }

  @Then("^the response code is (\\d+)$")
  public void itWillVerifyTheResponseCode(int responseCode) {
    assertThat(response.getStatus()).isEqualTo(responseCode);
  }

  @Then("^the following accounts now exist:$")
  public void itWillVerifyTheFollowingCountsExist(DataTable accountTable) {
    Client client = new JerseyClientBuilder().build();

    List<Account> accounts = client.target(ACCOUNT_PATH_SUPPLIER.get()).request().get(ACCOUNT_TYPE_REF);

    List<Map<String, String>> accountEntries = accountTable.asMaps();
    IntStream.range(0, accountEntries.size())
        .forEach(idx -> {
          Account actualAccount = accounts.get(idx);
          int expectedAccountId = Optional.ofNullable(accountEntries.get(idx).get("alias"))
              .map(accountAliasToIdMap::get)
              .orElse(actualAccount.getId());

          Account expectedAccount = Account.builder()
              .from(createAccountRequestFromProps(accountEntries.get(idx)))
              .id(expectedAccountId)
              .build();

          assertThat(actualAccount).isEqualTo(expectedAccount);
        });
  }

  @Then("^the following transfers now exist:$")
  public void itWillVerifyTheFollowingTransfersExist(DataTable transferTable) {
    Client client = new JerseyClientBuilder().build();

    List<Transfer> transfers = client.target(TRANSFER_PATH_SUPPLIER.get()).request().get(TRANSFER_TYPE_REF);

    List<Map<String, String>> transferEntries = transferTable.asMaps();
    IntStream.range(0, transferEntries.size())
        .forEach(idx -> {
          Transfer actualTransfer = transfers.get(idx);
          Transfer expectedTransfer = Transfer.builder()
              .from(createTransferRequestFromProps(transferEntries.get(idx)))
              .id(actualTransfer.getId())
              .build();

          assertThat(actualTransfer).isEqualTo(expectedTransfer);
        });
  }

  private Response createAccountFromProps(Map<String, String> accountProps) {
    return executePost(ACCOUNT_PATH_SUPPLIER.get(), createAccountRequestFromProps(accountProps));
  }

  private Response createTransferFromProps(Map<String, String> transferProps) {
    return executePost(TRANSFER_PATH_SUPPLIER.get(), createTransferRequestFromProps(transferProps));
  }

  private <T> Response executePost(String path, T entity) {
    return client.target(path).request().post(Entity.entity(entity, MediaType.APPLICATION_JSON));
  }


  private AccountCreationRequest createAccountRequestFromProps(Map<String, String> accountProps) {
    int customerId = Integer.parseInt(accountProps.get("customer_id"));
    CurrencyCode currencyCode = CurrencyCode.getByCode(accountProps.get("currency_code"));
    Optional<BigDecimal> balance = Optional.ofNullable(accountProps.get("balance")).map(BigDecimal::new);
    Money startingBalance = balance.map(amount -> Money.of(currencyCode, amount)).orElseGet(() -> Money.of(currencyCode));

    return AccountCreationRequest.builder()
        .customerId(customerId)
        .balance(startingBalance)
        .build();
  }


  private TransferRequest createTransferRequestFromProps(Map<String, String> accountProps) {
    int fromAccountId = accountAliasToIdMap.get(accountProps.get("from_account_alias"));
    int toAccountId = accountAliasToIdMap.get(accountProps.get("to_account_alias"));
    CurrencyCode currencyCode = CurrencyCode.getByCode(accountProps.get("currency_code"));
    BigDecimal amount = new BigDecimal(accountProps.get("amount"));
    Money startingBalance = Money.of(currencyCode, amount);

    return TransferRequest.builder()
        .fromAccountId(fromAccountId)
        .toAccountId(toAccountId)
        .amount(startingBalance)
        .build();
  }
}
