package integration;

import com.jasongermaine.RevolutTestConfiguration;
import com.jasongermaine.revoluttest.RevolutTestApplication;

import cucumber.api.java.After;
import cucumber.api.java.Before;
import io.dropwizard.testing.DropwizardTestSupport;

public class ApplicationSupport {
  static final DropwizardTestSupport<RevolutTestConfiguration> SUPPORT =
      new DropwizardTestSupport<>(RevolutTestApplication.class,
          "src/test/resources/test-config.yml");

  @Before
  public void beforeScenario() throws Exception {
    SUPPORT.before();
  }

  @After
  public void afterScenario() {
    SUPPORT.after();
  }
}
