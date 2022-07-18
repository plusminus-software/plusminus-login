package software.plusminus.login.controller;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import software.plusminus.authentication.annotation.Public;
import software.plusminus.authentication.service.AuthenticationService;
import software.plusminus.authentication.service.Authenticator;
import software.plusminus.login.exception.IncorrectCredentialsException;
import software.plusminus.login.service.LoginService;
import software.plusminus.login.util.CookieUtil;
import software.plusminus.security.Security;

import java.util.Map;
import java.util.function.Predicate;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;

@Public
@Controller
public class LoginController {

    public static final String RELATIVE_URI_REGEX = "^(?!www\\.|(?:http|ftp)s?://|[A-Za-z]:\\\\|//).*";

    private LoginService loginService;
    private AuthenticationService authenticationService;

    @SuppressFBWarnings(value = "SPRING_UNVALIDATED_REDIRECT",
            justification = "False-positive: the redirect is validated with @Pattern annotation")
    @PostMapping(path = "/login", produces = "text/html")
    public String loginPage(HttpServletResponse response,
                            @Email String email,
                            String password,
                            @Pattern(regexp = RELATIVE_URI_REGEX) @RequestParam(required = false) String redirect,
                            Model model) {
        Security security = loginService.login(email, password);
        if (security == null) {
            model.addAttribute("error", "Invalid username or password!");
            return "index"; //TODO get login page templateName instead of "index"
        }
        Map.Entry<Authenticator, String> token = getToken(security, a -> a.tokenPlace().getCookiesKey() != null);
        setCookies(response, token);
        if (redirect == null) {
            return "redirect:/";
        }
        return "redirect:" + redirect;
    }

    @PostMapping(path = "/login", produces = "application/json")
    @ResponseBody
    public String loginApi(HttpServletResponse response,
                           @Email String email,
                           String password) {
        Security security = loginService.login(email, password);
        if (security == null) {
            throw new IncorrectCredentialsException("Incorrect credentials");
        }
        Map.Entry<Authenticator, String> token = getToken(security, a -> true);
        if (token.getKey().tokenPlace().getCookiesKey() != null) {
            setCookies(response, token);
        }
        if (token.getKey().tokenPlace().getHeadersKey() == null) {
            return "";
        }
        return token.getValue();
    }

    private Map.Entry<Authenticator, String> getToken(Security security, Predicate<Authenticator> predicate) {
        Map.Entry<Authenticator, String> token = authenticationService.provideToken(security, predicate);
        if (token == null) {
            throw new SecurityException("Can't provide token for user " + security.getUsername() + " on login");
        }
        return token;
    }

    private void setCookies(HttpServletResponse response, Map.Entry<Authenticator, String> token) {
        CookieUtil.create(response,
                token.getKey().tokenPlace().getCookiesKey(),
                token.getValue(),
                "localhost");
    }

}
