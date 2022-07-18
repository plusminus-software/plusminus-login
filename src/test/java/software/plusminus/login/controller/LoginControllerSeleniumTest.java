package software.plusminus.login.controller;

import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import software.plusminus.authentication.model.TokenPlace;
import software.plusminus.authentication.service.Authenticator;
import software.plusminus.security.Loginer;
import software.plusminus.security.Security;
import software.plusminus.selenium.model.WebTestOptions;
import software.plusminus.test.SeleniumTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

public class LoginControllerSeleniumTest extends SeleniumTest {

    private String email = "test@email.com";
    private String password = "test-password";
    private Security security = Security.builder().username(email).build();
    private String token = "test-token";
    
    @MockBean
    private Loginer loginer;
    @MockBean
    private Authenticator authenticator;

    @Override
    protected WebTestOptions options() {
        return super.options()
                .logsFilter(logEntry -> !logEntry.getMessage().contains("401") 
                        && !logEntry.getMessage().contains("favicon.ico"));
    }

    @Override
    protected String url() {
        return "/login";
    }
    
    @Before
    public void setUp() {
        when(loginer.login(email, password)).thenReturn(security);
        when(authenticator.tokenPlace()).thenReturn(TokenPlace.builder().cookiesKey("test-token").build());
        when(authenticator.provideToken(security)).thenReturn(token);
        when(authenticator.authenticate(token)).thenReturn(security);
        super.setUp();
        driver().manage().deleteAllCookies();
    }
    
    @Test
    public void successfulLogin() {
        find("#email").one().sendKeys(email);
        find("#password").one().sendKeys(password);
        find("#submit").one().click();
        
        find().byText("div", "Logged in").one();
    }

    @Test
    public void badCredentials() {
        find("#email").one().sendKeys("bad@email.com");
        find("#password").one().sendKeys("bad-password");
        find("#submit").one().click();

        find().byText("div", "Invalid username or password!").one();
    }

    @Test
    public void indexPageIsNotPublic() {
        go("/");
        String body = find("body").one().getText();
        assertThat(body).doesNotContain("Index page");
        assertThat(body).contains("Unauthorized");
    }
    
    @Test
    public void explicitRedirect() {
        driver().get(buildUrl("/login?redirect=explicit-redirect"));
        find("#email").one().sendKeys(email);
        find("#password").one().sendKeys(password);
        find("#submit").one().click();
        
        assertThat(driver().getCurrentUrl()).endsWith("explicit-redirect");
    }
}