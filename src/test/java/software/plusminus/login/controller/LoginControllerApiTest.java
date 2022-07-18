package software.plusminus.login.controller;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import software.plusminus.authentication.model.TokenPlace;
import software.plusminus.authentication.service.Authenticator;
import software.plusminus.security.Loginer;
import software.plusminus.security.Security;
import software.plusminus.test.IntegrationTest;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static software.plusminus.check.Checks.check;

@AutoConfigureMockMvc
public class LoginControllerApiTest extends IntegrationTest {

    private String email = "test@email.com";
    private String password = "test-password";
    private Security security = Security.builder().username(email).build();
    private String token = "test-token";

    @Autowired
    private MockMvc mvc;

    @MockBean
    private Loginer loginer;
    @MockBean
    private Authenticator authenticator;

    @Before
    public void setUp() {
        when(loginer.login(email, password)).thenReturn(security);
        when(authenticator.provideToken(security)).thenReturn(token);
        when(authenticator.authenticate(token)).thenReturn(security);
    }

    @Test
    public void loginWithCookiesOnly() throws Exception {
        String cookiesKey = "testCookiesKey";
        when(authenticator.tokenPlace()).thenReturn(TokenPlace.builder()
                .cookiesKey(cookiesKey)
                .build());
        
        MockHttpServletResponse response = mvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .param("email", email)
                .param("password", password))
                .andReturn()
                .getResponse();
        
        check(response.getStatus()).is(HttpStatus.OK.value());
        check(response.getContentAsString()).is("");
        check(response.getCookies()).hasSize(1);
        check(response.getCookies()[0].getName()).is(cookiesKey);
        check(response.getCookies()[0].getValue()).is(token);
    }
    
    @Test
    public void loginWithHeadersOnly() throws Exception {
        String headersKey = "testHeadersKey";
        when(authenticator.tokenPlace()).thenReturn(TokenPlace.builder()
                .headersKey(headersKey)
                .build());
        
        MockHttpServletResponse response = mvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .param("email", email)
                .param("password", password))
                .andReturn()
                .getResponse();
        
        check(response.getStatus()).is(HttpStatus.OK.value());
        check(response.getContentAsString()).is(token);
        check(response.getCookies()).isEmpty();
    }
    
    @Test
    public void loginWithCookiesAndHeaders() throws Exception {
        String headersKey = "testHeadersKey";
        String cookiesKey = "testCookiesKey";
        when(authenticator.tokenPlace()).thenReturn(TokenPlace.builder()
                .headersKey(headersKey)
                .cookiesKey(cookiesKey)
                .build());
        
        MockHttpServletResponse response = mvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .param("email", email)
                .param("password", password))
                .andReturn()
                .getResponse();
        
        check(response.getStatus()).is(HttpStatus.OK.value());
        check(response.getContentAsString()).is(token);
        check(response.getCookies()).hasSize(1);
        check(response.getCookies()[0].getName()).is(cookiesKey);
        check(response.getCookies()[0].getValue()).is(token);
    }

    @Test
    public void badCredentials() throws Exception {
        String headersKey = "testHeadersKey";
        when(authenticator.tokenPlace()).thenReturn(TokenPlace.builder()
                .headersKey(headersKey)
                .build());

        MockHttpServletResponse response = mvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .param("email", "bad@email.com")
                .param("password", "bad-password"))
                .andReturn()
                .getResponse();

        check(response.getStatus()).is(HttpStatus.UNAUTHORIZED.value());
    }
}