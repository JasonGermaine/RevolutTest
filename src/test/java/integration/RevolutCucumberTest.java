package integration;

import org.junit.runner.RunWith;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;

@RunWith(Cucumber.class)
@CucumberOptions(plugin = {"pretty"}, features = {"src/test/resources/add_account.feature", "src/test/resources/transfer_money.feature"})
public class RevolutCucumberTest {

}
