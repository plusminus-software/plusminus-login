package software.plusminus.login.controller;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.Cookie;
import org.springframework.boot.test.mock.mockito.MockBean;
import software.plusminus.authentication.model.TokenPlace;
import software.plusminus.authentication.service.Authenticator;
import software.plusminus.security.Loginer;
import software.plusminus.security.Security;
import software.plusminus.selenium.model.WebTestOptions;
import software.plusminus.test.SeleniumTest;

import static org.mockito.Mockito.when;
import static software.plusminus.check.Checks.check;

public class LogoutControllerSeleniumTest extends SeleniumTest {

    private String cookiesKey = "test-cookies-key";
    private String token = "test-token";
    private String email = "test@email.com";
    private Security security = Security.builder().username(email).build();

    @SuppressWarnings("PMD.UnusedPrivateField")
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

    @Before
    public void setUp() {
        when(authenticator.tokenPlace()).thenReturn(TokenPlace.builder().cookiesKey(cookiesKey).build());
        when(authenticator.authenticate(token)).thenReturn(security);
        super.setUp();
        driver().manage().addCookie(new Cookie(cookiesKey, token));
        driver().navigate().refresh();
    }

    @Override
    protected String url() {
        return "/";
    }

    @Test
    public void logoutClearsCookies() {
        find("#logout").one().click();
        find("#logout").none();
        check(driver().manage().getCookies()).isEmpty();
    }

    @Test
    public void logoutRedirectsToIndexPage() {
        find("#logout").one().click();
        find("#logout").none();
        check(driver().getCurrentUrl()).is(buildUrl("/"));
    }
}