import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        plugin = {"pretty", "json:target/jsonReports/cucumber.json"},
        glue = {"/com/test/apiservice"},
        tags = "@focus and not @ignore")
public class RunCucumberTest {
}