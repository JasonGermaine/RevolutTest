package com.jasongermaine.revoluttest;

import java.util.List;
import java.util.function.Supplier;

import org.jdbi.v3.core.HandleConsumer;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.transaction.SerializableTransactionRunner;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.jasongermaine.RevolutTestConfiguration;
import com.jasongermaine.revoluttest.account.AccountCreationCallback;
import com.jasongermaine.revoluttest.account.AccountCreationCallback.AccountCreationCallbackFactory;
import com.jasongermaine.revoluttest.core.Account;
import com.jasongermaine.revoluttest.core.Transfer;
import com.jasongermaine.revoluttest.mapping.AccountMapper;
import com.jasongermaine.revoluttest.mapping.TransferMapper;
import com.jasongermaine.revoluttest.resources.AccountResource;
import com.jasongermaine.revoluttest.resources.TransferResource;
import com.jasongermaine.revoluttest.transfer.TransferExecutionCallback;
import com.jasongermaine.revoluttest.transfer.TransferExecutionCallback.TransferExecutionCallbackFactory;

import io.dropwizard.Application;
import io.dropwizard.jdbi3.JdbiFactory;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class RevolutTestApplication extends Application<RevolutTestConfiguration> {

  public static void main(String[] args) throws Exception {
    new RevolutTestApplication().run(args);
  }

  @Override
  public void initialize(Bootstrap<RevolutTestConfiguration> bootstrap) {
  }

  @Override
  public void run(RevolutTestConfiguration configuration, Environment environment) throws Exception {
    JdbiFactory factory = new JdbiFactory();
    Jdbi jdbi = factory.build(environment, configuration.getDataSourceFactory(), "h2");
    jdbi.setTransactionHandler(new SerializableTransactionRunner());
    jdbi.configure(SerializableTransactionRunner.Configuration.class, config -> config.setSerializationFailureSqlState("HYT00"));
    jdbi.useHandle((HandleConsumer<Exception>) handle -> {
      String creationScript = Resources.toString(Resources.getResource("create_tables.sql"), Charsets.UTF_8);
      handle.createScript(creationScript).execute();
    });

    Injector injector = Guice.createInjector(new ApplicationModule(jdbi));
    jdbi.registerRowMapper(injector.getInstance(AccountMapper.class));
    environment.jersey().register(injector.getInstance(AccountResource.class));
    environment.jersey().register(injector.getInstance(TransferResource.class));
  }

  private static final class ApplicationModule extends AbstractModule {
    private final Jdbi jdbi;

    private ApplicationModule(Jdbi jdbi) {
      this.jdbi = jdbi;
    }

    @Override
    protected void configure() {
      bind(Jdbi.class).toInstance(jdbi);
      bind(AccountResource.class);
      bind(TransferResource.class);

      install(new FactoryModuleBuilder()
          .implement(TransferExecutionCallback.class, TransferExecutionCallback.class)
          .build(TransferExecutionCallbackFactory.class));
      install(new FactoryModuleBuilder()
          .implement(AccountCreationCallback.class, AccountCreationCallback.class)
          .build(AccountCreationCallbackFactory.class));
    }

    @Provides
    public Supplier<List<Transfer>> providesTransferSupplier(TransferMapper mapper) {
      return () -> {
        try {
          return jdbi.withHandle(handle -> handle.createQuery("SELECT * FROM transfer").map(mapper).list());
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
      };
    }

    @Provides
    public Supplier<List<Account>> providesAccountSupplier(AccountMapper mapper) {
      return () -> {
        try {
          return jdbi.withHandle(handle -> handle.createQuery("SELECT * FROM account").map(mapper).list());
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
      };
    }
  }
}
